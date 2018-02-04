package pl.merskip.qemu;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessHandlerFactory;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.xdebugger.XDebugSession;
import com.jetbrains.cidr.cpp.cmake.model.CMakeConfiguration;
import com.jetbrains.cidr.cpp.cmake.model.CMakeTarget;
import com.jetbrains.cidr.cpp.execution.CMakeBuildConfigurationHelper;
import com.jetbrains.cidr.cpp.execution.debugger.backend.GDBDriverConfiguration;
import com.jetbrains.cidr.cpp.toolchains.CPPToolchains;
import com.jetbrains.cidr.execution.debugger.remote.CidrRemoteDebugParameters;
import com.jetbrains.cidr.execution.debugger.remote.CidrRemoteGDBDebugProcess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.OutputStream;
import java.util.Collections;

public abstract class QemuProfileStateBase extends CommandLineState {

    protected ProcessHandler mainProcess;
    protected ProcessHandler cmakeProcess;
    protected ProcessHandler runQemuProcess;

    protected QemuProfileStateBase(ExecutionEnvironment environment) {
        super(environment);
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        CMakeConfiguration cmakeConfiguration = getDefaultCMakeConfiguration();
        if (cmakeConfiguration == null) {
            throw new ExecutionException("No default configuration or target for CMake");
        }

        final File productFile = cmakeConfiguration.getProductFile();
        if (productFile == null) {
            throw new ExecutionException("Product file for default configuration is null");
        }

        onRunQemu();
        cmakeProcess = new CMakeBuildProcess(getEnvironment().getProject(), cmakeConfiguration);
        cmakeProcess.addProcessListener(new ProcessAdapter() {

            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
                if (event.getExitCode() != 0) {
                    mainProcess.destroyProcess();
                }

                try {
                    performNextActionAfter(event.getProcessHandler());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    mainProcess.destroyProcess();
                }
            }

            private void performNextActionAfter(ProcessHandler terminatedProcess) throws ExecutionException {
                if (terminatedProcess == cmakeProcess) {

                    GeneralCommandLine commandLine = createRunQemuCommandLine(productFile, isDebug());
                    runQemuProcess = ProcessHandlerFactory.getInstance().createProcessHandler(commandLine);
                    runQemuProcess.addProcessListener(this);
                    runQemuProcess.startNotify(); // Without this events for process listener will not perform. Is it bug?

                }
                else if (terminatedProcess == runQemuProcess) {
                    mainProcess.destroyProcess();
                }
                else {
                    throw new IllegalArgumentException("Unknown terminated process");
                }
            }
        });

        mainProcess = new ProcessHandler() {

            @Override
            protected void destroyProcessImpl() {
                boolean success = bothProcessReturnsSuccess();
                destroyBothProcess();
                notifyProcessTerminated(success ? 0 : 1);
            }

            private boolean bothProcessReturnsSuccess() {
                if (cmakeProcess != null && runQemuProcess != null) {
                    Integer cmakeExitCode = cmakeProcess.getExitCode();
                    Integer qemuExitCode = runQemuProcess.getExitCode();
                    if (cmakeExitCode != null && qemuExitCode != null) {
                        return cmakeExitCode == 0 && qemuExitCode == 0;
                    }
                }
                return false;
            }

            private void destroyBothProcess() {
                if (cmakeProcess != null && !cmakeProcess.isProcessTerminated()) {
                    cmakeProcess.destroyProcess();
                }
                if (runQemuProcess != null && !runQemuProcess.isProcessTerminated()) {
                    runQemuProcess.destroyProcess();
                }
            }

            @Override
            protected void detachProcessImpl() {
                notifyProcessDetached();
            }

            @Override
            public boolean detachIsDefault() {
                return false;
            }

            @Nullable
            @Override
            public OutputStream getProcessInput() {
                return null;
            }
        };
        return mainProcess;
    }

    protected void onRunQemu() throws ExecutionException {

    }

    abstract boolean isDebug();

    protected CidrRemoteGDBDebugProcess createRemoteGDBProcess(String remoteCommand, String symbolFile, XDebugSession xDebugSession) throws ExecutionException {
        CPPToolchains.Toolchain toolchain = CPPToolchains.getInstance().getDefaultToolchain();
        if (toolchain == null) {
            throw new IllegalStateException("No default toolchain");
        }
        GDBDriverConfiguration debuggerConfiguration = new GDBDriverConfiguration(getEnvironment().getProject(), toolchain);
        CidrRemoteDebugParameters debugParameters = new CidrRemoteDebugParameters(remoteCommand, symbolFile,"", Collections.emptyList());
        TextConsoleBuilder textConsoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(getEnvironment().getProject());

        return new CidrRemoteGDBDebugProcess(debuggerConfiguration, debugParameters, xDebugSession, textConsoleBuilder);
    }

    @Nullable
    protected CMakeConfiguration getDefaultCMakeConfiguration() {
        CMakeBuildConfigurationHelper configurationHelper = new CMakeBuildConfigurationHelper(getEnvironment().getProject());
        CMakeTarget defaultTarget = configurationHelper.getTargets().get(1); // TODO: Anything different
        if (defaultTarget == null) {
            return null;
        }
        return configurationHelper.getDefaultConfiguration(defaultTarget);
    }

    protected GeneralCommandLine createRunQemuCommandLine(File cdromFile, boolean forDebugger) {
        GeneralCommandLine commandLine = new GeneralCommandLine("qemu-system-x86_64");
        commandLine.addParameters("-cdrom", cdromFile.getAbsolutePath());
        if (forDebugger) {
            commandLine.addParameters("-s", "-S");
        }
        return commandLine;
    }
}

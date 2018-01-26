package pl.merskip.qemu;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.xdebugger.XDebugSession;
import com.jetbrains.cidr.cpp.execution.debugger.backend.GDBDriverConfiguration;
import com.jetbrains.cidr.cpp.toolchains.CPPToolchains;
import com.jetbrains.cidr.execution.debugger.CidrDebugProcess;
import com.jetbrains.cidr.execution.debugger.remote.CidrRemoteDebugParameters;
import com.jetbrains.cidr.execution.debugger.remote.CidrRemoteGDBDebugProcess;
import com.jetbrains.cidr.execution.testing.CidrLauncher;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class QemuLauncher extends CidrLauncher {

    private Project project;

    public QemuLauncher(Project project) {
        this.project = project;
    }

    @Override
    protected ProcessHandler createProcess(@NotNull CommandLineState commandLineState) throws ExecutionException {
        GeneralCommandLine commandLine = createQemuCommandLine();
        return new BaseOSProcessHandler(commandLine.createProcess(), commandLine.getPreparedCommandLine(), commandLine.getCharset());
    }

    @NotNull
    @Override
    protected CidrDebugProcess createDebugProcess(@NotNull CommandLineState commandLineState, @NotNull XDebugSession xDebugSession) throws ExecutionException {
        Process qemuProcess = createQemuCommandLine().createProcess();
        CidrRemoteGDBDebugProcess remoteGDBProcess = createRemoteGDBProcess("tcp:localhost:1234",
                "/home/merskip/Workspace/PiomekOS/cmake-build-debug/kernel.bin", xDebugSession);

        remoteGDBProcess.getProcessHandler().addProcessListener(new ProcessAdapter() {

            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
                qemuProcess.destroy();
            }
        });

        return remoteGDBProcess;
    }

    private CidrRemoteGDBDebugProcess createRemoteGDBProcess(String remoteCommand, String symbolFile, XDebugSession xDebugSession) throws ExecutionException {
        CPPToolchains.Toolchain toolchain = CPPToolchains.getInstance().getDefaultToolchain();
        if (toolchain == null) {
            throw new IllegalStateException("No default toolchain");
        }
        GDBDriverConfiguration debuggerConfiguration = new GDBDriverConfiguration(project, toolchain);
        CidrRemoteDebugParameters debugParameters = new CidrRemoteDebugParameters(remoteCommand, symbolFile,"", Collections.emptyList());
        TextConsoleBuilder textConsoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(project);
        return new CidrRemoteGDBDebugProcess(debuggerConfiguration, debugParameters, xDebugSession, textConsoleBuilder);
    }

    private GeneralCommandLine createQemuCommandLine() {
        return new GeneralCommandLine("qemu-system-x86_64", "-s", "-S", "-cdrom", "/home/merskip/Workspace/PiomekOS/cmake-build-debug/PiomekOS.iso");
    }

    @NotNull
    @Override
    protected Project getProject() {
        return project;
    }
}

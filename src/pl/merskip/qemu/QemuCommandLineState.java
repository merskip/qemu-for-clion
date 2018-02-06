package pl.merskip.qemu;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.jetbrains.cidr.cpp.cmake.model.CMakeConfiguration;
import com.jetbrains.cidr.cpp.cmake.model.CMakeTarget;
import com.jetbrains.cidr.cpp.execution.CMakeBuildConfigurationHelper;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class QemuCommandLineState extends CommandLineState {

    private QemuRunConfiguration configuration;

    private ProcessHandler mainProcess;
    private ProcessHandler cmakeProcess;
    private ProcessHandler qemuProcess;

    private boolean processFailure = false;

    protected QemuCommandLineState(ExecutionEnvironment environment, QemuRunConfiguration configuration) {
        super(environment);
        this.configuration = configuration;
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        switch (configuration.getDiskImageSource()) {
            case File:
                onDiskImageReady(new File(configuration.getCdromFile()));
                break;
            case CMakeTarget:
                cmakeProcess = startBuildCMake();
                break;
        }

        mainProcess = new NopProcessHandler() {

            @Override
            protected void destroyProcessImpl() {
                destroyBothProcess();
                notifyProcessTerminated(processFailure ? 1 : 0);
            }

            private void destroyBothProcess() {
                if (cmakeProcess != null && !cmakeProcess.isProcessTerminated()) {
                    cmakeProcess.destroyProcess();
                }
                if (qemuProcess != null && !qemuProcess.isProcessTerminated()) {
                    qemuProcess.destroyProcess();
                }
            }
        };
        return mainProcess;
    }

    private void onDiskImageReady(File diskImage) {
        GeneralCommandLine commandLine = createRunQemuCommandLine(diskImage);

        try {
            qemuProcess = ProcessHandlerFactory.getInstance().createProcessHandler(commandLine);
            qemuProcess.addProcessListener(new ProcessAdapter() {
                @Override
                public void processTerminated(@NotNull ProcessEvent event) {
                    mainProcess.destroyProcess();
                }
            });
            qemuProcess.startNotify(); // Without this events for process listener will not perform. Is it bug?
        } catch (ExecutionException e) {
            e.printStackTrace();
            onProcessFailure();
        }
    }

    private void onProcessFailure() {
        processFailure = true;

        if (qemuProcess != null) {
            qemuProcess.destroyProcess();
        }
        mainProcess.destroyProcess();
    }

    private ProcessHandler startBuildCMake() throws ExecutionException {
        CMakeTarget cmakeTarget = configuration.getCmakeTarget();
        if (cmakeTarget == null) {
            throw new ExecutionException("No selected CMake target");
        }
        CMakeBuildConfigurationHelper configurationHelper = new CMakeBuildConfigurationHelper(getEnvironment().getProject());
        CMakeConfiguration configuration = configurationHelper.getDefaultConfiguration(cmakeTarget);
        if (configuration == null) {
            throw new ExecutionException("No found CMake configuration for selected target");
        }

        CMakeBuildProcess process = new CMakeBuildProcess(getEnvironment().getProject(), configuration);
        process.addProcessListener(new ProcessAdapter() {

            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
                if (event.getExitCode() != 0) {
                    onProcessFailure();
                    return;
                }

                onDiskImageReady(configuration.getProductFile());
            }
        });
        return process;
    }


    private GeneralCommandLine createRunQemuCommandLine(File diskImageFile) {
        GeneralCommandLine commandLine = new GeneralCommandLine("qemu-system-x86_64");
        commandLine.addParameters("-cdrom", diskImageFile.getAbsolutePath());
        return commandLine;
    }
}

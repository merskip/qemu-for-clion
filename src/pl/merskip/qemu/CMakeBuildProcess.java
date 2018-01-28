package pl.merskip.qemu;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.jetbrains.cidr.cpp.cmake.model.CMakeConfiguration;
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfiguration.BuildAndRunConfigurations;
import com.jetbrains.cidr.cpp.execution.build.CMakeBuild;
import com.jetbrains.cidr.cpp.toolchains.CMake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;

public class CMakeBuildProcess extends ProcessHandler {

    @Nullable
    private ProcessHandler cmakeProcess;

    CMakeBuildProcess(@NotNull Project project, @NotNull CMakeConfiguration configuration) {
        CMakeBuild.build(project, new BuildAndRunConfigurations(configuration), new ProcessAdapter() {
            @Override
            public void startNotified(@NotNull ProcessEvent event) {
                cmakeProcess = event.getProcessHandler();
                startNotify();
            }

            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
                notifyProcessTerminated(event.getExitCode());
            }

            @Override
            public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                notifyTextAvailable(event.getText(), outputType);
            }
        });
    }

    @Override
    protected void destroyProcessImpl() {
        if (cmakeProcess != null) {
            cmakeProcess.destroyProcess();
        }
    }

    @Override
    protected void detachProcessImpl() {
        if (cmakeProcess != null) {
            cmakeProcess.detachProcess();
        }
    }

    @Override
    public boolean detachIsDefault() {
        return cmakeProcess != null && cmakeProcess.detachIsDefault();
    }

    @Nullable
    @Override
    public OutputStream getProcessInput() {
        return null;
    }
}

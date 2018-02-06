package pl.merskip.qemu;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.jetbrains.cidr.cpp.cmake.model.CMakeTarget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class QemuRunConfiguration extends RunConfigurationBase {

    public enum DiskImageSource {
        File,
        CMakeTarget
    }

    private DiskImageSource diskImageSource = DiskImageSource.File;
    private String cdromFile;
    private CMakeTarget cmakeTarget;

    QemuRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new QemuRunConfigurationEditor(getProject());
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
        if (executor instanceof DefaultRunExecutor) {
            return new QemuCommandLineState(environment) {
            };
        }
        else {
            return null;
        }
    }

    // Getters/Setters

    public DiskImageSource getDiskImageSource() {
        return diskImageSource;
    }

    public void setDiskImageSource(DiskImageSource diskImageSource) {
        this.diskImageSource = diskImageSource;
    }

    public String getCdromFile() {
        return cdromFile;
    }

    public void setCdromFile(String cdromFile) {
        this.cdromFile = cdromFile;
    }

    public CMakeTarget getCmakeTarget() {
        return cmakeTarget;
    }

    public void setCmakeTarget(CMakeTarget cmakeTarget) {
        this.cmakeTarget = cmakeTarget;
    }
}

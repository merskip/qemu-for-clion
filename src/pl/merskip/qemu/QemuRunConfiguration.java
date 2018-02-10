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

    private String qemuCommand = "qemu-system-x86_64";
    private DiskImageSource diskImageSource = DiskImageSource.File;
    @NotNull private String cdromFile = "";
    @Nullable private CMakeTarget cmakeTarget;
    private boolean enableGDB = false;
    private int tcpPort = 1234;
    private boolean waitForDebugger = true;
    private  boolean deamonize = false;

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
            return new QemuCommandLineState(environment, this);
        }
        else {
            return null;
        }
    }

    // Getters/Setters

    public String getQemuCommand() {
        return qemuCommand;
    }

    public void setQemuCommand(String qemuCommand) {
        this.qemuCommand = qemuCommand;
    }

    public DiskImageSource getDiskImageSource() {
        return diskImageSource;
    }

    public void setDiskImageSource(DiskImageSource diskImageSource) {
        this.diskImageSource = diskImageSource;
    }

    @NotNull
    public String getCdromFile() {
        return cdromFile;
    }

    public void setCdromFile(@NotNull String cdromFile) {
        this.cdromFile = cdromFile;
    }

    @Nullable
    public CMakeTarget getCmakeTarget() {
        return cmakeTarget;
    }

    public void setCmakeTarget(@Nullable CMakeTarget cmakeTarget) {
        this.cmakeTarget = cmakeTarget;
    }

    public boolean isEnableGDB() {
        return enableGDB;
    }

    public void setEnableGDB(boolean enableGDB) {
        this.enableGDB = enableGDB;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public void setTcpPort(int tcpPort) {
        this.tcpPort = tcpPort;
    }

    public boolean isWaitForDebugger() {
        return waitForDebugger;
    }

    public void setWaitForDebugger(boolean waitForDebugger) {
        this.waitForDebugger = waitForDebugger;
    }

    public boolean isDeamonize() {
        return deamonize;
    }

    public void setDeamonize(boolean deamonize) {
        this.deamonize = deamonize;
    }
}

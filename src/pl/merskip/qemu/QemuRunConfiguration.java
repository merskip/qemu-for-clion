package pl.merskip.qemu;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.NopProcessHandler;
import com.intellij.execution.runners.DebuggableRunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.jetbrains.cidr.execution.CidrCommandLineState;
import com.jetbrains.cidr.execution.CidrRunProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;


public class QemuRunConfiguration extends RunConfigurationBase implements CidrRunProfile {

    private String cdromFile;

    QemuRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new QemuRunConfigurationEditor(getProject());
    }

    public String getCdromFile() {
        return cdromFile;
    }

    public void setCdromFile(String cdromFile) {
        this.cdromFile = cdromFile;
    }

    @Nullable
    @Override
    public CidrCommandLineState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) {
        QemuLauncher qemuLauncher = new QemuLauncher(getProject());
        return new CidrCommandLineState(executionEnvironment, qemuLauncher);
    }
}

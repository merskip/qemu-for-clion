package pl.merskip.qemu;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.NopProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class QemuRunConfiguration extends RunConfigurationBase {

    private String cdromFile;

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
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env) throws ExecutionException {
        GeneralCommandLine commandLine = new GeneralCommandLine("qemu-system-x86_64", "-s", "-S", "-cdrom", cdromFile);
        Process process = commandLine.createProcess();

        System.out.println(process.isAlive());

        NopProcessHandler nopProcessHandler = new NopProcessHandler();
        DefaultExecutionResult executionResult = new DefaultExecutionResult(nopProcessHandler);
        nopProcessHandler.destroyProcess();

        return (executor1, programRunner) -> executionResult;
    }

    public String getCdromFile() {
        return cdromFile;
    }

    public void setCdromFile(String cdromFile) {
        this.cdromFile = cdromFile;
    }
}

package pl.merskip.qemu;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class QemuConfigurationType extends ConfigurationTypeBase {

    protected QemuConfigurationType() {
        super("qemu", "QEMU", "QEMU support configuration", AllIcons.RunConfigurations.Application);
        addFactory(new ConfigurationFactory(this) {
            @NotNull
            @Override
            public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                return new QemuRunConfiguration(project, this, getDisplayName());
            }
        });
    }
}

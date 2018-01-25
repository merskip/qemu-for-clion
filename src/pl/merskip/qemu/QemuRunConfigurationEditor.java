package pl.merskip.qemu;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class QemuRunConfigurationEditor extends SettingsEditor<QemuRunConfiguration> {

    @Override
    protected void resetEditorFrom(@NotNull QemuRunConfiguration qemuRunConfiguration) {

    }

    @Override
    protected void applyEditorTo(@NotNull QemuRunConfiguration qemuRunConfiguration) throws ConfigurationException {

    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return new JPanel();
    }
}

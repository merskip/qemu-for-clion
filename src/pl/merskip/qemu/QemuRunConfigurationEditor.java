package pl.merskip.qemu;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class QemuRunConfigurationEditor extends SettingsEditor<QemuRunConfiguration> {

    private Project project;
    private JPanel panel;
    private TextFieldWithBrowseButton cdromFileField;

    public QemuRunConfigurationEditor(Project project) {
        this.project = project;
    }

    @Override
    protected void resetEditorFrom(@NotNull QemuRunConfiguration qemuRunConfiguration) {
        cdromFileField.setText(qemuRunConfiguration.getCdromFile());
    }

    @Override
    protected void applyEditorTo(@NotNull QemuRunConfiguration qemuRunConfiguration) {
        qemuRunConfiguration.setCdromFile(cdromFileField.getText());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        cdromFileField.addBrowseFolderListener(
                new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFileDescriptor(), project)
        );

        return panel;
    }
}

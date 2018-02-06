package pl.merskip.qemu;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.ListCellRendererWithRightAlignedComponent;
import com.jetbrains.cidr.cpp.cmake.model.CMakeTarget;
import com.jetbrains.cidr.cpp.execution.CMakeBuildConfigurationHelper;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

public class QemuRunConfigurationEditor extends SettingsEditor<QemuRunConfiguration> {

    private Project project;
    private JPanel panel;
    private TextFieldWithBrowseButton cdromFileField;
    private JRadioButton cdromRadioBtn;
    private JRadioButton cmakeTargetRadioBtn;
    private ComboBox<CMakeTarget> cmakeTargetComboBox;
    private JCheckBox enableGDBCheckBox;
    private JTextField tcpPortField;
    private JCheckBox waitForDebuggerCheckBox;
    private TextFieldWithBrowseButton qemuCommandField;

    public QemuRunConfigurationEditor(Project project) {
        this.project = project;
    }

    @Override
    protected void resetEditorFrom(@NotNull QemuRunConfiguration configuration) {
        qemuCommandField.setText(configuration.getQemuCommand());
        cdromFileField.setText(configuration.getCdromFile());
        cmakeTargetComboBox.setSelectedItem(configuration.getCmakeTarget());

        switch (configuration.getDiskImageSource()) {
            case File:
                cdromRadioBtn.setSelected(true);
                break;
            case CMakeTarget:
                cmakeTargetRadioBtn.setSelected(true);
                break;
        }

        enableGDBCheckBox.setSelected(configuration.isEnableGDB());
        tcpPortField.setText(String.valueOf(configuration.getTcpPort()));
        waitForDebuggerCheckBox.setSelected(configuration.isWaitForDebugger());
    }

    @Override
    protected void applyEditorTo(@NotNull QemuRunConfiguration configuration) {
        configuration.setQemuCommand(qemuCommandField.getText());
        configuration.setCdromFile(cdromFileField.getText());
        configuration.setCmakeTarget((CMakeTarget) cmakeTargetComboBox.getSelectedItem());

        if (cdromRadioBtn.isSelected()) {
            configuration.setDiskImageSource(QemuRunConfiguration.DiskImageSource.File);
        }
        else if (cmakeTargetRadioBtn.isSelected()) {
            configuration.setDiskImageSource(QemuRunConfiguration.DiskImageSource.CMakeTarget);
        }

        configuration.setEnableGDB(enableGDBCheckBox.isSelected());
        try {
            configuration.setTcpPort(Integer.parseInt(tcpPortField.getText()));
        } catch (NumberFormatException e) {
            // Nothing, just no change port in model
        }
        configuration.setWaitForDebugger(waitForDebuggerCheckBox.isSelected());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        qemuCommandField.addBrowseFolderListener(
                new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleLocalFileDescriptor(), project)
        );
        cdromFileField.addBrowseFolderListener(
                new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFileDescriptor(), project)
        );

        List<CMakeTarget> targets = new CMakeBuildConfigurationHelper(project).getTargets();
        //noinspection unchecked
        cmakeTargetComboBox.setModel(new ListComboBoxModel<>(targets));
        //noinspection unchecked,GtkPreferredJComboBoxRenderer
        cmakeTargetComboBox.setRenderer(new CMakeTargetCellRenderer());

        return panel;
    }

    class CMakeTargetCellRenderer extends ListCellRendererWithRightAlignedComponent<CMakeTarget> {

        @Override
        protected void customize(CMakeTarget cMakeTarget) {
            if (cMakeTarget != null) {
                setIcon(cMakeTarget.getIcon());
                setLeftText(cMakeTarget.getName());
            }
        }
    }
}

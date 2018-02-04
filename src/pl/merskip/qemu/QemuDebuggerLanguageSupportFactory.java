package pl.merskip.qemu;

import com.intellij.execution.configurations.RunProfile;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import com.jetbrains.cidr.execution.debugger.OCDebuggerEditorsProvider;
import com.jetbrains.cidr.execution.debugger.OCDebuggerLanguageSupportFactory;
import org.jetbrains.annotations.Nullable;

public class QemuDebuggerLanguageSupportFactory extends OCDebuggerLanguageSupportFactory {

    @Nullable
    @Override
    public XDebuggerEditorsProvider createEditor(RunProfile runProfile) {
        if (runProfile instanceof QemuRunConfiguration) {
            return new OCDebuggerEditorsProvider();
        }
        return super.createEditor(runProfile);
    }
}

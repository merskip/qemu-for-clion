package pl.merskip.qemu;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugProcessStarter;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import org.jetbrains.annotations.NotNull;


public class QemuDebugProfileState extends QemuProfileStateBase {


    protected QemuDebugProfileState(ExecutionEnvironment environment) {
        super(environment);
    }

    @Override
    protected void onRunQemu() throws ExecutionException {
                startGDB();
    }

    private void startGDB() throws ExecutionException {
        XDebuggerManager debuggerManager = XDebuggerManager.getInstance(getEnvironment().getProject());
        debuggerManager.startSession(getEnvironment(), new XDebugProcessStarter() { // TODO: This don't works
            @NotNull
            @Override
            public XDebugProcess start(@NotNull XDebugSession xDebugSession) throws ExecutionException {
                return createRemoteGDBProcess("tcp:localhost:1234",
                        "/home/merskip/Workspace/PiomekOS/cmake-build-debug/kernel.bin", xDebugSession);
            }
        });
    }

    @Override
    boolean isDebug() {
        return true;
    }
}

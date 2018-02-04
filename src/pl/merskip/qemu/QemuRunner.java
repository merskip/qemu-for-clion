package pl.merskip.qemu;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.GenericProgramRunner;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.runners.RunContentBuilder;
import com.intellij.execution.ui.RunContentDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QemuRunner extends GenericProgramRunner {

    public static final String RUN_EXECUTOR = DefaultRunExecutor.EXECUTOR_ID;
    public static final String DEBUG_EXECUTOR = DefaultDebugExecutor.EXECUTOR_ID;

    @NotNull
    @Override
    public String getRunnerId() {
        return getClass().getCanonicalName();
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile runProfile) {
        return runProfile instanceof QemuRunConfiguration
                && (executorId.equals(RUN_EXECUTOR) || executorId.equals(DEBUG_EXECUTOR));
    }

    @Override
    protected void execute(ExecutionEnvironment environment, Callback callback, RunProfileState state) throws ExecutionException {
        super.execute(environment, callback, state);
    }

    @Nullable
    @Override
    protected RunContentDescriptor doExecute(RunProfileState state, ExecutionEnvironment environment) throws ExecutionException {
        ExecutionResult executionResult = state.execute(environment.getExecutor(), this);
        if (executionResult == null) {
            return null;
        }
        return new RunContentBuilder(executionResult, environment).showRunContent(environment.getContentToReuse());
    }
}

package pl.merskip.qemu;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.BaseOSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.jetbrains.cidr.cpp.execution.CLionRunParameters;
import com.jetbrains.cidr.cpp.execution.debugger.backend.GDBDriverConfiguration;
import com.jetbrains.cidr.cpp.toolchains.CPPToolchains;
import com.jetbrains.cidr.execution.TrivialInstaller;
import com.jetbrains.cidr.execution.debugger.CidrDebugProcess;
import com.jetbrains.cidr.execution.debugger.CidrLocalDebugProcess;
import com.jetbrains.cidr.execution.testing.CidrLauncher;
import org.jetbrains.annotations.NotNull;

public class QemuLauncher extends CidrLauncher {

    private Project project;

    public QemuLauncher(Project project) {
        this.project = project;
    }

    @Override
    protected ProcessHandler createProcess(@NotNull CommandLineState commandLineState) throws ExecutionException {
        GeneralCommandLine commandLine = createGeneralCommandLine();
        return new BaseOSProcessHandler(commandLine.createProcess(), commandLine.getPreparedCommandLine(), commandLine.getCharset());
    }

    @NotNull
    @Override
    protected CidrDebugProcess createDebugProcess(@NotNull CommandLineState commandLineState, @NotNull XDebugSession xDebugSession) throws ExecutionException {
        GeneralCommandLine commandLine = createGeneralCommandLine();

        CPPToolchains.Toolchain toolchain = CPPToolchains.getInstance().getDefaultToolchain();
        if (toolchain == null) {
            throw new IllegalStateException("No default toolchain");
        }
        GDBDriverConfiguration debuggerConfiguration = new GDBDriverConfiguration(getProject(), toolchain);
        CLionRunParameters runParameters = new CLionRunParameters(debuggerConfiguration, new TrivialInstaller(commandLine));
        return new CidrLocalDebugProcess(runParameters, xDebugSession, TextConsoleBuilderFactory.getInstance().createBuilder(getProject()));
    }

    private GeneralCommandLine createGeneralCommandLine() {
        return new GeneralCommandLine("qemu-system-x86_64", "-s", "-S", "-cdrom", "/home/merskip/Workspace/PiomekOS/cmake-build-debug/PiomekOS.iso");
    }

    @NotNull
    @Override
    protected Project getProject() {
        return project;
    }
}

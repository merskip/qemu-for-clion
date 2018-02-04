package pl.merskip.qemu;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.jetbrains.cidr.cpp.cmake.model.CMakeConfiguration;
import com.jetbrains.cidr.cpp.cmake.model.CMakeTarget;
import com.jetbrains.cidr.cpp.execution.CMakeBuildConfigurationHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

public class QemuRunProfileState extends QemuProfileStateBase {

    protected QemuRunProfileState(ExecutionEnvironment environment) {
        super(environment);
    }

    @Override
    boolean isDebug() {
        return false;
    }

}

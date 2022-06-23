package org.bleachhack.util.shader.gl;

import org.bleachhack.util.BleachLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlProgramManager {
    private static GlProgramManager instance = new GlProgramManager();

    public static GlProgramManager getInstance() {
        return instance;
    }

    private GlProgramManager() {
    }

    public void destroyProgram(JsonGlProgram program) {
        program.getFsh().deleteShader(program);
        program.getVsh().deleteShader(program);
        GLX.gl20DeleteProgram(program.getProgramRef());
    }

    public int createProgram() {
        int n = GLX.gl20CreateProgram();
        if (n <= 0) {
            throw new ShaderParseException("Could not create shader program (returned program ID " + n + ")");
        }
        return n;
    }

    public void attachProgram(JsonGlProgram program) {
        program.getFsh().attachShader(program);
        program.getVsh().attachShader(program);
        GLX.gl20LinkProgram(program.getProgramRef());
        int n = GLX.gl20GetProgrami(program.getProgramRef(), GLX.linkStatus);
        if (n == 0) {
            BleachLogger.logger.warn("Error encountered when linking program containing VS " + program.getVsh().getName() + " and FS " + program.getFsh().getName() + ". Log output:");
            BleachLogger.logger.warn(GLX.gl20GetProgramInfoLog(program.getProgramRef(), 32768));
        }
    }
}

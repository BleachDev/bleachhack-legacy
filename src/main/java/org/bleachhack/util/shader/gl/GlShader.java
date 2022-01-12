package org.bleachhack.util.shader.gl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.BufferUtils;

import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class GlShader {
    private final FileType field_8057;
    private final String name;
    private int shaderRef;
    private int refCount = 0;

    private GlShader(FileType arg, int i, String string) {
        this.field_8057 = arg;
        this.shaderRef = i;
        this.name = string;
    }

    public void attachShader(JsonGlProgram program) {
        ++this.refCount;
        GLX.gl20GetAttachShader(program.getProgramRef(), this.shaderRef);
    }

    public void deleteShader(JsonGlProgram program) {
        --this.refCount;
        if (this.refCount <= 0) {
            GLX.gl20DeleteShader(this.shaderRef);
            this.field_8057.method_6969().remove(this.name);
        }
    }

    public String getName() {
        return this.name;
    }

    public static GlShader method_6964(ResourceManager arg, FileType arg2, String string) {
        GlShader class_18742 = (GlShader)arg2.method_6969().get(string);
        if (class_18742 == null) {
            Identifier class_16532 = new Identifier(replaceIdentifier("shaders/program/" + string + arg2.method_6967(), string));
            BufferedInputStream bufferedInputStream = new BufferedInputStream(arg.getResource(class_16532).getInputStream());
            byte[] byArray;
			try {
				byArray = IOUtils.toByteArray(bufferedInputStream);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
            ByteBuffer byteBuffer = BufferUtils.createByteBuffer((int)byArray.length);
            byteBuffer.put(byArray);
            byteBuffer.position(0);
            int n = GLX.gl20CreateShader(arg2.method_6968());
            GLX.gl20ShaderSource(n, byteBuffer);
            GLX.gl20CompileShader(n);
            if (GLX.gl20GetShaderi(n, GLX.compileStatus) == 0) {
                String string2 = StringUtils.trim((String)GLX.gl20GetShaderInfoLog(n, 32768));
                ShaderParseException class_20922 = new ShaderParseException("Couldn't compile " + arg2.method_6966() + " program: " + string2);
                class_20922.addFaultyFile(class_16532.getPath());
                throw class_20922;
            }
            class_18742 = new GlShader(arg2, n, string);
            arg2.method_6969().put(string, class_18742);
        }
        return class_18742;
    }
    
    private static String replaceIdentifier(String string, String name) {
		String[] split = name.split(":");
		if (split.length > 1) {
			if ("__url__".equals(split[0]))
				return name;

			return split[0] + ":" + string.replace(name, split[1]);
		}

		return string;
	}
    
    public enum FileType {
    	VERTEX("vertex", ".vsh", GLX.vertexShader), 
        FRAGMENT("fragment", ".fsh", GLX.fragmentShader);
        
        private final String field_8063;
        private final String field_8064;
        private final int field_8065;
        private final Map<String, GlShader> field_8066;
        
        private FileType(String string4, String string5, int integer6) {
            this.field_8066 = new HashMap<>();
            this.field_8063 = string4;
            this.field_8064 = string5;
            this.field_8065 = integer6;
        }
        
        public String method_6966() {
            return this.field_8063;
        }
        
        protected String method_6967() {
            return this.field_8064;
        }
        
        protected int method_6968() {
            return this.field_8065;
        }
        
        protected Map<String, GlShader> method_6969() {
            return this.field_8066;
        }
    }
}

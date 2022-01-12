package org.bleachhack.util.shader.gl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTBlendFuncSeparate;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;

public class GLX {
    public static boolean gl21Supported;
    public static int textureUnit;
    public static int lightmapTextureUnit;
    public static boolean nvidia;
    public static int framebuffer;
    public static int renderbuffer;
    public static int colorAttachment;
    public static int depthAttachment;
    public static int completeFramebuffer;
    public static int incompleteFramebufferAttachment;
    public static int incompleteFramebufferAttachmentMiss;
    public static int incompleteFramebufferAttachmentDraw;
    public static int incompleteFramebufferAttachmentRead;
    private static int type;
    public static boolean advanced = true;
    private static boolean shaders;
    private static boolean arbShaderObjects;
    public static int linkStatus;
    public static int compileStatus;
    public static int vertexShader;
    public static int fragmentShader;
    public static boolean field_8388;
    public static int field_8389;
    private static boolean arbMultitexture;
    private static boolean gl14Supported;
    public static boolean blendFuncSeperateSupported;
    public static boolean shadersSupported;
    private static String contextDescription;

    public static void _initCapabilities() {
        ContextCapabilities contextCapabilities = GLContext.getCapabilities();
        if (arbMultitexture) {
            contextDescription = contextDescription + "Using multitexturing ARB.\n";
            textureUnit = 33984;
            lightmapTextureUnit = 33985;
        } else {
            contextDescription = contextDescription + "Using GL 1.3 multitexturing.\n";
            textureUnit = 33984;
            lightmapTextureUnit = 33985;
        }
        blendFuncSeperateSupported = contextCapabilities.GL_EXT_blend_func_separate && !contextCapabilities.OpenGL14;
        gl14Supported = contextCapabilities.OpenGL14 || contextCapabilities.GL_EXT_blend_func_separate;
        if (advanced) {
            contextDescription = contextDescription + "Using framebuffer objects because ";
            if (contextCapabilities.OpenGL30) {
                contextDescription = contextDescription + "OpenGL 3.0 is supported and separate blending is supported.\n";
                type = 0;
                framebuffer = 36160;
                renderbuffer = 36161;
                colorAttachment = 36064;
                depthAttachment = 36096;
                completeFramebuffer = 36053;
                incompleteFramebufferAttachment = 36054;
                incompleteFramebufferAttachmentMiss = 36055;
                incompleteFramebufferAttachmentDraw = 36059;
                incompleteFramebufferAttachmentRead = 36060;
            } else if (contextCapabilities.GL_ARB_framebuffer_object) {
                contextDescription = contextDescription + "ARB_framebuffer_object is supported and separate blending is supported.\n";
                type = 1;
                framebuffer = 36160;
                renderbuffer = 36161;
                colorAttachment = 36064;
                depthAttachment = 36096;
                completeFramebuffer = 36053;
                incompleteFramebufferAttachmentMiss = 36055;
                incompleteFramebufferAttachment = 36054;
                incompleteFramebufferAttachmentDraw = 36059;
                incompleteFramebufferAttachmentRead = 36060;
            } else if (contextCapabilities.GL_EXT_framebuffer_object) {
                contextDescription = contextDescription + "EXT_framebuffer_object is supported.\n";
                type = 2;
                framebuffer = 36160;
                renderbuffer = 36161;
                colorAttachment = 36064;
                depthAttachment = 36096;
                completeFramebuffer = 36053;
                incompleteFramebufferAttachmentMiss = 36055;
                incompleteFramebufferAttachment = 36054;
                incompleteFramebufferAttachmentDraw = 36059;
                incompleteFramebufferAttachmentRead = 36060;
            }
        } else {
            contextDescription = contextDescription + "Not using framebuffer objects because ";
            contextDescription = contextDescription + "OpenGL 1.4 is " + (contextCapabilities.OpenGL14 ? "" : "not ") + "supported, ";
            contextDescription = contextDescription + "EXT_blend_func_separate is " + (contextCapabilities.GL_EXT_blend_func_separate ? "" : "not ") + "supported, ";
            contextDescription = contextDescription + "OpenGL 3.0 is " + (contextCapabilities.OpenGL30 ? "" : "not ") + "supported, ";
            contextDescription = contextDescription + "ARB_framebuffer_object is " + (contextCapabilities.GL_ARB_framebuffer_object ? "" : "not ") + "supported, and ";
            contextDescription = contextDescription + "EXT_framebuffer_object is " + (contextCapabilities.GL_EXT_framebuffer_object ? "" : "not ") + "supported.\n";
        }
        field_8388 = contextCapabilities.GL_EXT_texture_filter_anisotropic;
        field_8389 = (int)(field_8388 ? GL11.glGetFloat((int)34047) : 0.0f);
        contextDescription = contextDescription + "Anisotropic filtering is " + (field_8388 ? "" : "not ") + "supported";
        contextDescription = field_8388 ? contextDescription + " and maximum anisotropy is " + field_8389 + ".\n" : contextDescription + ".\n";
        gl21Supported = contextCapabilities.OpenGL21;
        shaders = gl21Supported || contextCapabilities.GL_ARB_vertex_shader && contextCapabilities.GL_ARB_fragment_shader && contextCapabilities.GL_ARB_shader_objects;
        contextDescription = contextDescription + "Shaders are " + (shaders ? "" : "not ") + "available because ";
        if (shaders) {
            if (contextCapabilities.OpenGL21) {
                contextDescription = contextDescription + "OpenGL 2.1 is supported.\n";
                arbShaderObjects = false;
                linkStatus = 35714;
                compileStatus = 35713;
                vertexShader = 35633;
                fragmentShader = 35632;
            } else {
                contextDescription = contextDescription + "ARB_shader_objects, ARB_vertex_shader, and ARB_fragment_shader are supported.\n";
                arbShaderObjects = true;
                linkStatus = 35714;
                compileStatus = 35713;
                vertexShader = 35633;
                fragmentShader = 35632;
            }
        } else {
            contextDescription = contextDescription + "OpenGL 2.1 is " + (contextCapabilities.OpenGL21 ? "" : "not ") + "supported, ";
            contextDescription = contextDescription + "ARB_shader_objects is " + (contextCapabilities.GL_ARB_shader_objects ? "" : "not ") + "supported, ";
            contextDescription = contextDescription + "ARB_vertex_shader is " + (contextCapabilities.GL_ARB_vertex_shader ? "" : "not ") + "supported, and ";
            contextDescription = contextDescription + "ARB_fragment_shader is " + (contextCapabilities.GL_ARB_fragment_shader ? "" : "not ") + "supported.\n";
        }
        shadersSupported = advanced && shaders;
        nvidia = GL11.glGetString((int)7936).toLowerCase().contains("nvidia");
    }

    public static boolean areShadersSupported() {
        return shadersSupported;
    }

    public static String getContextDescription() {
        return contextDescription;
    }

    /**
     * Returns a parameter from a program object
     */
    public static int gl20GetProgrami(int program, int param) {
        if (arbShaderObjects) {
            return ARBShaderObjects.glGetObjectParameteriARB((int)program, (int)param);
        }
        return GL20.glGetProgrami((int)program, (int)param);
    }

    /**
     * Attaches a shader object to a program object
     */
    public static void gl20GetAttachShader(int program, int shader) {
        if (arbShaderObjects) {
            ARBShaderObjects.glAttachObjectARB((int)program, (int)shader);
        } else {
            GL20.glAttachShader((int)program, (int)shader);
        }
    }

    /**
     * Deletes a shader object
     */
    public static void gl20DeleteShader(int shader) {
        if (arbShaderObjects) {
            ARBShaderObjects.glDeleteObjectARB((int)shader);
        } else {
            GL20.glDeleteShader((int)shader);
        }
    }

    /**
     * Creates a shader object
     */
    public static int gl20CreateShader(int shader) {
        if (arbShaderObjects) {
            return ARBShaderObjects.glCreateShaderObjectARB((int)shader);
        }
        return GL20.glCreateShader((int)shader);
    }

    /**
     * Replaces the source code in a shader object
     */
    public static void gl20ShaderSource(int shader, ByteBuffer count) {
        if (arbShaderObjects) {
            ARBShaderObjects.glShaderSourceARB((int)shader, (ByteBuffer)count);
        } else {
            GL20.glShaderSource((int)shader, (ByteBuffer)count);
        }
    }

    /**
     * Compiles a shader object
     */
    public static void gl20CompileShader(int shader) {
        if (arbShaderObjects) {
            ARBShaderObjects.glCompileShaderARB((int)shader);
        } else {
            GL20.glCompileShader((int)shader);
        }
    }

    /**
     * Returns a parameter from a shader object
     */
    public static int gl20GetShaderi(int shader, int param) {
        if (arbShaderObjects) {
            return ARBShaderObjects.glGetObjectParameteriARB((int)shader, (int)param);
        }
        return GL20.glGetShaderi((int)shader, (int)param);
    }

    /**
     * Returns the information log for a shader object
     */
    public static String gl20GetShaderInfoLog(int shader, int maxLength) {
        if (arbShaderObjects) {
            return ARBShaderObjects.glGetInfoLogARB((int)shader, (int)maxLength);
        }
        return GL20.glGetShaderInfoLog((int)shader, (int)maxLength);
    }

    /**
     * Returns the information log for a program object
     */
    public static String gl20GetProgramInfoLog(int program, int maxLength) {
        if (arbShaderObjects) {
            return ARBShaderObjects.glGetInfoLogARB((int)program, (int)maxLength);
        }
        return GL20.glGetProgramInfoLog((int)program, (int)maxLength);
    }

    /**
     * Installs a program object as part of current rendering state
     */
    public static void gl20UseProgram(int program) {
        if (arbShaderObjects) {
            ARBShaderObjects.glUseProgramObjectARB((int)program);
        } else {
            GL20.glUseProgram((int)program);
        }
    }

    /**
     * Creates a program object
     */
    public static int gl20CreateProgram() {
        if (arbShaderObjects) {
            return ARBShaderObjects.glCreateProgramObjectARB();
        }
        return GL20.glCreateProgram();
    }

    /**
     * Deletes a program object
     */
    public static void gl20DeleteProgram(int program) {
        if (arbShaderObjects) {
            ARBShaderObjects.glDeleteObjectARB((int)program);
        } else {
            GL20.glDeleteProgram((int)program);
        }
    }

    /**
     * Links a program object
     */
    public static void gl20LinkProgram(int program) {
        if (arbShaderObjects) {
            ARBShaderObjects.glLinkProgramARB((int)program);
        } else {
            GL20.glLinkProgram((int)program);
        }
    }

    /**
     * Returns the location of a uniform variable
     */
    public static int gl20GetUniformLocation(int program, CharSequence name) {
        if (arbShaderObjects) {
            return ARBShaderObjects.glGetUniformLocationARB((int)program, (CharSequence)name);
        }
        return GL20.glGetUniformLocation((int)program, (CharSequence)name);
    }

    /**
     * Specifies the value of a uniform variable for the current program object
     */
    public static void gl20Uniform1(int loc, IntBuffer v) {
        if (arbShaderObjects) {
            ARBShaderObjects.glUniform1ARB((int)loc, (IntBuffer)v);
        } else {
            GL20.glUniform1((int)loc, (IntBuffer)v);
        }
    }

    /**
     * Specifies the value of a uniform variable for the current program object
     */
    public static void gl20Uniform1(int loc, int v) {
        if (arbShaderObjects) {
            ARBShaderObjects.glUniform1iARB((int)loc, (int)v);
        } else {
            GL20.glUniform1i((int)loc, (int)v);
        }
    }

    /**
     * Specifies the value of a uniform variable for the current program object
     */
    public static void gl20Uniform(int loc, FloatBuffer v) {
        if (arbShaderObjects) {
            ARBShaderObjects.glUniform1ARB((int)loc, (FloatBuffer)v);
        } else {
            GL20.glUniform1((int)loc, (FloatBuffer)v);
        }
    }

    /**
     * Specifies the value of a uniform variable for the current program object
     */
    public static void gl20Uniform2(int loc, IntBuffer v) {
        if (arbShaderObjects) {
            ARBShaderObjects.glUniform2ARB((int)loc, (IntBuffer)v);
        } else {
            GL20.glUniform2((int)loc, (IntBuffer)v);
        }
    }

    /**
     * Specifies the value of a uniform variable for the current program object
     */
    public static void gl20Uniform2(int loc, FloatBuffer v) {
        if (arbShaderObjects) {
            ARBShaderObjects.glUniform2ARB((int)loc, (FloatBuffer)v);
        } else {
            GL20.glUniform2((int)loc, (FloatBuffer)v);
        }
    }

    /**
     * Specifies the value of a uniform variable for the current program object
     */
    public static void gl20Uniform3(int loc, IntBuffer v) {
        if (arbShaderObjects) {
            ARBShaderObjects.glUniform3ARB((int)loc, (IntBuffer)v);
        } else {
            GL20.glUniform3((int)loc, (IntBuffer)v);
        }
    }

    /**
     * Specifies the value of a uniform variable for the current program object
     */
    public static void gl20Uniform3(int loc, FloatBuffer v) {
        if (arbShaderObjects) {
            ARBShaderObjects.glUniform3ARB((int)loc, (FloatBuffer)v);
        } else {
            GL20.glUniform3((int)loc, (FloatBuffer)v);
        }
    }

    /**
     * Specifies the value of a uniform variable for the current program object
     */
    public static void gl20Uniform4(int loc, IntBuffer v) {
        if (arbShaderObjects) {
            ARBShaderObjects.glUniform4ARB((int)loc, (IntBuffer)v);
        } else {
            GL20.glUniform4((int)loc, (IntBuffer)v);
        }
    }

    /**
     * Specifies the value of a uniform variable for the current program object
     */
    public static void gl20Uniform4(int loc, FloatBuffer v) {
        if (arbShaderObjects) {
            ARBShaderObjects.glUniform4ARB((int)loc, (FloatBuffer)v);
        } else {
            GL20.glUniform4((int)loc, (FloatBuffer)v);
        }
    }

    /**
     * Specifies the value of a uniform variable for the current program object
     */
    public static void gl20UniformMatrix2(int uniform, boolean bl, FloatBuffer buf) {
        if (arbShaderObjects) {
            ARBShaderObjects.glUniformMatrix2ARB((int)uniform, (boolean)bl, (FloatBuffer)buf);
        } else {
            GL20.glUniformMatrix2((int)uniform, (boolean)bl, (FloatBuffer)buf);
        }
    }

    /**
     * Specifies the value of a uniform variable for the current program object
     */
    public static void gl20UniformMatrix3(int uniform, boolean bl, FloatBuffer buf) {
        if (arbShaderObjects) {
            ARBShaderObjects.glUniformMatrix3ARB((int)uniform, (boolean)bl, (FloatBuffer)buf);
        } else {
            GL20.glUniformMatrix3((int)uniform, (boolean)bl, (FloatBuffer)buf);
        }
    }

    /**
     * Specifies the value of a uniform variable for the current program object
     */
    public static void gl20UniformMatrix4(int uniform, boolean bl, FloatBuffer buf) {
        if (arbShaderObjects) {
            ARBShaderObjects.glUniformMatrix4ARB((int)uniform, (boolean)bl, (FloatBuffer)buf);
        } else {
            GL20.glUniformMatrix4((int)uniform, (boolean)bl, (FloatBuffer)buf);
        }
    }

    /**
     * Returns the location of an attribute variable
     */
    public static int gl20GetAttribLocation(int loc, CharSequence sequence) {
        if (arbShaderObjects) {
            return ARBVertexShader.glGetAttribLocationARB((int)loc, (CharSequence)sequence);
        }
        return GL20.glGetAttribLocation((int)loc, (CharSequence)sequence);
    }

    /**
     * Binds a framebuffer to a framebuffer target
     */
    public static void advancedBindFramebuffer(int i, int j) {
        if (!advanced) {
            return;
        }
        switch (type) {
            case 0: {
                GL30.glBindFramebuffer((int)i, (int)j);
                break;
            }
            case 1: {
                ARBFramebufferObject.glBindFramebuffer((int)i, (int)j);
                break;
            }
            case 2: {
                EXTFramebufferObject.glBindFramebufferEXT((int)i, (int)j);
            }
        }
    }

    /**
     * Binds a renderbuffer to a renderbuffer target
     */
    public static void advancedBindRenderBuffer(int i, int j) {
        if (!advanced) {
            return;
        }
        switch (type) {
            case 0: {
                GL30.glBindRenderbuffer((int)i, (int)j);
                break;
            }
            case 1: {
                ARBFramebufferObject.glBindRenderbuffer((int)i, (int)j);
                break;
            }
            case 2: {
                EXTFramebufferObject.glBindRenderbufferEXT((int)i, (int)j);
            }
        }
    }

    /**
     * Deletes renderbuffer objects
     */
    public static void advancedDeleteRenderBuffers(int renderbuffer) {
        if (!advanced) {
            return;
        }
        switch (type) {
            case 0: {
                GL30.glDeleteRenderbuffers((int)renderbuffer);
                break;
            }
            case 1: {
                ARBFramebufferObject.glDeleteRenderbuffers((int)renderbuffer);
                break;
            }
            case 2: {
                EXTFramebufferObject.glDeleteRenderbuffersEXT((int)renderbuffer);
            }
        }
    }

    /**
     * Deletes framebuffer objects
     */
    public static void advancedDeleteFrameBuffers(int framebuffer) {
        if (!advanced) {
            return;
        }
        switch (type) {
            case 0: {
                GL30.glDeleteFramebuffers((int)framebuffer);
                break;
            }
            case 1: {
                ARBFramebufferObject.glDeleteFramebuffers((int)framebuffer);
                break;
            }
            case 2: {
                EXTFramebufferObject.glDeleteFramebuffersEXT((int)framebuffer);
            }
        }
    }

    /**
     * Generates framebuffer object names
     */
    public static int advancedGenFrameBuffers() {
        if (!advanced) {
            return -1;
        }
        switch (type) {
            case 0: {
                return GL30.glGenFramebuffers();
            }
            case 1: {
                return ARBFramebufferObject.glGenFramebuffers();
            }
            case 2: {
                return EXTFramebufferObject.glGenFramebuffersEXT();
            }
        }
        return -1;
    }

    /**
     * Generates renderbuffer object names
     */
    public static int advancedGenRenderBuffers() {
        if (!advanced) {
            return -1;
        }
        switch (type) {
            case 0: {
                return GL30.glGenRenderbuffers();
            }
            case 1: {
                return ARBFramebufferObject.glGenRenderbuffers();
            }
            case 2: {
                return EXTFramebufferObject.glGenRenderbuffersEXT();
            }
        }
        return -1;
    }

    /**
     * Establishes data storage, format and dimensions of a renderbuffer object's image
     */
    public static void advancedRenderBufferStorage(int i, int j, int k, int l) {
        if (!advanced) {
            return;
        }
        switch (type) {
            case 0: {
                GL30.glRenderbufferStorage((int)i, (int)j, (int)k, (int)l);
                break;
            }
            case 1: {
                ARBFramebufferObject.glRenderbufferStorage((int)i, (int)j, (int)k, (int)l);
                break;
            }
            case 2: {
                EXTFramebufferObject.glRenderbufferStorageEXT((int)i, (int)j, (int)k, (int)l);
            }
        }
    }

    /**
     * Attaches a renderbuffer as a logical buffer of a framebuffer object
     */
    public static void advancedFramebufferRenderbuffer(int i, int j, int k, int l) {
        if (!advanced) {
            return;
        }
        switch (type) {
            case 0: {
                GL30.glFramebufferRenderbuffer((int)i, (int)j, (int)k, (int)l);
                break;
            }
            case 1: {
                ARBFramebufferObject.glFramebufferRenderbuffer((int)i, (int)j, (int)k, (int)l);
                break;
            }
            case 2: {
                EXTFramebufferObject.glFramebufferRenderbufferEXT((int)i, (int)j, (int)k, (int)l);
            }
        }
    }

    /**
     * Checks the completeness of a framebuffer
     */
    public static int advancedCheckFrameBufferStatus(int framebuffer) {
        if (!advanced) {
            return -1;
        }
        switch (type) {
            case 0: {
                return GL30.glCheckFramebufferStatus((int)framebuffer);
            }
            case 1: {
                return ARBFramebufferObject.glCheckFramebufferStatus((int)framebuffer);
            }
            case 2: {
                return EXTFramebufferObject.glCheckFramebufferStatusEXT((int)framebuffer);
            }
        }
        return -1;
    }

    /**
     * Attaches a level of a texture object as a logical buffer of a framebuffer object
     */
    public static void advancedFrameBufferTexture2D(int i, int j, int k, int l, int m) {
        if (!advanced) {
            return;
        }
        switch (type) {
            case 0: {
                GL30.glFramebufferTexture2D((int)i, (int)j, (int)k, (int)l, (int)m);
                break;
            }
            case 1: {
                ARBFramebufferObject.glFramebufferTexture2D((int)i, (int)j, (int)k, (int)l, (int)m);
                break;
            }
            case 2: {
                EXTFramebufferObject.glFramebufferTexture2DEXT((int)i, (int)j, (int)k, (int)l, (int)m);
            }
        }
    }

    /**
     * Allows selecting the active texture unit
     */
    public static void gl13ActiveTexture(int texture) {
        if (arbMultitexture) {
            ARBMultitexture.glActiveTextureARB((int)texture);
        } else {
            GL13.glActiveTexture((int)texture);
        }
    }

    /**
     * Allows selecting the active texture unit
     */
    public static void gl13ClientActiveTexture(int texture) {
        if (arbMultitexture) {
            ARBMultitexture.glClientActiveTextureARB((int)texture);
        } else {
            GL13.glClientActiveTexture((int)texture);
        }
    }

    /**
     * Sets the current texture coordinates
     */
    public static void gl13MultiTexCoord2f(int i, float f1, float f2) {
        if (arbMultitexture) {
            ARBMultitexture.glMultiTexCoord2fARB((int)i, (float)f1, (float)f2);
        } else {
            GL13.glMultiTexCoord2f((int)i, (float)f1, (float)f2);
        }
    }

    /**
     * Specifies pixel arithmetic for RGB and alpha components separately
     */
    public static void glBlendFuncSeparate(int r, int g, int b, int a) {
        if (gl14Supported) {
            if (blendFuncSeperateSupported) {
                EXTBlendFuncSeparate.glBlendFuncSeparateEXT((int)r, (int)g, (int)b, (int)a);
            } else {
                GL14.glBlendFuncSeparate((int)r, (int)g, (int)b, (int)a);
            }
        } else {
            GL11.glBlendFunc((int)r, (int)g);
        }
    }

    /**
     * Returns whether OpenGl supports  Frame Buffer Objects
     */
    public static boolean supportsFbo() {
        return advanced;
    }

    static {
        contextDescription = "";
    }
}
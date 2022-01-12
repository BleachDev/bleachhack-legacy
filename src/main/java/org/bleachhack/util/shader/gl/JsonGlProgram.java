package org.bleachhack.util.shader.gl;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bleachhack.util.shader.gl.GlShader.FileType;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.texture.Texture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@SuppressWarnings("unused")
public class JsonGlProgram {
    private static final Logger LOGGER;
    private static final GlShaderUniform UNIFORM;
    private static JsonGlProgram activeProgram;
    private static int activeProgramRef;
    private static boolean active;
    private final Map<String, Object> samplerBinds;
    private final List<String> samplerNames;
    private final List<Integer> samplerShaderLocs;
    private final List<GlUniform> uniformData;
    private final List<Integer> uniformLocs;
    public final Map<String, GlUniform> uniformByName;
    private final int programRef;
    private final String name;
    private final boolean useCullFace;
	private boolean uniformStateDirty;
    private final GlBlendState field_8033;
    private final List<Integer> attribLocs;
    private final List<String> attribNames;
    private final GlShader vertex;
    private final GlShader fragment;
    
    public JsonGlProgram(ResourceManager manager, String name) {
        this.samplerBinds = Maps.newHashMap();
        this.samplerNames = Lists.newArrayList();
        this.samplerShaderLocs = Lists.newArrayList();
        this.uniformData = Lists.newArrayList();
        this.uniformLocs = Lists.newArrayList();
        this.uniformByName = Maps.newHashMap();
        JsonParser jsonParser4 = new JsonParser();
        Identifier identifier5 = new Identifier(replaceIdentifier("shaders/program/" + name + ".json", name));
        this.name = name;
        InputStream __Null6 = null;
        try {
            __Null6 = manager.getResource(identifier5).getInputStream();
            JsonObject jsonObject7 = jsonParser4.parse(IOUtils.toString(__Null6, Charsets.UTF_8)).getAsJsonObject();
            String string8 = JsonHelper.getString(jsonObject7, "vertex");
            String string9 = JsonHelper.getString(jsonObject7, "fragment");
            JsonArray jsonArray10 = JsonHelper.getArray(jsonObject7, "samplers", null);
            if (jsonArray10 != null) {
                int integer11 = 0;
                for (final JsonElement jsonElement : jsonArray10) {
                    try {
                        this.addSampler(jsonElement);
                    }
                    catch (Exception exception) {
                        ShaderParseException shaderParseException15 = ShaderParseException.wrap(exception);
                        shaderParseException15.addFaultyElement("samplers[" + integer11 + "]");
                        throw shaderParseException15;
                    }
                    ++integer11;
                }
            }
            JsonArray jsonArray11 = JsonHelper.getArray(jsonObject7, "attributes", null);
            if (jsonArray11 != null) {
                int integer12 = 0;
                this.attribLocs = Lists.newArrayListWithCapacity(jsonArray11.size());
                this.attribNames = Lists.newArrayListWithCapacity(jsonArray11.size());
                for (final JsonElement jsonElement2 : jsonArray11) {
                    try {
                        this.attribNames.add(JsonHelper.asString(jsonElement2, "attribute"));
                    }
                    catch (Exception exception2) {
                        ShaderParseException shaderParseException16 = ShaderParseException.wrap(exception2);
                        shaderParseException16.addFaultyElement("attributes[" + integer12 + "]");
                        throw shaderParseException16;
                    }
                    ++integer12;
                }
            }
            else {
                this.attribLocs = null;
                this.attribNames = null;
            }
            JsonArray jsonArray12 = JsonHelper.getArray(jsonObject7, "uniforms", null);
            if (jsonArray12 != null) {
                int integer13 = 0;
                for (final JsonElement jsonElement3 : jsonArray12) {
                    try {
                        this.addUniformInternal(jsonElement3);
                    }
                    catch (Exception exception3) {
                        ShaderParseException shaderParseException17 = ShaderParseException.wrap(exception3);
                        shaderParseException17.addFaultyElement("uniforms[" + integer13 + "]");
                        throw shaderParseException17;
                    }
                    ++integer13;
                }
            }
            this.field_8033 = GlBlendState.method_6927(JsonHelper.getObject(jsonObject7, "blend", null));
            this.useCullFace = JsonHelper.getBoolean(jsonObject7, "cull", true);
            this.vertex = GlShader.method_6964(manager, FileType.VERTEX, string8);
            this.fragment = GlShader.method_6964(manager, FileType.FRAGMENT, string9);
            this.programRef = GlProgramManager.getInstance().createProgram();
            GlProgramManager.getInstance().attachProgram(this);
            this.finalizeUniformsAndSamplers();
            if (this.attribNames != null) {
                Iterator<String> iterator13 = (Iterator<String>)this.attribNames.iterator();
                while (iterator13.hasNext()) {
                    this.attribLocs.add(GLX.gl20GetAttribLocation(this.programRef, iterator13.next()));
                }
            }
        }
        catch (Exception exception4) {
            ShaderParseException shaderParseException9 = ShaderParseException.wrap(exception4);
            shaderParseException9.addFaultyFile(identifier5.getPath());
            throw shaderParseException9;
        }
        finally {
            IOUtils.closeQuietly(__Null6);
        }
        this.method_6939();
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
    
    public void method_6931() {
        GlProgramManager.getInstance().destroyProgram(this);
    }
    
    public void disable() {
        GLX.gl20UseProgram(0);
        JsonGlProgram.activeProgramRef = -1;
        JsonGlProgram.activeProgram = null;
        JsonGlProgram.active = true;
        for (int i = 0; i < this.samplerShaderLocs.size(); ++i) {
            if (this.samplerBinds.get(this.samplerNames.get(i)) != null) {
                GL13.glActiveTexture(33984 + i);
                GL11.glBindTexture(3553, 0);
            }
        }
    }
    
    public void enable() {
        this.uniformStateDirty = false;
        JsonGlProgram.activeProgram = this;
        this.field_8033.enable();
        if (this.programRef != JsonGlProgram.activeProgramRef) {
            GLX.gl20UseProgram(this.programRef);
            JsonGlProgram.activeProgramRef = this.programRef;
        }
        if (JsonGlProgram.active != this.useCullFace) {
            JsonGlProgram.active = this.useCullFace;
            if (this.useCullFace) {
                GL11.glEnable(2884);
            }
            else {
                GL11.glDisable(2884);
            }
        }
        for (int i = 0; i < this.samplerShaderLocs.size(); ++i) {
            if (this.samplerBinds.get(this.samplerNames.get(i)) != null) {
                GL13.glActiveTexture(33984 + i);
                GL11.glEnable(3553);
                Object object3 = this.samplerBinds.get(this.samplerNames.get(i));
                int integer4 = -1;
                if (object3 instanceof Framebuffer) {
                    integer4 = ((Framebuffer)object3).colorAttachment;
                }
                else if (object3 instanceof Texture) {
                    integer4 = ((Texture)object3).getGlId();
                }
                else if (object3 instanceof Integer) {
                    integer4 = (int)object3;
                }
                if (integer4 != -1) {
                    GL11.glBindTexture(3553, integer4);
                    GLX.gl20Uniform1(GLX.gl20GetUniformLocation(this.programRef, (CharSequence)this.samplerNames.get(i)), i);
                }
            }
        }
        Iterator<GlUniform> iterator2 = this.uniformData.iterator();
        while (iterator2.hasNext()) {
            iterator2.next().upload();
        }
    }
    
    public void method_6939() {
        this.uniformStateDirty = true;
    }
    
    public GlUniform getUniformByName(String name) {
        if (this.uniformByName.containsKey(name)) {
            return this.uniformByName.get(name);
        }
        return null;
    }
    
    public GlUniform method_6937(String string) {
        if (this.uniformByName.containsKey(string)) {
            return this.uniformByName.get(string);
        }
        return JsonGlProgram.UNIFORM;
    }
    
    private void finalizeUniformsAndSamplers() {
        for (int i = 0, n = 0; i < this.samplerNames.size(); ++i, ++n) {
            String string4 = this.samplerNames.get(i);
            int integer5 = GLX.gl20GetUniformLocation(this.programRef, string4);
            if (integer5 == -1) {
                JsonGlProgram.LOGGER.warn("Shader " + this.name + "could not find sampler named " + string4 + " in the specified shader program.");
                this.samplerBinds.remove(string4);
                this.samplerNames.remove(n);
                --n;
            }
            else {
                this.samplerShaderLocs.add(integer5);
            }
        }
        for (final GlUniform class_1877 : this.uniformData) {
            String string4 = class_1877.getName();
            int integer5 = GLX.gl20GetUniformLocation(this.programRef, string4);
            if (integer5 == -1) {
                JsonGlProgram.LOGGER.warn("Could not find uniform named " + string4 + " in the specified" + " shader program.");
            }
            else {
                this.uniformLocs.add(integer5);
                class_1877.setLoc(integer5);
                this.uniformByName.put(string4, class_1877);
            }
        }
    }
    
    private void addSampler(JsonElement jsonElement) {
        JsonObject jsonObject3 = JsonHelper.asObject(jsonElement, "sampler");
        String string4 = JsonHelper.getString(jsonObject3, "name");
        if (!JsonHelper.hasString(jsonObject3, "file")) {
            this.samplerBinds.put(string4, null);
            this.samplerNames.add(string4);
            return;
        }
        this.samplerNames.add(string4);
    }
    
    public void bindSampler(String samplerName, Object object) {
        if (this.samplerBinds.containsKey(samplerName)) {
            this.samplerBinds.remove(samplerName);
        }
        this.samplerBinds.put(samplerName, object);
        this.method_6939();
    }
    
    private void addUniformInternal(JsonElement jsonElement) {
        JsonObject jsonObject3 = JsonHelper.asObject(jsonElement, "uniform");
        String string4 = JsonHelper.getString(jsonObject3, "name");
        int integer5 = GlUniform.getTypeIndex(JsonHelper.getString(jsonObject3, "type"));
        int integer6 = JsonHelper.getInt(jsonObject3, "count");
        float[] fs = new float[Math.max(integer6, 16)];
        JsonArray jsonArray8 = JsonHelper.getArray(jsonObject3, "values");
        if (jsonArray8.size() != integer6 && jsonArray8.size() > 1) {
            throw new ShaderParseException("Invalid amount of values specified (expected " + integer6 + ", found " + jsonArray8.size() + ")");
        }
        int integer9 = 0;
        for (final JsonElement jsonElement2 : jsonArray8) {
            try {
                fs[integer9] = JsonHelper.asFloat(jsonElement2, "value");
            }
            catch (Exception exception) {
                ShaderParseException shaderParseException13 = ShaderParseException.wrap(exception);
                shaderParseException13.addFaultyElement("values[" + integer9 + "]");
                throw shaderParseException13;
            }
            ++integer9;
        }
        if (integer6 > 1 && jsonArray8.size() == 1) {
            while (integer9 < integer6) {
                fs[integer9] = fs[0];
                ++integer9;
            }
        }
        GlUniform object11 = new GlUniform(string4, integer5 + ((integer6 > 1 && integer6 <= 4 && integer5 < 8) ? (integer6 - 1) : 0), integer6, this);
        if (integer5 <= 3) {
            object11.method_6981((int)fs[0], (int)fs[1], (int)fs[2], (int)fs[3]);
        }
        else if (integer5 <= 7) {
            object11.method_6986(fs[0], fs[1], fs[2], fs[3]);
        }
        else {
            object11.method_6984(fs);
        }
        this.uniformData.add(object11);
    }
    
    public GlShader getVsh() {
        return this.vertex;
    }
    
    public GlShader getFsh() {
        return this.fragment;
    }
    
    public int getProgramRef() {
        return this.programRef;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        UNIFORM = new GlShaderUniform();
        JsonGlProgram.activeProgram = null;
        JsonGlProgram.activeProgramRef = -1;
        JsonGlProgram.active = true;
    }
}

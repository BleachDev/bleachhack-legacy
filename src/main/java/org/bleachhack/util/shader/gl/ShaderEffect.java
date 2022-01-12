package org.bleachhack.util.shader.gl;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.texture.Texture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class ShaderEffect {
	private final Framebuffer framebuffer;
	private final ResourceManager resurceManager;
	private final String name;
	public final List<PostProcessShader> passes;
	public final Map<String, Framebuffer> targetsByName;
	private final List<Framebuffer> defaultSizedTargets;
	private Matrix4f field_8044;
	private int width;
	private int height;
	private float time;
	private float lastTickDelta;

	public ShaderEffect(TextureManager textureManager, ResourceManager resourceManager, Framebuffer framebuffer, Identifier identifier) {
		this.passes = Lists.newArrayList();
		this.targetsByName = Maps.newHashMap();
		this.defaultSizedTargets = Lists.newArrayList();
		this.resurceManager = resourceManager;
		this.framebuffer = framebuffer;
		this.time = 0.0f;
		this.lastTickDelta = 0.0f;
		this.width = framebuffer.viewportWidth;
		this.height = framebuffer.viewportHeight;
		this.name = identifier.toString();
		this.setupProjectionMatrix();
		this.method_6947(textureManager, identifier);
	}

	public void method_6947(TextureManager textureManager, Identifier identifier) {
		JsonParser jsonParser4 = new JsonParser();
		InputStream __Null5 = null;
		try {
			__Null5 = this.resurceManager.getResource(identifier).getInputStream();
			JsonObject jsonObject7 = jsonParser4.parse(IOUtils.toString(__Null5, Charsets.UTF_8)).getAsJsonObject();
			if (JsonHelper.hasArray(jsonObject7, "targets")) {
				JsonArray jsonArray8 = jsonObject7.getAsJsonArray("targets");
				int integer9 = 0;
				for (final JsonElement jsonElement : jsonArray8) {
					try {
						this.parseTarget(jsonElement);
					}
					catch (Exception exception) {
						ShaderParseException shaderParseException13 = ShaderParseException.wrap(exception);
						shaderParseException13.addFaultyElement("targets[" + integer9 + "]");
						throw shaderParseException13;
					}
					++integer9;
				}
			}
			if (JsonHelper.hasArray(jsonObject7, "passes")) {
				JsonArray jsonArray8 = jsonObject7.getAsJsonArray("passes");
				int integer9 = 0;
				for (final JsonElement jsonElement2 : jsonArray8) {
					try {
						this.method_6948(textureManager, jsonElement2);
					}
					catch (Exception exception2) {
						ShaderParseException shaderParseException13 = ShaderParseException.wrap(exception2);
						shaderParseException13.addFaultyElement("passes[" + integer9 + "]");
						throw shaderParseException13;
					}
					++integer9;
				}
			}
		}
		catch (Exception exception3) {
			ShaderParseException shaderParseException7 = ShaderParseException.wrap(exception3);
			shaderParseException7.addFaultyFile(identifier.getPath());
			throw shaderParseException7;
		}
		finally {
			IOUtils.closeQuietly(__Null5);
		}
	}

	private void parseTarget(JsonElement jsonTarget) {
		if (JsonHelper.isString(jsonTarget)) {
			this.addTarget(jsonTarget.getAsString(), this.width, this.height);
		}
		else {
			JsonObject jsonObject3 = JsonHelper.asObject(jsonTarget, "target");
			String string4 = JsonHelper.getString(jsonObject3, "name");
			int integer5 = JsonHelper.getInt(jsonObject3, "width", this.width);
			int integer6 = JsonHelper.getInt(jsonObject3, "height", this.height);
			if (this.targetsByName.containsKey(string4)) {
				throw new ShaderParseException(string4 + " is already defined");
			}
			this.addTarget(string4, integer5, integer6);
		}
	}

	private void method_6948(TextureManager textureManager, JsonElement jsonElement) {
		JsonObject jsonObject4 = JsonHelper.asObject(jsonElement, "pass");
		String string5 = JsonHelper.getString(jsonObject4, "name");
		String string6 = JsonHelper.getString(jsonObject4, "intarget");
		String string7 = JsonHelper.getString(jsonObject4, "outtarget");
		Framebuffer framebuffer8 = this.getTarget(string6);
		Framebuffer framebuffer9 = this.getTarget(string7);
		if (framebuffer8 == null) {
			throw new ShaderParseException("Input target '" + string6 + "' does not exist");
		}
		if (framebuffer9 == null) {
			throw new ShaderParseException("Output target '" + string7 + "' does not exist");
		}
		PostProcessShader postProcessShader10 = this.addPass(string5, framebuffer8, framebuffer9);
		JsonArray jsonArray11 = JsonHelper.getArray(jsonObject4, "auxtargets", null);
		if (jsonArray11 != null) {
			int integer12 = 0;
			for (final JsonElement jsonElement2 : jsonArray11) {
				try {
					JsonObject jsonObject15 = JsonHelper.asObject(jsonElement2, "auxtarget");
					String string16 = JsonHelper.getString(jsonObject15, "name");
					String string17 = JsonHelper.getString(jsonObject15, "id");
					Framebuffer framebuffer18 = this.getTarget(string17);
					if (framebuffer18 == null) {
						Identifier object19 = new Identifier("textures/effect/" + string17 + ".png");
						this.resurceManager.getResource(object19);
						textureManager.bindTexture(object19);
						Texture texture20 = textureManager.getTexture(object19);
						int integer21 = JsonHelper.getInt(jsonObject15, "width");
						int integer22 = JsonHelper.getInt(jsonObject15, "height");
						if (JsonHelper.getBoolean(jsonObject15, "bilinear")) {
							GL11.glTexParameteri(3553, 10241, 9729);
							GL11.glTexParameteri(3553, 10240, 9729);
						}
						else {
							GL11.glTexParameteri(3553, 10241, 9728);
							GL11.glTexParameteri(3553, 10240, 9728);
						}
						postProcessShader10.addAuxTarget(string16, texture20.getGlId(), integer21, integer22);
					}
					else {
						postProcessShader10.addAuxTarget(string16, framebuffer18, framebuffer18.textureWidth, framebuffer18.textureHeight);
					}
				}
				catch (Exception exception) {
					ShaderParseException shaderParseException16 = ShaderParseException.wrap(exception);
					shaderParseException16.addFaultyElement("auxtargets[" + integer12 + "]");
					throw shaderParseException16;
				}
				++integer12;
			}
		}
		JsonArray jsonArray12 = JsonHelper.getArray(jsonObject4, "uniforms", null);
		if (jsonArray12 != null) {
			int integer13 = 0;
			for (final JsonElement jsonElement3 : jsonArray12) {
				try {
					this.parseUniform(jsonElement3);
				}
				catch (Exception exception2) {
					ShaderParseException shaderParseException17 = ShaderParseException.wrap(exception2);
					shaderParseException17.addFaultyElement("uniforms[" + integer13 + "]");
					throw shaderParseException17;
				}
				++integer13;
			}
		}
	}

	private void parseUniform(JsonElement jsonUniform) {
		JsonObject jsonObject3 = JsonHelper.asObject(jsonUniform, "uniform");
		String string4 = JsonHelper.getString(jsonObject3, "name");
		GlUniform glUniform5 = this.passes.get(this.passes.size() - 1).getProgram().getUniformByName(string4);
		if (glUniform5 == null) {
			throw new ShaderParseException("Uniform '" + string4 + "' does not exist");
		}
		float[] array = new float[4];
		int integer7 = 0;
		for (final JsonElement jsonElement2 : JsonHelper.getArray(jsonObject3, "values")) {
			try {
				array[integer7] = JsonHelper.asFloat(jsonElement2, "value");
			}
			catch (Exception exception) {
				ShaderParseException shaderParseException12 = ShaderParseException.wrap(exception);
				shaderParseException12.addFaultyElement("values[" + integer7 + "]");
				throw shaderParseException12;
			}
			++integer7;
		}
		switch (integer7) {
			case 1: {
				glUniform5.method_6976(array[0]);
				break;
			}
			case 2: {
				glUniform5.method_6977(array[0], array[1]);
				break;
			}
			case 3: {
				glUniform5.method_6978(array[0], array[1], array[2]);
				break;
			}
			case 4: {
				glUniform5.method_6979(array[0], array[1], array[2], array[3]);
				break;
			}
		}
	}

	public void addTarget(String name, int width, int height) {
		Framebuffer framebuffer5 = new Framebuffer(width, height, true);
		this.targetsByName.put(name, framebuffer5);
		if (width == this.width && height == this.height) {
			this.defaultSizedTargets.add(framebuffer5);
		}
	}

	public void disable() {
		Iterator<Framebuffer> iterator2 = this.targetsByName.values().iterator();
		while (iterator2.hasNext()) {
			iterator2.next().delete();
		}
		Iterator<PostProcessShader> iterator2_ = this.passes.iterator();
		while (iterator2_.hasNext()) {
			iterator2_.next().method_6959();
		}
		this.passes.clear();
	}

	public PostProcessShader addPass(String programName, Framebuffer source, Framebuffer dest) {
		PostProcessShader postProcessShader5 = new PostProcessShader(this.resurceManager, programName, source, dest);
		this.passes.add(this.passes.size(), postProcessShader5);
		return postProcessShader5;
	}

	private void setupProjectionMatrix() {
		(this.field_8044 = new Matrix4f()).setIdentity();
		this.field_8044.m00 = 2.0f / this.framebuffer.textureWidth;
		this.field_8044.m11 = 2.0f / -this.framebuffer.textureHeight;
		this.field_8044.m22 = -0.0020001999f;
		this.field_8044.m33 = 1.0f;
		this.field_8044.m03 = -1.0f;
		this.field_8044.m13 = 1.0f;
		this.field_8044.m23 = -1.0001999f;
	}

	public void setupDimensions(int targetsWidth, int targetsHeight) {
		this.width = this.framebuffer.textureWidth;
		this.height = this.framebuffer.textureHeight;
		this.setupProjectionMatrix();
		Iterator<PostProcessShader> iterator4 = this.passes.iterator();
		while (iterator4.hasNext()) {
			iterator4.next().method_6958(this.field_8044);
		}
		Iterator<Framebuffer> iterator4_ = this.defaultSizedTargets.iterator();
		while (iterator4_.hasNext()) {
			iterator4_.next().resize(targetsWidth, targetsHeight);
		}
	}

	public void render(float tickDelta) {
		if (tickDelta < this.lastTickDelta) {
			this.time += 1.0f - this.lastTickDelta;
			this.time += tickDelta;
		}
		else {
			this.time += tickDelta - this.lastTickDelta;
		}
		this.lastTickDelta = tickDelta;
		while (this.time > 20.0f) {
			this.time -= 20.0f;
		}
		Iterator<PostProcessShader> iterator3 = this.passes.iterator();
		while (iterator3.hasNext()) {
			iterator3.next().render(this.time / 20.0f);
		}
	}

	public final String getName() {
		return this.name;
	}
	
	public Framebuffer getSecondaryTarget(String name) {
		return (Framebuffer)this.targetsByName.get(name);
	}

	private Framebuffer getTarget(String name) {
		if (name == null) {
			return null;
		}
		if (name.equals("minecraft:main")) {
			return this.framebuffer;
		}
		return this.targetsByName.get(name);
	}
}
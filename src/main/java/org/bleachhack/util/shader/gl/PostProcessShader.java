package org.bleachhack.util.shader.gl;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GLX;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Tessellator;
import net.minecraft.resource.ResourceManager;

public class PostProcessShader {
	private final JsonGlProgram jsonGlProgram;
	public final Framebuffer input;
	public final Framebuffer output;
	public final List<Object> samplerValues;
	private final List<String> samplerNames;
	private final List<Integer> samplerWidths;
	private final List<Integer> samplerHeights;
	private Matrix4f field_8056;

	public PostProcessShader(ResourceManager resourceManager, String string, Framebuffer framebuffer4, Framebuffer framebuffer5) {
		this.samplerValues = Lists.newArrayList();
		this.samplerNames = Lists.newArrayList();
		this.samplerWidths = Lists.newArrayList();
		this.samplerHeights = Lists.newArrayList();
		this.jsonGlProgram = new JsonGlProgram(resourceManager, string);
		this.input = framebuffer4;
		this.output = framebuffer5;
	}

	public void method_6959() {
		this.jsonGlProgram.method_6931();
	}

	public void addAuxTarget(String name, Object target, int width, int height) {
		this.samplerNames.add(this.samplerNames.size(), name);
		this.samplerValues.add(this.samplerValues.size(), target);
		this.samplerWidths.add(this.samplerWidths.size(), width);
		this.samplerHeights.add(this.samplerHeights.size(), height);
	}

	private void method_6961() {
        /*GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(GL11.GL_BLEND);
        //GL11.glDisable(GL11.GL_DEPTH_TEST);
        //GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_COLOR_MATERIAL);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);*/
    }

	public void method_6958(Matrix4f matrix4f) {
		this.field_8056 = matrix4f;
	}

	public void render(float time) {
		this.method_6961();
        this.input.endWrite();
        float float3 = (float)this.output.textureWidth;
        float float4 = (float)this.output.textureHeight;
        //GL11.glViewport(0, 0, (int)float3, (int)float4);
        this.jsonGlProgram.bindSampler("DiffuseSampler", this.input);
        for (int i = 0; i < this.samplerValues.size(); ++i) {
            this.jsonGlProgram.bindSampler((String)this.samplerNames.get(i), this.samplerValues.get(i));
            this.jsonGlProgram.method_6937("AuxSize" + i).method_6977((float)(int)this.samplerWidths.get(i), (float)(int)this.samplerHeights.get(i));
        }
        this.jsonGlProgram.method_6937("ProjMat").method_6983(this.field_8056);
        this.jsonGlProgram.method_6937("InSize").method_6977((float)this.input.textureWidth, (float)this.input.textureHeight);
        this.jsonGlProgram.method_6937("OutSize").method_6977(float3, float4);
        this.jsonGlProgram.method_6937("Time").method_6976(time);
        MinecraftClient minecraftClient5 = MinecraftClient.getInstance();
        this.jsonGlProgram.method_6937("ScreenSize").method_6977((float)minecraftClient5.width, (float)minecraftClient5.height);
        this.jsonGlProgram.enable();
        //this.output.clear();
        this.output.bind(false);
        //GL11.glDepthMask(false);
        //GL11.glColorMask(true, true, true, true);
        
        Framebuffer.enable2D((int) float3, (int) float4, false);
        Tessellator tessellator6 = Tessellator.INSTANCE;
        GLX.gl13MultiTexCoord2f(GLX.lightmapTextureUnit, 240f, 240f);
        tessellator6.method_1405();
        tessellator6.method_1413(-1);
        tessellator6.method_1398(0.0, float4, 0.0);
        tessellator6.method_1398(float3, float4, 0.0);
        tessellator6.method_1398(float3, 0.0, 0.0);
        tessellator6.method_1398(0.0, 0.0, 0.0);
        tessellator6.method_1396();
        Framebuffer.disable2D((int) float3, (int) float4, false);
        
        //GL11.glDepthMask(true);
        //GL11.glColorMask(true, true, true, true);
        this.jsonGlProgram.disable();
        this.output.endWrite();
        this.input.endRead();
        
        for (final Object next : this.samplerValues) {
            if (next instanceof Framebuffer) {
                ((Framebuffer) next).endRead();
            }
        }
	}

	public JsonGlProgram getProgram() {
		return this.jsonGlProgram;
	}
}


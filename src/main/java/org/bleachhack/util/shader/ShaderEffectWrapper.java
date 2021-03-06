package org.bleachhack.util.shader;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import org.bleachhack.mixin.AccessorMinecraftClient;
import org.bleachhack.util.shader.gl.Framebuffer;
import org.bleachhack.util.shader.gl.PostProcessShader;
import org.bleachhack.util.shader.gl.ShaderEffect;

import net.minecraft.client.MinecraftClient;

public class ShaderEffectWrapper {

	private static MinecraftClient mc = MinecraftClient.getInstance();

	private ShaderEffect shader;
	private int lastWidth = -1;
	private int lastHeight = -1;
	private Map<String, Function<Integer, float[]>> uniforms = new HashMap<>();

	public ShaderEffectWrapper(ShaderEffect effect) {
		this.shader = effect;
	}

	public void prepare() {
		if (lastWidth != mc.width || lastHeight != mc.height)
			resizeShader();

		for (int i = 0; i < shader.passes.size(); i++) {
			PostProcessShader p = shader.passes.get(i);
			for (Entry<String, Function<Integer, float[]>> e: uniforms.entrySet()) {
				float[] dabruh = e.getValue().apply(i);
				if (dabruh == null)
					continue;
	
				switch (dabruh.length) {
					case 1:
						p.getProgram().getUniformByName(e.getKey()).method_6976(dabruh[0]);
						break;
					case 2:
						p.getProgram().getUniformByName(e.getKey()).method_6977(dabruh[0], dabruh[1]);
						break;
					case 3:
						p.getProgram().getUniformByName(e.getKey()).method_6978(dabruh[0], dabruh[1], dabruh[2]);
						break;
					default:
						p.getProgram().getUniformByName(e.getKey()).method_6979(dabruh[0], dabruh[1], dabruh[2], dabruh[3]);
						break;
				}
			}
		}
	}

	public void renderShader() {
		Framebuffer.main.bind(false);
		shader.render(((AccessorMinecraftClient) mc).getTricker().tickDelta);
		Framebuffer.main.bind(false);
	}

	public void clearFramebuffer(String framebuffer) {
		shader.getSecondaryTarget(framebuffer).clear();
		Framebuffer.main.bind(false);
	}

	public void drawFramebuffer(String framebuffer) {
		shader.getSecondaryTarget(framebuffer).drawSimple(mc.width, mc.height);
		Framebuffer.main.bind(false);
	}

	private void resizeShader() {
		shader.setupDimensions(mc.width, mc.height);
		lastWidth = mc.width;
		lastHeight = mc.height;
	}

	public Map<String, Function<Integer, float[]>> getUniforms() {
		return uniforms;
	}

	public ShaderEffect getShader() {
		return shader;
	}
}

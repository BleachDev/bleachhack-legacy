package org.bleachhack.util.shader;

import java.io.IOException;

import org.bleachhack.util.shader.gl.Framebuffer;
import org.bleachhack.util.shader.gl.ShaderEffect;

import com.google.gson.JsonSyntaxException;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class ShaderLoader {

	public static ShaderEffect loadEffect(Framebuffer framebuffer, Identifier id) throws JsonSyntaxException, IOException {
		ResourceManager resMang = MinecraftClient.getInstance().getResourceManager();
		TextureManager texMang = MinecraftClient.getInstance().getTextureManager();
	
		return new ShaderEffect(texMang, new OpenResourceManager(resMang), framebuffer, id);
	}

}

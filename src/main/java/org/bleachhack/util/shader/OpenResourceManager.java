package org.bleachhack.util.shader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

/**
 * A open resource manager that can get resources from Fabric mods using their namespace
 * or from a URL with the __url__ namespace and an encoded path.
 * 
 * USE WITH CAUTION!
 */
public class OpenResourceManager implements ResourceManager {

	private static Pattern DECODE_PATTERN = Pattern.compile("_([0-9]+)_");

	private ResourceManager parent;

	public OpenResourceManager(ResourceManager parent) {
		this.parent = parent;
	}

	public OpenResourceManager(ResourceManager parent, Function<Identifier, InputStream> customResources) {
		this.parent = parent;
	}

	@Override
	public Resource getResource(Identifier id) {
		if ("minecraft".equals(id.getNamespace()))
			return parent.getResource(id);

		if ("__url__".equals(id.getNamespace())) {
			try {
				return new ResourceImpl(id, parseURL(id.getPath()), null, null);
			} catch (IOException e) {
				throw new RuntimeException(id.toString());
			}
		}

		// Scuffed resource loader
		Path path = FabricLoader.getInstance().getModContainer(id.getNamespace()).get().getPath("assets/" + id.getNamespace() + "/" + id.getPath());
		try {
			return new ResourceImpl(id, Files.newInputStream(path), null, null);
		} catch (IOException e) {
			throw new RuntimeException(id.toString());
		}
	}

	@Override
	public Set<?> getAllNamespaces() {
		return parent.getAllNamespaces();
	}


	@Override
	public List<?> getAllResources(Identifier id) {
		return parent.getAllResources(id);
	}

	private InputStream parseURL(String path) throws IOException {
		Matcher m = DECODE_PATTERN.matcher(path);
		while(m.find())
			m.replaceFirst(m.group(Character.toString((char) Integer.parseInt(m.group(1)))));
		return new URL(path).openStream();
	}

}

package org.bleachhack.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;

public enum OperatingSystem {
	LINUX,
	SOLARIS,
	WINDOWS {
		protected String[] getURLOpenCommand(URL url) {
			return new String[]{"rundll32", "url.dll,FileProtocolHandler", url.toString()};
		}
	},
	OSX {
		protected String[] getURLOpenCommand(URL url) {
			return new String[]{"open", url.toString()};
		}
	},
	UNKNOWN;

	public void open(URL url) {
		try {
			Process process = Runtime.getRuntime().exec(this.getURLOpenCommand(url));
			Iterator<String> var3 = IOUtils.readLines(process.getErrorStream(), StandardCharsets.UTF_8).iterator();

			while(var3.hasNext()) {
				String string = (String)var3.next();
				BleachLogger.logger.error(string);
			}

			process.getInputStream().close();
			process.getErrorStream().close();
			process.getOutputStream().close();
		} catch (IOException var5) {
			BleachLogger.logger.error((String)"Couldn't open url '{}'", (Object)url, (Object)var5);
		}

	}

	public void open(URI uri) {
		try {
			this.open(uri.toURL());
		} catch (MalformedURLException var3) {
			BleachLogger.logger.error((String)"Couldn't open uri '{}'", (Object)uri, (Object)var3);
		}

	}

	public void open(File file) {
		try {
			this.open(file.toURI().toURL());
		} catch (MalformedURLException var3) {
			BleachLogger.logger.error((String)"Couldn't open file '{}'", (Object)file, (Object)var3);
		}

	}

	protected String[] getURLOpenCommand(URL url) {
		String string = url.toString();
		if ("file".equals(url.getProtocol())) {
			string = string.replace("file:", "file://");
		}

		return new String[]{"xdg-open", string};
	}

	public void open(String uri) {
		try {
			this.open((new URI(uri)).toURL());
		} catch (MalformedURLException | IllegalArgumentException | URISyntaxException var3) {
			BleachLogger.logger.error((String)"Couldn't open uri '{}'", (Object)uri, (Object)var3);
		}

	}
	
	public static OperatingSystem getOS() {
		String var0 = System.getProperty("os.name").toLowerCase();
		if (var0.contains("win")) {
			return OperatingSystem.WINDOWS;
		} else if (var0.contains("mac")) {
			return OperatingSystem.OSX;
		} else if (var0.contains("solaris")) {
			return OperatingSystem.SOLARIS;
		} else if (var0.contains("linux")) {
			return OperatingSystem.LINUX;
		} else {
			return var0.contains("unix") ? OperatingSystem.LINUX : OperatingSystem.UNKNOWN;
		}
	}
}

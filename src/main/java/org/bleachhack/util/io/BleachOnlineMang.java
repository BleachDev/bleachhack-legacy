/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.util.io;

import org.bleachhack.util.BleachLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Utils for online BleachHack Resources.
 */
public class BleachOnlineMang {

	private static final URI RESOURCE_URL = URI.create("https://bleachhack.org/resources/");
	private static final URI API_URL = URI.create("http://api0.bleachhack.org/"); // using api0 because of compatibility with BH 1.2.1 and under.

	public static URI getResourceUrl() {
		return RESOURCE_URL;
	}

	public static URI getApiUrl() {
		return API_URL;
	}

	public static HttpResponse getResource(String path) {
		BleachLogger.logger.info("Getting Resource (/" + path + ")");
		return sendRequest(RESOURCE_URL.resolve(path), "GET", null, null, 5000);
	}

	public static CompletableFuture<HttpResponse> getResourceAsync(String path) {
		BleachLogger.logger.info("Getting Resource (/" + path + ")");
		return sendAsyncRequest(RESOURCE_URL.resolve(path), "GET", null, null, 5000);
	}

	public static HttpResponse sendApiGet(String path) {
		BleachLogger.logger.info("Trying to call API (GET, /" + path + ")");
		return sendRequest(API_URL.resolve(path), "GET", null, null, 5000);
	}

	public static HttpResponse sendApiPost(String path, String body) {
		BleachLogger.logger.info("Trying to call API (POST, /" + path + ", " + body + ")");
		return sendRequest(API_URL.resolve(path), "POST", null, body, 5000);
	}

	public static <T> HttpResponse sendRequest(URI url, String method, String[] headers, String body, int timeout) {
		try {
			HttpURLConnection con = (HttpURLConnection) url.toURL().openConnection();
			con.setReadTimeout(timeout);
			con.setRequestMethod(method);
			if (body != null) {
				con.setDoOutput(true);
				try (OutputStream os = con.getOutputStream()) {
					byte[] input = body.getBytes(StandardCharsets.UTF_8);
					os.write(input, 0, input.length);			
				}
			}

			if (headers != null) {
				for (int i = 0; i < headers.length; i += 2) {
					con.getHeaderFields().putIfAbsent(headers[i], new ArrayList<>());
					con.getHeaderFields().get(headers[i]).add(headers[i + 1]);
				}
			}

			try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
				String line;
				List<String> list = new ArrayList<>();
				while ((line = br.readLine()) != null) {
					list.add(line);
				}
				
				return new HttpResponse(list, con.getResponseCode());
			}
		} catch (IOException e) {
			BleachLogger.logger.error(e);
			return null;
		}
	}

	public static <T> CompletableFuture<HttpResponse> sendAsyncRequest(URI url, String method, String[] headers, String body, int timeout) {
		return CompletableFuture.supplyAsync(() -> sendRequest(url, method, headers, body, timeout));
	}

	public static class HttpResponse {

		private int code;
		private List<String> list = new ArrayList<>();

		public HttpResponse(List<String> l, int code) {
			this.list = l;
			this.code = code;
		}

		public String bodyAsString() {
			return list.stream().collect(Collectors.joining("\n"));
		}

		public List<String> bodyAsLines() {
			return list;
		}
		
		public int getCode() {
			return code;
		}
	}
}
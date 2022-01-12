package org.bleachhack.util.shader.gl;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

public class JsonHelper {

	public static boolean hasString(JsonObject object, String element) {
		if (!JsonHelper.hasPrimitive(object, element)) {
			return false;
		}
		return object.getAsJsonPrimitive(element).isString();
	}

	public static boolean isString(JsonElement element) {
		if (!element.isJsonPrimitive()) {
			return false;
		}
		return element.getAsJsonPrimitive().isString();
	}

	public static boolean hasArray(JsonObject object, String element) {
		if (!JsonHelper.hasElement(object, element)) {
			return false;
		}
		return object.get(element).isJsonArray();
	}

	public static boolean hasPrimitive(JsonObject object, String element) {
		if (!JsonHelper.hasElement(object, element)) {
			return false;
		}
		return object.get(element).isJsonPrimitive();
	}

	public static boolean hasElement(JsonObject object, String lement) {
		if (object == null) {
			return false;
		}
		return object.get(lement) != null;
	}

	public static String asString(JsonElement element, String name) {
		if (element.isJsonPrimitive()) {
			return element.getAsString();
		}
		throw new JsonSyntaxException("Expected " + name + " to be a string, was " + JsonHelper.getType(element));
	}

	public static String getString(JsonObject object, String element) {
		if (object.has(element)) {
			return JsonHelper.asString(object.get(element), element);
		}
		throw new JsonSyntaxException("Missing " + element + ", expected to find a string");
	}

	public static String getString(JsonObject object, String element, String defaultStr) {
		if (object.has(element)) {
			return JsonHelper.asString(object.get(element), element);
		}
		return defaultStr;
	}

	public static boolean asBoolean(JsonElement element, String name) {
		if (element.isJsonPrimitive()) {
			return element.getAsBoolean();
		}
		throw new JsonSyntaxException("Expected " + name + " to be a Boolean, was " + JsonHelper.getType(element));
	}

	public static boolean getBoolean(JsonObject object, String element) {
		if (object.has(element)) {
			return JsonHelper.asBoolean(object.get(element), element);
		}
		throw new JsonSyntaxException("Missing " + element + ", expected to find a Boolean");
	}

	public static boolean getBoolean(JsonObject object, String element, boolean defaultBoolean) {
		if (object.has(element)) {
			return JsonHelper.asBoolean(object.get(element), element);
		}
		return defaultBoolean;
	}

	public static float asFloat(JsonElement element, String name) {
		if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
			return element.getAsFloat();
		}
		throw new JsonSyntaxException("Expected " + name + " to be a Float, was " + JsonHelper.getType(element));
	}

	public static float getFloat(JsonObject object, String element) {
		if (object.has(element)) {
			return JsonHelper.asFloat(object.get(element), element);
		}
		throw new JsonSyntaxException("Missing " + element + ", expected to find a Float");
	}

	public static float getFloat(JsonObject object, String element, float defaultFloat) {
		if (object.has(element)) {
			return JsonHelper.asFloat(object.get(element), element);
		}
		return defaultFloat;
	}

	public static int asInt(JsonElement element, String name) {
		if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
			return element.getAsInt();
		}
		throw new JsonSyntaxException("Expected " + name + " to be a Int, was " + JsonHelper.getType(element));
	}

	public static int getInt(JsonObject object, String element) {
		if (object.has(element)) {
			return JsonHelper.asInt(object.get(element), element);
		}
		throw new JsonSyntaxException("Missing " + element + ", expected to find a Int");
	}

	public static int getInt(JsonObject object, String element, int defaultInt) {
		if (object.has(element)) {
			return JsonHelper.asInt(object.get(element), element);
		}
		return defaultInt;
	}

	public static JsonObject asObject(JsonElement element, String name) {
		if (element.isJsonObject()) {
			return element.getAsJsonObject();
		}
		throw new JsonSyntaxException("Expected " + name + " to be a JsonObject, was " + JsonHelper.getType(element));
	}

	public static JsonObject getObject(JsonObject object, String element) {
		if (object.has(element)) {
			return JsonHelper.asObject(object.get(element), element);
		}
		throw new JsonSyntaxException("Missing " + element + ", expected to find a JsonObject");
	}

	public static JsonObject getObject(JsonObject object, String element, JsonObject defaultObject) {
		if (object.has(element)) {
			return JsonHelper.asObject(object.get(element), element);
		}
		return defaultObject;
	}

	public static JsonArray asArray(JsonElement element, String name) {
		if (element.isJsonArray()) {
			return element.getAsJsonArray();
		}
		throw new JsonSyntaxException("Expected " + name + " to be a JsonArray, was " + JsonHelper.getType(element));
	}

	public static JsonArray getArray(JsonObject object, String element) {
		if (object.has(element)) {
			return JsonHelper.asArray(object.get(element), element);
		}
		throw new JsonSyntaxException("Missing " + element + ", expected to find a JsonArray");
	}

	public static JsonArray getArray(JsonObject object, String name, JsonArray defaultArray) {
		if (object.has(name)) {
			return JsonHelper.asArray(object.get(name), name);
		}
		return defaultArray;
	}

	public static String getType(JsonElement element) {
		String string = StringUtils.abbreviateMiddle((String)String.valueOf(element), (String)"...", (int)10);
		if (element == null) {
			return "null (missing)";
		}
		if (element.isJsonNull()) {
			return "null (json)";
		}
		if (element.isJsonArray()) {
			return "an array (" + string + ")";
		}
		if (element.isJsonObject()) {
			return "an object (" + string + ")";
		}
		if (element.isJsonPrimitive()) {
			JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
			if (jsonPrimitive.isNumber()) {
				return "a number (" + string + ")";
			}
			if (jsonPrimitive.isBoolean()) {
				return "a boolean (" + string + ")";
			}
		}
		return string;
	}
}

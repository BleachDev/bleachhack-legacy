package org.bleachhack.setting;

import java.util.Collection;
import java.util.function.Supplier;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class SettingDataHandlers {

	public static final SettingDataHandler<Boolean> BOOLEAN = new SettingDataHandler<>() {
		public JsonElement write(Boolean value) {
			return new JsonPrimitive(value);
		}

		public Boolean read(JsonElement json) {
			return json.getAsBoolean();
		}
	};

	public static final SettingDataHandler<Integer> INTEGER = new SettingDataHandler<>() {
		public JsonElement write(Integer value) {
			return new JsonPrimitive(value);
		}

		public Integer read(JsonElement json) {
			return json.getAsInt();
		}
	};

	public static final SettingDataHandler<Double> DOUBLE = new SettingDataHandler<>() {
		public JsonElement write(Double value) {
			return new JsonPrimitive(value);
		}

		public Double read(JsonElement json) {
			return json.getAsDouble();
		}
	};
	
	public static final SettingDataHandler<float[]> FLOAT_ARRAY = new SettingDataHandler<>() {
		public JsonElement write(float[] value) {
			JsonArray array = new JsonArray();
			for (float f: value)
				array.add(new JsonPrimitive(f));
			
			return array;
		}

		public float[] read(JsonElement json) {
			JsonArray array = json.getAsJsonArray();
			float[] farray = new float[array.size()];
			for (int i = 0; i < array.size(); i++)
				farray[i] = array.get(i).getAsFloat();
			
			return farray;
		}
	};

	public static final SettingDataHandler<String> STRING = new SettingDataHandler<>() {
		public JsonElement write(String value) {
			return new JsonPrimitive(value);
		}

		public String read(JsonElement json) {
			return json.getAsString();
		}
	};
	
	public static final SettingDataHandler<Block> BLOCK = new SettingDataHandler<>() {
		public JsonElement write(Block value) {
			return new JsonPrimitive(value.getTranslationKey());
		}

		public Block read(JsonElement json) {
			String key = json.getAsString();
			for (Block b: Block.field_492) {
				if (b.getTranslationKey().equals(key))
					return b;
			}

			return null;
		}
	};
	
	public static final SettingDataHandler<Item> ITEM = new SettingDataHandler<>() {
		public JsonElement write(Item value) {
			return new JsonPrimitive(value.getTranslationKey());
		}

		public Item read(JsonElement json) {
			String key = json.getAsString();
			for (Item i: Item.field_4343) {
				if (i.getTranslationKey().equals(key))
					return i;
			}
			
			return null;
		}
	};
	
	public static <T, C extends Collection<T>> SettingDataHandler<C> ofCollection(SettingDataHandler<T> handler, Supplier<C> supplier) {
		return new SettingDataHandler<C>() {
			public JsonElement write(C values) {
				JsonArray array = new JsonArray();
				for (T val: values)
					array.add(handler.write(val));

				return array;
			}

			public C read(JsonElement json) {
				C collection = supplier.get();
				JsonArray array = json.getAsJsonArray();
				for (JsonElement j: array) {
					collection.add(handler.read(j));
				}

				return collection;
			}
		};
	}
}

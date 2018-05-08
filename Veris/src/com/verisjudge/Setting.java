package com.verisjudge;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Setting<T> implements Comparable<Setting<?>> {

	private final String key;
	private T value;
	
	public Setting(String key, T value) {
		if (key == null)
			throw new IllegalArgumentException("Key cannot be null!");
		this.key = key;
		this.value = value;
	}
	
	public static Setting<?> fromJsonElement(String key, JsonElement jsonElement) {
		if (key == null)
			throw new IllegalArgumentException("Key cannot be null!");
		if (jsonElement == null)
			throw new IllegalArgumentException("JsonElement cannot be null!");
		
		if (jsonElement.isJsonNull()) {
			return new Setting<Object>(key, null);
		} else if (jsonElement.isJsonObject()) {
			return new Setting<JsonObject>(key, jsonElement.getAsJsonObject());
		} else if (jsonElement.isJsonArray()) {
			return new Setting<JsonArray>(key, jsonElement.getAsJsonArray());
		} else if (jsonElement.isJsonPrimitive()) {
			JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
			if (jsonPrimitive.isBoolean()) {
				return new Setting<Boolean>(key, jsonPrimitive.getAsBoolean());
			} else if (jsonPrimitive.isString()) {
				return new Setting<String>(key, jsonPrimitive.getAsString());
			} else if (jsonPrimitive.isNumber()) {
				try {
					long longValue = jsonPrimitive.getAsLong();
					return new Setting<Long>(key, longValue);
				} catch (NumberFormatException e) {
					// Ignore error.
				}
				try {
					double doubleValue = jsonPrimitive.getAsDouble();
					return new Setting<Double>(key, doubleValue);
				} catch (NumberFormatException e) {
					// Ignore error.
				}
				return null;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	/**
	 * Gets the unique key identifying this setting.
	 * @return The key identifying this setting.
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * Gets the value of this setting.
	 * @return The value of this setting.
	 */
	public T get() {
		return value;
	}
	
	/**
	 * Gets the value of this setting but resorts to the default value if the value is null.
	 * @param defaultValue The default to resort to if the value is null.
	 * @return The value of this setting or defaultValue if the value is null.
	 */
	public T getOrDefault(T defaultValue) {
		return value == null ? defaultValue : value;
	}
	
	/**
	 * Sets the value of this setting.
	 * @param value The new value to set to.
	 * @return The previous value of this setting.
	 */
	public T set(T value) {
		T previous = get();
		this.value = value;
		return previous;
	}
	
	/**
	 * Clears the value of this setting (sets it to null).
	 * @return The previous value of this setting.
	 */
	public T clear() {
		return set(null);
	}
	
	/**
	 * Returns whether or not this setting has the same key as the one given.
	 * @param setting The setting to test against.
	 * @return True if this setting has the same key as the one given or false otherwise.
	 */
	public boolean isSame(Setting<?> setting) {
		return getKey().equals(setting.getKey());
	}
	
	@Override
	public int compareTo(Setting<?> setting) {
		return key.compareTo(setting.getKey());
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Setting<?>))
			return false;
		Setting<?> s = (Setting<?>) o;
		if (!getKey().equals(s.getKey()))
			return false;
		if (get() == null && s.get() != null)
			return false;
		if (get() != null && s.get() == null)
			return false;
		if (!get().getClass().equals(s.get().getClass()))
			return false;
		if (!get().equals(s.get()))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return getKey() + " -> " + get();
	}
}

package com.verisjudge;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Settings {

	private final Map<String, Setting<?>> settings;
	
	public Settings() {
		this.settings = new HashMap<>();
	}
	
	public Settings(Collection<Setting<?>> settings) {
		this.settings = new HashMap<>();
		for (Setting<?> setting : settings) {
			if (!this.settings.containsKey(setting.getKey())) {
				this.settings.put(setting.getKey(), setting);
			} else {
				throw new IllegalArgumentException("Found more than one setting with the key '" + setting.getKey() + "'!");
			}
		}
	}
	
	/**
	 * Gets the setting object with the given key.
	 * @param key The key defining the setting to get.
	 * @return The setting with the given key or null if no setting exists.
	 * @throws IllegalArgumentException if key is null.
	 */
	public Setting<?> getSetting(String key) throws IllegalArgumentException {
		return settings.get(key);
	}
	
	/**
	 * Adds a setting or replaces the current setting if one exists with the same key.
	 * @param setting The setting to be added.
	 * @return The setting being replaced or null if no previous setting existed with the same key.
	 * @throws IllegalArgumentException if setting is null.
	 */
	public Setting<?> add(Setting<?> setting) throws IllegalArgumentException {
		if (setting == null) {
			throw new IllegalArgumentException("Setting cannot be null!");
		}
		Setting<?> previous = getSetting(setting.getKey());
		settings.put(setting.getKey(), setting);
		return previous;
	}
	
	/**
	 * Creates and adds a new setting key -> value
	 * @param key The unique key identifier for this setting.
	 * @param value The value for this setting (can be null).
	 * @return The setting being replaced or null if no previous setting existed with the same key.
	 * @throws IllegalArgumentException if key is null.
	 */
	public Setting<?> set(String key, Object value) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException("Key cannot be null!");
		}
		Setting<?> setting = new Setting<>(key, value);
		return add(setting);
	}
	
	/**
	 * Removes all settings.
	 */
	public void clear() {
		settings.clear();
	}
	
	/**
	 * Clears all of the settings.
	 */
	public void clearAll() {
		for (Setting<?> setting : settings.values())
			setting.clear();
	}
	
	/**
	 * Clears the setting (if it exists) with the key given.
	 * @param key The key of the setting to be cleared.
	 * @return The setting object that is now cleared or null if no setting with the given key existed.
	 * @throws IllegalArgumentException if key is null.
	 */
	public Setting<?> clear(String key) {
		if (key == null) {
			throw new IllegalArgumentException("Key cannot be null!");
		}
		Setting<?> setting = getSetting(key);
		if (setting != null)
			setting.clear();
		return setting;
	}
	
	/**
	 * Removes the setting (if it exists) with the key given.
	 * @param key The key of the setting to be removed.
	 * @return The setting object that was removed or null if no setting with the given key existed.
	 * @throws IllegalArgumentException if key is null.
	 */
	public Setting<?> remove(String key) {
		if (key == null) {
			throw new IllegalArgumentException("Key cannot be null!");
		}
		Setting<?> setting = getSetting(key);
		if (setting != null)
			settings.remove(key);
		return setting;
	}
	
	/**
	 * Gets the number of settings.
	 * @return The number of settings.
	 */
	public int size() {
		return settings.size();
	}
	
	public String toJsonString() {
		JsonObject jsonObject = toJsonObject();
		return jsonObject.toString();
	}
	
	public JsonObject toJsonObject() {
		JsonObject jsonObject = new JsonObject();
		for (Setting<?> setting : settings.values()) {
			String key = setting.getKey();
			Object value = setting.get();
			if (value == null) {
				jsonObject.add(key, JsonNull.INSTANCE);
			} else if (value instanceof String) {
				jsonObject.add(key, new JsonPrimitive((String) value));
			} else if (value instanceof Number) {
				jsonObject.add(key, new JsonPrimitive((Number) value));
			} else if (value instanceof Boolean) {
				jsonObject.add(key, new JsonPrimitive((Boolean) value));
			} else if (value instanceof Character) {
				jsonObject.add(key, new JsonPrimitive((Character) value));
			} else if (value instanceof JsonObject) {
				jsonObject.add(key, (JsonObject) value);
			} else {
				jsonObject.add(key, new JsonPrimitive((String) value));
			}
		}
		return jsonObject;
	}
	
	public static Settings fromJsonObject(JsonObject jsonObject) {
		Settings settings = new Settings();
		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			String key = entry.getKey();
			JsonElement jsonElement = entry.getValue();
			Setting<?> setting = Setting.fromJsonElement(key, jsonElement);
			if (setting != null)
				settings.add(setting);
		}
		return settings;
	}
}

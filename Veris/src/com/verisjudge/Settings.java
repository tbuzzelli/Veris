package com.verisjudge;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.verisjudge.utils.FileUtils;

public class Settings {

	private static Settings SETTINGS;
	private final static String SETTINGS_FILENAME = "settings.json";
	
	public final static String REMEMBER_JUDGING_SETTINGS = "rememberJudgingSettings";
	public final static String PREVIOUS_SOLUTION_PATH = "previousSolutionPath";
	public final static String PREVIOUS_DATA_PATH = "previousDataPath";
	public final static String PREVIOUS_LANGUAGE = "previousLanguage";
	public final static String PREVIOUS_TIME_LIMIT = "previousTimeLimit";
	public final static String PREVIOUS_CHECKER = "previousChecker";
	public final static String PREVIOUS_TOKEN_CHECKER_CASE_SENSITIVE = "previousTokenCheckerCaseSensitive";
	public final static String PREVIOUS_DIFF_CHECKER_IGNORE_TRAILING_WHITESPACE = "previousDiffCheckerIgnoreTrailingWhitespace";
	public final static String PREVIOUS_DIFF_CHECKER_IGNORE_TRAILING_BLANKLINES = "previousDiffCheckerIgnoreTrailingBlanklines";
	public final static String PREVIOUS_EPSILON_CHECKER_ABSOLUTE_EPSILON = "previousEpsilonCheckerAbsoluteEpsilon";
	public final static String PREVIOUS_EPSILON_CHECKER_RELATIVE_EPSILON = "previousEpsilonCheckerRelativeEpsilon";
	public final static String PREVIOUS_USE_TIME = "previousUseTime";
	public final static String USE_DARK_THEME = "useDarkTheme";
	public final static String SORT_CASES_BY_SIZE = "sortCasesBySize";
	public final static String DEFAULT_TIME_LIMIT = "defaultTimeLimit";
	public final static String STOP_AT_FIRST_NON_CORRECT_VERDICT = "stopAtFirstNonCorrectVerdict";
	
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
	
	public static Settings getSettings() {
		if (SETTINGS != null)
			return SETTINGS;
		SETTINGS = fromJsonString(FileUtils.readEntireFile(FileUtils.getConfigFile(SETTINGS_FILENAME)));
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			saveSettings();
		}
		return SETTINGS;
	}
	
	public static boolean saveSettings() {
		if (SETTINGS != null) {
			return FileUtils.writeStringToFile(FileUtils.getConfigFile(SETTINGS_FILENAME), SETTINGS.toJsonString());
		}
		return false;
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
	 * Gets the value for the setting object with the given key.
	 * @param key The key defining the setting to get.
	 * @return The value of the setting with the given key or null if no setting exists (or the value is null).
	 * @throws IllegalArgumentException if key is null.
	 */
	public Object get(String key) throws IllegalArgumentException {
		Setting<?> setting = getSetting(key);
		if (setting == null)
			return null;
		return setting.get();
	}
	
	/**
	 * Gets the value for the setting object with the given key and defaults to default if the value is null.
	 * @param key The key defining the setting to get.
	 * @param default The default value to resort to if the value is null.
	 * @return The value of the setting with the given key or defaultValue if no setting exists (or the value was null).
	 * @throws IllegalArgumentException if key is null.
	 */
	public Object getOrDefault(String key, Object defaultValue) throws IllegalArgumentException {
		Object value = get(key);
		return value == null ? defaultValue : value;
	}
	
	/**
	 * Gets the String value for the setting object with the given key.
	 * @param key The key defining the setting to get.
	 * @return The value of the setting with the given key or null if no setting exists (or the value is null or not a string).
	 * @throws IllegalArgumentException if key is null.
	 */
	public String getString(String key) throws IllegalArgumentException {
		Object value = get(key);
		if (value == null || !(value instanceof String))
			return null;
		return (String) value;
	}
	
	/**
	 * Gets the String value for the setting object with the given key and defaults to default if the value is null.
	 * @param key The key defining the setting to get.
	 * @param default The default value to resort to if the value is null.
	 * @return The value of the setting with the given key or defaultValue if no setting exists (or the value was null or not a string).
	 * @throws IllegalArgumentException if key is null.
	 */
	public String getStringOrDefault(String key, String defaultValue) throws IllegalArgumentException {
		String value = getString(key);
		return value == null ? defaultValue : value;
	}
	
	/**
	 * Gets the String value for the setting object with the given key.
	 * @param key The key defining the setting to get.
	 * @return The value of the setting with the given key or null if no setting exists (or the value is null or not a string).
	 * @throws IllegalArgumentException if key is null.
	 */
	public Boolean getBoolean(String key) throws IllegalArgumentException {
		Object value = get(key);
		if (value == null || !(value instanceof Boolean))
			return null;
		return (Boolean) value;
	}
	
	/**
	 * Gets the Boolean value for the setting object with the given key and defaults to default if the value is null.
	 * @param key The key defining the setting to get.
	 * @param default The default value to resort to if the value is null.
	 * @return The value of the setting with the given key or defaultValue if no setting exists (or the value was null or not a boolean).
	 * @throws IllegalArgumentException if key is null.
	 */
	public Boolean getBooleanOrDefault(String key, Boolean defaultValue) throws IllegalArgumentException {
		Boolean value = getBoolean(key);
		return value == null ? defaultValue : value;
	}
	
	/**
	 * Gets the Character value for the setting object with the given key.
	 * @param key The key defining the setting to get.
	 * @return The value of the setting with the given key or null if no setting exists (or the value is null or not a character).
	 * @throws IllegalArgumentException if key is null.
	 */
	public Character getCharacter(String key) throws IllegalArgumentException {
		Object value = get(key);
		if (value == null || !(value instanceof Character))
			return null;
		return (Character) value;
	}
	
	/**
	 * Gets the Character value for the setting object with the given key and defaults to default if the value is null.
	 * @param key The key defining the setting to get.
	 * @param default The default value to resort to if the value is null.
	 * @return The value of the setting with the given key or defaultValue if no setting exists (or the value was null or not a character).
	 * @throws IllegalArgumentException if key is null.
	 */
	public Character getCharacterOrDefault(String key, Character defaultValue) throws IllegalArgumentException {
		Character value = getCharacter(key);
		return value == null ? defaultValue : value;
	}
	
	/**
	 * Gets the Number value for the setting object with the given key.
	 * @param key The key defining the setting to get.
	 * @return The value of the setting with the given key or null if no setting exists (or the value is null or not a Number).
	 * @throws IllegalArgumentException if key is null.
	 */
	public Number getNumber(String key) throws IllegalArgumentException {
		Object value = get(key);
		if (value == null || !(value instanceof Number))
			return null;
		return (Number) value;
	}
	
	/**
	 * Gets the Number value for the setting object with the given key and defaults to default if the value is null.
	 * @param key The key defining the setting to get.
	 * @param default The default value to resort to if the value is null.
	 * @return The value of the setting with the given key or defaultValue if no setting exists (or the value was null or not a Number).
	 * @throws IllegalArgumentException if key is null.
	 */
	public Number getNumberOrDefault(String key, Number defaultValue) throws IllegalArgumentException {
		Number value = getNumber(key);
		return value == null ? defaultValue : value;
	}
	
	/**
	 * Gets the Integer value for the setting object with the given key.
	 * @param key The key defining the setting to get.
	 * @return The value of the setting with the given key or null if no setting exists (or the value is null or not an integer).
	 * @throws IllegalArgumentException if key is null.
	 */
	public Integer getInt(String key) throws IllegalArgumentException {
		Number value = getNumber(key);
		if (value == null)
			return null;
		long longValue = value.longValue();
		if (longValue < Integer.MIN_VALUE || longValue > Integer.MAX_VALUE)
			return null;
		return (int) longValue;
	}
	
	/**
	 * Gets the Integer value for the setting object with the given key and defaults to default if the value is null.
	 * @param key The key defining the setting to get.
	 * @param default The default value to resort to if the value is null.
	 * @return The value of the setting with the given key or defaultValue if no setting exists (or the value was null or not an integer).
	 * @throws IllegalArgumentException if key is null.
	 */
	public Integer getIntOrDefault(String key, Integer defaultValue) throws IllegalArgumentException {
		Integer value = getInt(key);
		return value == null ? defaultValue : value;
	}
	
	/**
	 * Gets the Long value for the setting object with the given key.
	 * @param key The key defining the setting to get.
	 * @return The value of the setting with the given key or null if no setting exists (or the value is null or not a long).
	 * @throws IllegalArgumentException if key is null.
	 */
	public Long getLong(String key) throws IllegalArgumentException {
		Number value = getNumber(key);
		if (value == null)
			return null;
		long longValue = value.longValue();
		return longValue;
	}
	
	/**
	 * Gets the Long value for the setting object with the given key and defaults to default if the value is null.
	 * @param key The key defining the setting to get.
	 * @param default The default value to resort to if the value is null.
	 * @return The value of the setting with the given key or defaultValue if no setting exists (or the value was null or not a long).
	 * @throws IllegalArgumentException if key is null.
	 */
	public Long getLongOrDefault(String key, Long defaultValue) throws IllegalArgumentException {
		Long value = getLong(key);
		return value == null ? defaultValue : value;
	}
	
	/**
	 * Gets the Double value for the setting object with the given key.
	 * @param key The key defining the setting to get.
	 * @return The value of the setting with the given key or null if no setting exists (or the value is null or not a double).
	 * @throws IllegalArgumentException if key is null.
	 */
	public Double getDouble(String key) throws IllegalArgumentException {
		Number value = getNumber(key);
		if (value == null)
			return null;
		return value.doubleValue();
	}
	
	/**
	 * Gets the Double value for the setting object with the given key and defaults to default if the value is null.
	 * @param key The key defining the setting to get.
	 * @param default The default value to resort to if the value is null.
	 * @return The value of the setting with the given key or defaultValue if no setting exists (or the value was null or not a double).
	 * @throws IllegalArgumentException if key is null.
	 */
	public Double getDoubleOrDefault(String key, Double defaultValue) throws IllegalArgumentException {
		Double value = getDouble(key);
		return value == null ? defaultValue : value;
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
	
	public static Settings fromJsonString(String str) {
        JsonParser parser = new JsonParser();
        try {
        	return fromJsonObject(parser.parse(str).getAsJsonObject());
        } catch (Exception e) {
        	e.printStackTrace();
        	return null;
        }
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
	
	@Override
	public String toString() {
		return settings.toString();
	}
}

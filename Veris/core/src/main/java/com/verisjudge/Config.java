package com.verisjudge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.verisjudge.utils.FileUtils;
import com.verisjudge.utils.ParsingUtils;

import javafx.stage.FileChooser.ExtensionFilter;

public class Config {

	public final static String JSON_FIELD_INPUT_FILE_TYPES = "inputFileTypes";
	public final static String JSON_FIELD_OUTPUT_FILE_TYPES = "outputFileTypes";
	public final static String JSON_FIELD_DEFAULT_TIME_LIMIT = "defaultTimeLimit";
	public final static String JSON_FIELD_MINIMUM_TIME_LIMIT = "minimumTimeLimit";
	public final static String JSON_FIELD_MAXIMUM_TIME_LIMIT = "maximumTimeLimit";
	public final static String JSON_FIELD_MAXIMUM_IDLE_TIME = "maximumIdleTime";
	public final static String JSON_FIELD_COMPILE_TIME_LIMIT = "compileTimeLimit";
	public final static String JSON_FIELD_LANGUAGE_SPECS = "languageSpecs";

	public final static String USER_CONFIG_FILENAME = "config.json";
	public final static String DEFAULT_CONFIG_RESOURCE_PATH = "/default_config.json";
	
	private static Config CONFIG;
	
	private final List<String> inputFileTypes;
	private final List<String> outputFileTypes;
	
	private final String defaultTimeLimitString;
	private final String minimumTimeLimitString;
	private final String maximumTimeLimitString;
	
	private final String maximumIdleTimeString;
	private final String compileTimeLimitString;
	
	private final Long defaultTimeLimit;
	private final Long minimumTimeLimit;
	private final Long maximumTimeLimit;
	
	private final Long maximumIdleTime;
	private final Long compileTimeLimit;
	
	private final List<LanguageSpec> languageSpecs;
	private final List<LanguageSpec> languageSpecsForDetectLanguage;
	private final List<LanguageSpec> languageSpecsForDisplay;

	public Config(
			List<String> inputFileTypes,
			List<String> outputFileTypes,
			String defaultTimeLimitString,
			String minimumTimeLimitString,
			String maximumTimeLimitString,
			String maximumIdleTimeString,
			String compileTimeLimitString,
			List<LanguageSpec> languageSpecs
	) {
		if (inputFileTypes == null) {
			throw new IllegalArgumentException("Config inputFileTypes cannot be null!");
		}
		if (outputFileTypes == null) {
			throw new IllegalArgumentException("Config outputFileTypes cannot be null!");
		}
		if (languageSpecs == null) {
			throw new IllegalArgumentException("Config languageSpecs cannot be null!");
		}
		this.inputFileTypes = inputFileTypes.stream()
				.collect(Collectors.toUnmodifiableList());
		this.outputFileTypes = outputFileTypes.stream()
				.collect(Collectors.toUnmodifiableList());
		
		this.defaultTimeLimitString = defaultTimeLimitString;
		this.minimumTimeLimitString = minimumTimeLimitString;
		this.maximumTimeLimitString = maximumTimeLimitString;
		this.maximumIdleTimeString = maximumIdleTimeString;
		this.compileTimeLimitString = compileTimeLimitString;
		
		defaultTimeLimit = ParsingUtils.parseTime(defaultTimeLimitString);
		minimumTimeLimit = ParsingUtils.parseTime(minimumTimeLimitString);
		maximumTimeLimit = ParsingUtils.parseTime(maximumTimeLimitString);
		maximumIdleTime = ParsingUtils.parseTime(maximumIdleTimeString);
		compileTimeLimit = ParsingUtils.parseTime(compileTimeLimitString);
		
		this.languageSpecs = languageSpecs.stream()
				.collect(Collectors.toUnmodifiableList());
		this.languageSpecsForDetectLanguage = languageSpecs.stream()
				.sorted(LanguageSpec.DETECT_LANGUAGE_COMPARATOR)
				.collect(Collectors.toUnmodifiableList());
		this.languageSpecsForDisplay = languageSpecs.stream()
				.sorted(LanguageSpec.DISPLAY_COMPARATOR)
				.collect(Collectors.toUnmodifiableList());
	}
	
	public static Config fromConfigFile(String filename) {
		return fromConfigFile(new File(filename));
	}
	
	public static Config fromConfigFile(File f) {
		try {
            return fromConfigInputStream(new FileInputStream(f));
        } catch (Exception e) {
        	e.printStackTrace();
            return null;
        }
	}
	
	public static Config fromConfigInputStream(InputStream inputStream) {
		return fromJsonString(FileUtils.readEntireInputStream(inputStream));
	}
	
	public static Config fromJsonString(String str) {
        JsonParser parser = new JsonParser();
        try {
        	return fromJson(parser.parse(str).getAsJsonObject());
        } catch (Exception e) {
        	e.printStackTrace();
        	return null;
        }
    }
	
	public static Config fromJson(JsonObject j) {
		Builder builder = new Builder();

		if (j.has(Config.JSON_FIELD_INPUT_FILE_TYPES)) {
			builder.setInputFileTypes(Config.getStringList(
        			j, Config.JSON_FIELD_INPUT_FILE_TYPES));
        }
        
        if (j.has(Config.JSON_FIELD_OUTPUT_FILE_TYPES)) {
        	builder.setOutputFileTypes(Config.getStringList(
        			j, Config.JSON_FIELD_OUTPUT_FILE_TYPES));
        }
        
        if (j.has(Config.JSON_FIELD_DEFAULT_TIME_LIMIT)) {
        	JsonElement element = j.get(Config.JSON_FIELD_DEFAULT_TIME_LIMIT);
        	if (element.isJsonPrimitive()) {
       			builder.setDefaultTimeLimitString(element.getAsString());
        	}
        }
        
        if (j.has(Config.JSON_FIELD_MINIMUM_TIME_LIMIT)) {
        	JsonElement element = j.get(Config.JSON_FIELD_MINIMUM_TIME_LIMIT);
        	if (element.isJsonPrimitive()) {
        		builder.setMinimumTimeLimitString(element.getAsString());
        	}
        }
        
        if (j.has(Config.JSON_FIELD_MAXIMUM_TIME_LIMIT)) {
        	JsonElement element = j.get(Config.JSON_FIELD_MAXIMUM_TIME_LIMIT);
        	if (element.isJsonPrimitive()) {
        		builder.setMaximumTimeLimitString(element.getAsString());
        	}
        }
        
        if (j.has(Config.JSON_FIELD_MAXIMUM_IDLE_TIME)) {
        	JsonElement element = j.get(Config.JSON_FIELD_MAXIMUM_IDLE_TIME);
        	if (element.isJsonPrimitive()) {
        		builder.setMaximumIdleTimeString(element.getAsString());
        	}
        }
        
        if (j.has(Config.JSON_FIELD_COMPILE_TIME_LIMIT)) {
        	JsonElement element = j.get(Config.JSON_FIELD_COMPILE_TIME_LIMIT);
        	if (element.isJsonPrimitive()) {
        		builder.setCompileTimeLimitString(element.getAsString());
        	}
        }
        
        if (j.has(Config.JSON_FIELD_LANGUAGE_SPECS)) {
        	JsonElement jElement = j.get(Config.JSON_FIELD_LANGUAGE_SPECS);
    		if (jElement.isJsonArray()) {
	    		JsonArray jArray = jElement.getAsJsonArray();
	    		int size = jArray.size();
	    		List<LanguageSpec> languageSpecs = new ArrayList<>();
	    		boolean isValid = true;
	    		for (int i = 0; i < size; i++) {
	    			JsonElement arrayElement = jArray.get(i);
	    			if (!arrayElement.isJsonObject()) {
	    				isValid = false;
	    				// TODO: Throw error.
	    				break;
	    			}
	    			languageSpecs.add(LanguageSpec.fromJson(arrayElement.getAsJsonObject()));
	    		}
	    		if (isValid) {
		        	builder.setLanguageSpecs(languageSpecs);
	    		}
    		} else {
    			// TODO: Throw error.
    		}
        }
        
		return builder.build();
	}
	
	/**
	 * Merges the two Configs having newConfig override anything they both have set.
	 * @param defaultConfig The Config which has all the default info.
	 * @param newConfig The Config which should override defaults.
	 * @return A Config of the two merged together.
	 */
	public static Config merge(Config defaultConfig, Config newConfig) {
		Builder builder = new Builder();

		if (newConfig.hasInputFileTypes()) {
			builder.setInputFileTypes(newConfig.getInputFileTypes());
		} else {
			builder.setInputFileTypes(defaultConfig.getInputFileTypes());
		}
		
		if (newConfig.hasOutputFileTypes()) {
			builder.setOutputFileTypes(newConfig.getOutputFileTypes());
		} else {
			builder.setOutputFileTypes(defaultConfig.getOutputFileTypes());
		}
		
		if (newConfig.hasDefaultTimeLimit()) {
			builder.setDefaultTimeLimitString(newConfig.getDefaultTimeLimitString());
		} else {
			builder.setDefaultTimeLimitString(defaultConfig.getDefaultTimeLimitString());
		}
		
		if (newConfig.hasMinimumTimeLimit()) {
			builder.setMinimumTimeLimitString(newConfig.getMinimumTimeLimitString());
		} else {
			builder.setMinimumTimeLimitString(defaultConfig.getMinimumTimeLimitString());
		}
		
		if (newConfig.hasMaximumTimeLimit()) {
			builder.setMaximumTimeLimitString(newConfig.getMaximumTimeLimitString());
		} else {
			builder.setMaximumTimeLimitString(defaultConfig.getMaximumTimeLimitString());
		}
		
		if (newConfig.hasMaximumIdleTime()) {
			builder.setMaximumIdleTimeString(newConfig.getMaximumIdleTimeString());
		} else {
			builder.setMaximumIdleTimeString(defaultConfig.getMaximumIdleTimeString());
		}
		
		if (newConfig.hasCompileTimeLimit()) {
			builder.setCompileTimeLimitString(newConfig.getCompileTimeLimitString());
		} else {
			builder.setCompileTimeLimitString(defaultConfig.getCompileTimeLimitString());
		}
		
		if (newConfig.hasLanguageSpecs() && !newConfig.getLanguageSpecs().isEmpty()) {
			builder.setLanguageSpecs(newConfig.getLanguageSpecs());
		} else {
			builder.setLanguageSpecs(defaultConfig.getLanguageSpecs());
		}
		return builder.build();
	}

	public static Config getConfig() {
		if (CONFIG != null)
			return CONFIG;
		CONFIG = fromConfigInputStream(Config.class.getResourceAsStream(DEFAULT_CONFIG_RESOURCE_PATH));
		if (CONFIG == null) {
			System.err.println("Failed to load internal config!");
			System.exit(1);
			// TODO: Show pop-up error message.
		}
		createUserConfigFileIfNeeded();
		Config userConfig = fromConfigFile(FileUtils.getConfigFile(USER_CONFIG_FILENAME));
		if (userConfig != null) {
			CONFIG = merge(CONFIG, userConfig);
		}
		return CONFIG;
	}
	
	public static boolean createUserConfigFileIfNeeded() {
		File userConfigFile = FileUtils.getConfigFile(USER_CONFIG_FILENAME);
		// If the user config file already exists, we don't need to create it.
		if (userConfigFile.exists()) {
			return true;
		}
		
		// Copy the default config to the user config file.
		try {
			InputStream inputStream = Config.class.getResourceAsStream(DEFAULT_CONFIG_RESOURCE_PATH);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(userConfigFile));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				bufferedWriter.write(line);
				bufferedWriter.newLine();
			}
			bufferedWriter.close();
			bufferedReader.close();
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			
			return false;
		}
	}
	
	public static boolean saveUserConfig(Config config) {
		File userConfigFile = FileUtils.getConfigFile(USER_CONFIG_FILENAME);
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(userConfigFile));
			String jsonString = config.toJsonString();
			bufferedWriter.write(jsonString);
			bufferedWriter.newLine();
			bufferedWriter.close();
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			
			return false;
		}
	}
	
	public List<String> getInputFileTypes() {
		return inputFileTypes;
	}

	public boolean hasInputFileTypes() {
		return inputFileTypes != null;
	}
	
	public List<String> getOutputFileTypes() {
		return outputFileTypes;
	}
	
	public boolean hasOutputFileTypes() {
		return outputFileTypes != null;
	}
	
	public String getDefaultTimeLimitString() {
		return defaultTimeLimitString;
	}
	
	public boolean hasDefaultTimeLimitString() {
		return defaultTimeLimitString != null;
	}

	public String getMinimumTimeLimitString() {
		return minimumTimeLimitString;
	}
	
	public boolean hasMinimumTimeLimitString() {
		return minimumTimeLimitString != null;
	}

	public String getMaximumTimeLimitString() {
		return maximumTimeLimitString;
	}
	
	public boolean hasMaximumTimeLimitString() {
		return maximumTimeLimitString != null;
	}
	
	public String getMaximumIdleTimeString() {
		return maximumIdleTimeString;
	}
	
	public boolean hasMaximumIdleTimeString() {
		return maximumIdleTimeString != null;
	}
	
	public String getCompileTimeLimitString() {
		return compileTimeLimitString;
	}
	
	public boolean hasCompileTimeLimitString() {
		return compileTimeLimitString != null;
	}

	public Long getDefaultTimeLimit() {
		return defaultTimeLimit;
	}
	
	public boolean hasDefaultTimeLimit() {
		return defaultTimeLimit != null;
	}

	public Long getMinimumTimeLimit() {
		return minimumTimeLimit;
	}
	
	public boolean hasMinimumTimeLimit() {
		return minimumTimeLimit != null;
	}

	public Long getMaximumTimeLimit() {
		return maximumTimeLimit;
	}
	
	public boolean hasMaximumTimeLimit() {
		return maximumTimeLimit != null;
	}
	
	public Long getMaximumIdleTime() {
		return maximumIdleTime;
	}
	
	public boolean hasMaximumIdleTime() {
		return maximumIdleTime != null;
	}
	
	public Long getCompileTimeLimit() {
		return compileTimeLimit;
	}
	
	public boolean hasCompileTimeLimit() {
		return compileTimeLimit != null;
	}

	public List<LanguageSpec> getLanguageSpecs() {
		return languageSpecs;
	}
	
	public boolean hasLanguageSpecs() {
		return languageSpecs != null;
	}
	
	public List<LanguageSpec> getLanguageSpecsForDetectLanguage() {
		return languageSpecsForDetectLanguage;
	}
	
	public List<LanguageSpec> getLanguageSpecsForDisplay() {
		return languageSpecsForDisplay;
	}
	
	public LanguageSpec getLanguageSpecForExtension(String extension) {
		for (LanguageSpec languageSpec : getLanguageSpecsForDetectLanguage()) {
			if (languageSpec != null && languageSpec.matchesExtension(extension)) {
				return languageSpec;
			}
		}
		return null;
	}
	
	public ExtensionFilter getExtensionFilterFromLanguageSpecs() {
		Set<String> extensions = new HashSet<>();
		for (LanguageSpec languageSpec : getLanguageSpecs()) {
			for (String extension : languageSpec.getFileExtensions()) {
				extensions.add(extension);
			}
		}
		String[] extensionsArray = extensions.stream().map(x -> "*." + x).sorted().toArray(String[]::new);
		String filterDescription = "Solution files (";
		for (int i = 0; i < extensionsArray.length; i++) {
			if (i > 0) {
				filterDescription += ", ";
			}
			filterDescription += extensionsArray[i];
		}
		filterDescription += ")";
		return new ExtensionFilter(filterDescription, extensionsArray);
	}
	
	public static List<String> getStringList(JsonObject j, String fieldName) {
		if (!j.has(fieldName))
			return null; // Throw error (if appropriate).
		JsonElement jElement = j.get(fieldName);
		if (!jElement.isJsonArray())
			return null; // Throw error.
		JsonArray jArray = jElement.getAsJsonArray();
		int size = jArray.size();
		List<String> list = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			JsonElement arrayElement = jArray.get(i);
			if (!arrayElement.isJsonPrimitive())
				return null; // Throw error.
			arrayElement.getAsString();
			JsonPrimitive arrayPrimitive = arrayElement.getAsJsonPrimitive();
			list.add(arrayPrimitive.getAsString());
		}
		return list;
	}
	
	public String toJsonString() {
		JsonObject jsonObject = toJsonObject();
		return jsonObject.toString();
	}
	
	private JsonArray getInputFileExtensionsJsonArray() {
		JsonArray jsonArray = new JsonArray();
		for (String inputFileExtension : inputFileTypes) {
			jsonArray.add(inputFileExtension);
		}
		return jsonArray;
	}
	
	private JsonArray getOutputFileExtensionsJsonArray() {
		JsonArray jsonArray = new JsonArray();
		for (String outputFileExtension : outputFileTypes) {
			jsonArray.add(outputFileExtension);
		}
		return jsonArray;
	}
	
	private JsonArray getLanguageSpecsJsonArray() {
		JsonArray jsonArray = new JsonArray();
		for (LanguageSpec languageSpec : getLanguageSpecs()) {
			jsonArray.add(languageSpec.toJsonObject());
		}
		return jsonArray;
	}
	
	public JsonObject toJsonObject() {
		JsonObject jsonObject = new JsonObject();
		
		jsonObject.add(JSON_FIELD_INPUT_FILE_TYPES, getInputFileExtensionsJsonArray());
		
		jsonObject.add(JSON_FIELD_OUTPUT_FILE_TYPES, getOutputFileExtensionsJsonArray());
		
        if (hasDefaultTimeLimitString()) {
			jsonObject.addProperty(JSON_FIELD_DEFAULT_TIME_LIMIT, getDefaultTimeLimitString());
		}
        
        if (hasMinimumTimeLimitString()) {
			jsonObject.addProperty(JSON_FIELD_MINIMUM_TIME_LIMIT, getMinimumTimeLimitString());
		}
        
        if (hasMaximumTimeLimitString()) {
			jsonObject.addProperty(JSON_FIELD_MAXIMUM_TIME_LIMIT, getMaximumTimeLimitString());
		}
        
        if (hasMaximumIdleTimeString()) {
			jsonObject.addProperty(JSON_FIELD_MAXIMUM_IDLE_TIME, getMaximumIdleTimeString());
		}
        
        if (hasCompileTimeLimitString()) {
        	jsonObject.addProperty(JSON_FIELD_COMPILE_TIME_LIMIT, getCompileTimeLimitString());
        }

        jsonObject.add(JSON_FIELD_LANGUAGE_SPECS, getLanguageSpecsJsonArray());
        
		return jsonObject;
	}
	
	@Override
	public String toString() {
		return String.format(
				  "{ inputFileTypes: %s\n"
				+ "  outputFileTypes: %s\n"
				+ "  defaultTimeLimit: %s\n"
				+ "  minimumTimeLimit: %s\n"
				+ "  maximumTimeLimit: %s\n"
				+ "  languageSpecs: %s }\n",
				inputFileTypes,
				outputFileTypes,
				defaultTimeLimit,
				minimumTimeLimit,
				maximumTimeLimit,
				languageSpecs);
	}
	
	public static class Builder {
		private List<String> inputFileTypes = new ArrayList<>();
		private List<String> outputFileTypes = new ArrayList<>();
		
		private String defaultTimeLimitString;
		private String minimumTimeLimitString;
		private String maximumTimeLimitString;
		
		private String maximumIdleTimeString;
		private String compileTimeLimitString;
		
		private List<LanguageSpec> languageSpecs = new ArrayList<>();
		
		public Config build() {
			return new Config(
					inputFileTypes,
					outputFileTypes,
					defaultTimeLimitString,
					minimumTimeLimitString,
					maximumTimeLimitString,
					maximumIdleTimeString,
					compileTimeLimitString,
					languageSpecs
			);
		}

		public List<String> getInputFileTypes() {
			return inputFileTypes;
		}

		public Builder setInputFileTypes(List<String> inputFileTypes) {
			this.inputFileTypes = inputFileTypes;
			return this;
		}

		public List<String> getOutputFileTypes() {
			return outputFileTypes;
		}

		public Builder setOutputFileTypes(List<String> outputFileTypes) {
			this.outputFileTypes = outputFileTypes;
			return this;
		}

		public String getDefaultTimeLimitString() {
			return defaultTimeLimitString;
		}

		public Builder setDefaultTimeLimitString(String defaultTimeLimitString) {
			this.defaultTimeLimitString = defaultTimeLimitString;
			return this;
		}

		public String getMinimumTimeLimitString() {
			return minimumTimeLimitString;
		}

		public Builder setMinimumTimeLimitString(String minimumTimeLimitString) {
			this.minimumTimeLimitString = minimumTimeLimitString;
			return this;
		}

		public String getMaximumTimeLimitString() {
			return maximumTimeLimitString;
		}

		public Builder setMaximumTimeLimitString(String maximumTimeLimitString) {
			this.maximumTimeLimitString = maximumTimeLimitString;
			return this;
		}
		
		public String getMaximumIdleTimeString() {
			return maximumIdleTimeString;
		}

		public Builder setMaximumIdleTimeString(String maximumIdleTimeString) {
			this.maximumIdleTimeString = maximumIdleTimeString;
			return this;
		}
		
		public String getCompileTimeLimitString() {
			return compileTimeLimitString;
		}

		public Builder setCompileTimeLimitString(String compileTimeLimitString) {
			this.compileTimeLimitString = compileTimeLimitString;
			return this;
		}

		public List<LanguageSpec> getLanguageSpecs() {
			return languageSpecs;
		}

		public Builder setLanguageSpecs(List<LanguageSpec> languageSpecs) {
			this.languageSpecs = languageSpecs;
			return this;
		}
		
		
	}
}

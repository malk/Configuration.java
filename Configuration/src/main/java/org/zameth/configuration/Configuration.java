package org.zameth.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public final class Configuration {

	private static final String EMPTY = "";
	private static final String CONFIG_PATH_PROPERTY = "config.properties";
	private static final String DEFAULT_PROPERTIES_RESOURCE = "/" + CONFIG_PATH_PROPERTY;

	private static final ConcurrentHashMap<String, String> CACHE = new ConcurrentHashMap<String, String>();
	private static final Iterable<Properties> PROPERTIES = loadProperties();

	private Configuration() { }

	
	public static String get(final String property) {
		if (!CACHE.containsKey(property)) {
			CACHE.putIfAbsent(property, seekValueFor(property));			
		}
		return CACHE.get(property);
	}
	
	private static String seekValueFor(final String property) {
		for (final Properties properties : PROPERTIES) {
			final String result = properties.getProperty(property);
			if (result != null) {
				return result;
			}
		}
		return EMPTY;
	}	

	private static Iterable<Properties> loadProperties() {
		final List<Properties> properties = new ArrayList<Properties>();
		final Properties system = System.getProperties();
		String configFilePath;

		try {
			configFilePath = Configuration
				.class
				.getResource(DEFAULT_PROPERTIES_RESOURCE)
				.getFile();
		} catch (final Exception e) {
			configFilePath = null;
		}

		final Properties defaultProperties = slurpPropertiesFile(configFilePath);
		
		configFilePath = system.getProperty(CONFIG_PATH_PROPERTY);
		if (configFilePath == null) {
			configFilePath = defaultProperties.getProperty(CONFIG_PATH_PROPERTY);
		}
		
		properties.add(system);
		properties.add(slurpPropertiesFile(configFilePath));
		properties.add(defaultProperties);
		return properties;
	}

	private static Properties slurpPropertiesFile(final String path) {
		final Properties properties = new Properties();
		try {
			final File configFile = new File(path);
			if (configFile.isFile()) {
				properties.load(new FileInputStream(configFile));
			}
		} catch (final Exception e) {
			// the configuration file existing at all is purely optional, no errors on fail
		}
		return properties;
	}
}

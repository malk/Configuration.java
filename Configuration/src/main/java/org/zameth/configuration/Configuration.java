package org.zameth.configuration;

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
		final Properties defaultProperties = slurpPropertiesFile(defaultPropertiesResourcePath());
		String configFilePath;

		
		configFilePath = system.getProperty(CONFIG_PATH_PROPERTY);
		if (configFilePath == null) {
			configFilePath = defaultProperties.getProperty(CONFIG_PATH_PROPERTY);
		}
		
		properties.add(system);
		properties.add(slurpPropertiesFile(configFilePath));
		properties.add(defaultProperties);
		return properties;
	}


	private static String defaultPropertiesResourcePath() {
		try {
			return Configuration
					.class
					.getResource(DEFAULT_PROPERTIES_RESOURCE)
					.getFile();
		} catch (final Exception e) {
			return null;
		}
	}

	private static Properties slurpPropertiesFile(final String path) {
		final Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(path));
		} catch (final Exception e) {
			// the configuration file existing at all is purely optional, no errors on fail
			return new Properties();
		}
		return properties;
	}
}

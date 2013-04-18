package com.internetitem.simpleweb.config;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import com.internetitem.simpleweb.config.dataModel.SimpleWebConfig;
import com.internetitem.simpleweb.utility.BeanUtility;
import com.internetitem.simpleweb.utility.JsonUtility;

public class ConfigurationFactory {
	public static Configuration getConfiguration(ConfigurationParameters params) throws ConfigurationException {
		String configFile = params.getParameter("config.file");
		if (configFile == null) {
			configFile = "/config.json";
		}

		InputStream istream = ConfigurationFactory.class.getResourceAsStream(configFile);
		if (istream == null) {
			configFile = "/basic-config.json";
			istream = ConfigurationFactory.class.getResourceAsStream(configFile);

			if (istream == null) {
				throw new ConfigurationException("Unable to find configuration file " + configFile);
			}
		}

		return loadConfig(configFile, istream);
	}

	private static Configuration loadConfig(String configFile, InputStream istream) throws ConfigurationException {
		try {
			Reader reader = new InputStreamReader(istream, "UTF-8");
			SimpleWebConfig simpleWebConfig = SimpleWebConfig.parseFromStream(reader);
			String configClassName = simpleWebConfig.getConfigClass();
			Map<String, String> parameters = JsonUtility.toMap(simpleWebConfig.getConfigParameters());
			Configuration configuration = BeanUtility.createObject(configClassName, Configuration.class, parameters);
			configuration.init();
			return configuration;
		} catch (Exception e) {
			throw new ConfigurationException("Error loading configuration from " + configFile + ": " + e.getMessage(), e);
		}

	}
}
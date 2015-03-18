package org.vanilladb.core.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Anyone who wants to fetch system properties must use this class as interface.
 * This class will read properties files before fetching any property and log
 * warning messages for properties that do not exist or can not be parsed.
 * 
 * @author yslin
 */
public class PropertiesFetcher {
	private static Logger logger = Logger.getLogger(PropertiesFetcher.class
			.getName());

	static {
		// read properties file
		boolean config = false;
		String path = System.getProperty("org.vanilladb.core.config.file");
		if (path != null) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(path);
				System.getProperties().load(fis);
				config = true;
			} catch (IOException e) {
				// do nothing
			} finally {
				try {
					if (fis != null)
						fis.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
		if (!config && logger.isLoggable(Level.WARNING))
			logger.warning("error reading the config file, using defaults");
	}

	public static String getPropertyAsString(String propertyName,
			String defaultValue) {
		String value = getPropertyValue(propertyName);

		// can't find property
		if (value == null) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("can't find property: " + propertyName
						+ ", using default value: " + defaultValue);
			return defaultValue;
		}

		return value;
	}

	public static boolean getPropertyAsBoolean(String propertyName,
			boolean defaultValue) {
		String value = getPropertyValue(propertyName);

		// can't find property
		if (value == null) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("can't find property: " + propertyName
						+ ", using default value: " + defaultValue);
			return defaultValue;
		}

		// parse to boolean
		boolean boolValue;
		try {
			boolValue = Boolean.parseBoolean(value);
		} catch (NumberFormatException e) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("parsing property: " + propertyName
						+ " into boolean fails, using default value: "
						+ defaultValue);
			boolValue = defaultValue;
		}

		return boolValue;
	}

	public static int getPropertyAsInteger(String propertyName, int defaultValue) {
		String value = getPropertyValue(propertyName);

		// can't find property
		if (value == null) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("can't find property: " + propertyName
						+ ", using default value: " + defaultValue);
			return defaultValue;
		}

		// parse to int
		int intValue;
		try {
			intValue = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("parsing property: " + propertyName
						+ " into integer fails, using default value: "
						+ defaultValue);
			intValue = defaultValue;
		}

		return intValue;
	}
	
	public static long getPropertyAsLong(String propertyName, long defaultValue) {
		String value = getPropertyValue(propertyName);

		// can't find property
		if (value == null) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("can't find property: " + propertyName
						+ ", using default value: " + defaultValue);
			return defaultValue;
		}

		// parse to long
		long longValue;
		try {
			longValue = Long.parseLong(value);
		} catch (NumberFormatException e) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("parsing property: " + propertyName
						+ " into long fails, using default value: "
						+ defaultValue);
			longValue = defaultValue;
		}

		return longValue;
	}
	
	public static String[] getPropertyAsStringArray(String propertyName, String[] defaultArray) {
		String value = getPropertyValue(propertyName);

		// can't find property
		if (value == null) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("can't find property: " + propertyName
						+ ", using default array: " + Arrays.toString(defaultArray));
			return defaultArray;
		}

		// split string by ','
		return value.split(",");
	}

	public static Class<?> getPropertyAsClass(String propertyName,
			Class<?> defaultClass, Class<?> superClassConstraint) {
		String value = getPropertyValue(propertyName);

		// can't find property
		if (value == null) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("can't find property: " + propertyName
						+ ", using default value: " + defaultClass);
			return defaultClass;
		}

		// parse to class
		Class<?> parsedClass;
		try {
			parsedClass = Class.forName(value);

			// check super class constraint
			if (superClassConstraint != null
					&& !superClassConstraint.isAssignableFrom(parsedClass)) {
				if (logger.isLoggable(Level.WARNING))
					logger.warning("'" + parsedClass
							+ "' class is not the subclass of '"
							+ superClassConstraint + "' for property: "
							+ propertyName + ", using default class: "
							+ defaultClass);
				parsedClass = defaultClass;
			}

		} catch (ClassNotFoundException e) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("can't find " + value + " for property: "
						+ propertyName + ", using default class: "
						+ defaultClass);
			parsedClass = defaultClass;
		}

		return parsedClass;
	}

	private static String getPropertyValue(String propertyName) {
		// get a property value as string
		String value = System.getProperty(propertyName);

		if (value == null || value.isEmpty())
			return null;

		return value.trim();
	}
}

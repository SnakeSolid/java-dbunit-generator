package ru.snake.dbunit.generator.worker;

import java.lang.reflect.Field;
import java.sql.Driver;
import java.sql.DriverAction;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to deregister all drivers registered in {@link DriverManager}.
 * This class used to prevent memory leak. Memory leak caused by different class
 * loaders of created JDBC driver and {@link DriverWrapper}.
 *
 * @author snake
 *
 */
public final class DriverDeregistrator {
	private static final Logger LOG = LoggerFactory.getLogger(DriverDeregistrator.class);

	/**
	 * Deregister all drivers from {@link DriverManager}. This method ignores
	 * {@link DriverAction} and just clear registered driver list if it's
	 * possible.
	 */
	public static void deregisterAll() {
		boolean success = false;

		try {
			Field registeredDriversField = DriverManager.class.getDeclaredField("registeredDrivers");
			// This field is private, set it accessible.
			registeredDriversField.setAccessible(true);
			Object registeredDriversValue = registeredDriversField.get(null);

			if (registeredDriversValue instanceof List) {
				List<?> registeredDrivers = (List<?>) registeredDriversValue;
				registeredDrivers.clear();

				success = true;
			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			LOG.warn("Failed to get driver list", e);
		}

		if (!success) {
			// If reflection was not success deregister drivers using
			// enumeration.
			Enumeration<Driver> drivers = DriverManager.getDrivers();

			while (drivers.hasMoreElements()) {
				Driver driver = drivers.nextElement();

				try {
					DriverManager.deregisterDriver(driver);
				} catch (SQLException e1) {
					LOG.warn("Failed to deregixter driver", e1);
				}
			}
		}
	}

	/**
	 * Hides public constructor.
	 */
	private DriverDeregistrator() {
		// Hide public constructor for utility class.
	}
}

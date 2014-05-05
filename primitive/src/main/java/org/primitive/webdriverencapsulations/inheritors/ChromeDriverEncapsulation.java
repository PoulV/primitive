package org.primitive.webdriverencapsulations.inheritors;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.primitive.configuration.Configuration;
import org.primitive.configuration.webdriver.ESupportedDrivers;
import org.primitive.webdriverencapsulations.WebDriverEncapsulation;

public final class ChromeDriverEncapsulation extends WebDriverEncapsulation {

	public ChromeDriverEncapsulation(ChromeDriverService service) {
		super();
		createWebDriver(ESupportedDrivers.CHROME.getUsingWebDriverClass(),
				new Class<?>[] { ChromeDriverService.class },
				new Object[] { service });
	}

	public ChromeDriverEncapsulation(ChromeDriverService service,
			Configuration config) {
		super();
		this.configuration = config;
		createWebDriver(ESupportedDrivers.CHROME.getUsingWebDriverClass(),
				new Class<?>[] { ChromeDriverService.class },
				new Object[] { service });
	}

	public ChromeDriverEncapsulation(ChromeDriverService service,
			Capabilities capabilities) {
		super();
		createWebDriver(
				ESupportedDrivers.CHROME.getUsingWebDriverClass(),
				new Class<?>[] { ChromeDriverService.class, Capabilities.class },
				new Object[] { service, capabilities });
	}

	public ChromeDriverEncapsulation(ChromeDriverService service,
			Capabilities capabilities, Configuration config) {
		super();
		this.configuration = config;
		createWebDriver(
				ESupportedDrivers.CHROME.getUsingWebDriverClass(),
				new Class<?>[] { ChromeDriverService.class, Capabilities.class },
				new Object[] { service, capabilities });
	}

	public ChromeDriverEncapsulation(ChromeOptions options) {
		super();
		ESupportedDrivers.CHROME.setSystemProperty(Configuration.byDefault,
				DesiredCapabilities.chrome());
		createWebDriver(ESupportedDrivers.CHROME.getUsingWebDriverClass(),
				new Class<?>[] { ChromeOptions.class },
				new Object[] { options });
	}

	public ChromeDriverEncapsulation(ChromeOptions options, Configuration config) {
		super();
		ESupportedDrivers.CHROME.setSystemProperty(config,
				DesiredCapabilities.chrome());
		this.configuration = config;
		createWebDriver(ESupportedDrivers.CHROME.getUsingWebDriverClass(),
				new Class<?>[] { ChromeOptions.class },
				new Object[] { options });
	}

	public ChromeDriverEncapsulation(ChromeDriverService service,
			ChromeOptions options) {
		super();
		createWebDriver(
				ESupportedDrivers.CHROME.getUsingWebDriverClass(),
				new Class<?>[] { ChromeDriverService.class, ChromeOptions.class },
				new Object[] { service, options });
	}

	public ChromeDriverEncapsulation(ChromeDriverService service,
			ChromeOptions options, Configuration config) {
		super();
		this.configuration = config;
		createWebDriver(
				ESupportedDrivers.CHROME.getUsingWebDriverClass(),
				new Class<?>[] { ChromeDriverService.class, ChromeOptions.class },
				new Object[] { service, options });
	}
}
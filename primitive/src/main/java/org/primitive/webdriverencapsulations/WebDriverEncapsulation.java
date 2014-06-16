package org.primitive.webdriverencapsulations;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.internal.WrapsDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.primitive.configuration.Configuration;
import org.primitive.configuration.interfaces.IConfigurable;
import org.primitive.configuration.webdriver.ESupportedDrivers;
import org.primitive.interfaces.IDestroyable;
import org.primitive.logging.Log;
import org.primitive.webdriverencapsulations.components.bydefault.ByAccessibilityId;
import org.primitive.webdriverencapsulations.components.bydefault.ByAndroidUIAutomator;
import org.primitive.webdriverencapsulations.components.bydefault.ByIosUIAutomation;
import org.primitive.webdriverencapsulations.components.bydefault.ComponentFactory;
import org.primitive.webdriverencapsulations.components.bydefault.DriverLogs;
import org.primitive.webdriverencapsulations.components.bydefault.Interaction;
import org.primitive.webdriverencapsulations.components.bydefault.Rotator;
import org.primitive.webdriverencapsulations.components.bydefault.ScriptExecutor;
import org.primitive.webdriverencapsulations.components.overriden.Awaiting;
import org.primitive.webdriverencapsulations.components.overriden.Cookies;
import org.primitive.webdriverencapsulations.components.overriden.FrameSupport;
import org.primitive.webdriverencapsulations.components.overriden.Ime;
import org.primitive.webdriverencapsulations.components.overriden.PageFactoryWorker;
import org.primitive.webdriverencapsulations.components.overriden.TimeOut;
import org.primitive.webdriverencapsulations.eventlisteners.DefaultWebdriverListener;
import org.primitive.webdriverencapsulations.eventlisteners.IExtendedWebDriverEventListener;
import org.primitive.webdriverencapsulations.eventlisteners.IWindowListener;
import org.primitive.webdriverencapsulations.interfaces.IWebElementHighlighter;

public class WebDriverEncapsulation implements IDestroyable, IConfigurable,
		WrapsDriver, HasCapabilities {
	// get tests started with FireFoxDriver by default.
	private static ESupportedDrivers defaultSupportedDriver = ESupportedDrivers.FIREFOX;
	private ClosedFiringWebDriver closedDriver;
	protected Configuration configuration = Configuration.byDefault;

	private Awaiting awaiting;
	private PageFactoryWorker pageFactoryWorker;
	private ScriptExecutor scriptExecutor;
	private FrameSupport frameSupport;
	private Cookies cookies;
	private TimeOut timeout;
	private DriverLogs logs;
	private Ime ime;
	private Interaction interaction;
	private WebElementHighLighter elementHighLighter;
	private ByAccessibilityId byAccessibilityId;
	private ByAndroidUIAutomator byAndroidUIAutomator;
	private ByIosUIAutomation byIosUIAutomation;
	private Rotator rotator;
	
	private final ConfigurableElements configurableElements = new ConfigurableElements();

	protected static void prelaunch(ESupportedDrivers supporteddriver,
			Configuration config, Capabilities capabilities) {
		supporteddriver.launchRemoteServerLocallyIfWasDefined(config);
		supporteddriver.setSystemProperty(config, capabilities);
	}

	private void constructorBody(ESupportedDrivers supporteddriver,
			Capabilities capabilities, URL remoteAddress) {
		if ((supporteddriver.startsRemotely())&(remoteAddress!=null)) {
			createWebDriver(supporteddriver.getUsingWebDriverClass(),
					new Class[] { URL.class, Capabilities.class },
					new Object[] { remoteAddress, capabilities });
		} else {
			if ((remoteAddress==null)&(supporteddriver.requiresRemoteURL())){
				throw new RuntimeException(
						"Defined driver '"
								+ supporteddriver.toString()
								+ "' requires remote address (URL)! Please, define it in settings.json "
								+ "or use suitable constructor");
			}
			if (remoteAddress!=null){
				Log.message("Remote address " + String.valueOf(remoteAddress)
						+ " has been ignored");
			}
			createWebDriver(supporteddriver.getUsingWebDriverClass(),
					new Class[] { Capabilities.class },
					new Object[] { capabilities });
		}
	}

	// it makes objects of any WebDriver and navigates to specified URL
	protected void createWebDriver(Class<? extends WebDriver> driverClass,
			Class<?>[] paramClasses, Object[] values) {
		WebDriver driver = null;
		Constructor<?> suitableConstructor = null;
		try {
			suitableConstructor = driverClass.getConstructor(paramClasses);
		} catch (NoSuchMethodException | SecurityException e1) {
			throw new RuntimeException(
					"Wrong specified or constructor of WebDriver! "
							+ driverClass.getSimpleName(), e1);
		}

		try {
			driver = (WebDriver) suitableConstructor.newInstance(values);
			actoinsAfterWebDriverCreation(driver);
		} catch (RuntimeException | InstantiationException
				| IllegalAccessException | InvocationTargetException e) {
			if (driver != null) {
				driver.quit();
			}
			actoinsOnConstructFailure(new RuntimeException(e));
		}
	}

	// constructors are below:
	/** creates instance by specified driver and capabilities */
	public WebDriverEncapsulation(ESupportedDrivers supporteddriver,
			Capabilities capabilities) {
		prelaunch(supporteddriver, this.configuration, capabilities);
		constructorBody(supporteddriver, capabilities,
				(URL) null);
	}

	/** creates instance by specified driver */
	public WebDriverEncapsulation(ESupportedDrivers supporteddriver) {
		this(supporteddriver, supporteddriver.getDefaultCapabilities());
	}

	/** creates instance by specified driver, capabilities and remote address */
	public WebDriverEncapsulation(ESupportedDrivers supporteddriver,
			Capabilities capabilities, URL remoteAddress) {
		constructorBody(supporteddriver, capabilities, remoteAddress);
	}

	/**
	 * creates instance by specified driver and remote address using default
	 * capabilities
	 */
	public WebDriverEncapsulation(ESupportedDrivers supporteddriver,
			URL remoteAddress) {
		this(supporteddriver, supporteddriver.getDefaultCapabilities(),
				remoteAddress);
	}

	/**
	 * creates instance by specified driver and remote address using specified
	 * configuration
	 */
	public WebDriverEncapsulation(Configuration configuration) {
		this.configuration = configuration;
		ESupportedDrivers supportedDriver = this.configuration
				.getWebDriverSettings().getSupoortedWebDriver();
		if (supportedDriver == null) {
			supportedDriver = defaultSupportedDriver;
		}

		Capabilities capabilities = this.configuration.getCapabilities();
		if (capabilities == null) {
			capabilities = supportedDriver.getDefaultCapabilities();
		}

		if (capabilities.asMap().size() == 0) {
			capabilities = supportedDriver.getDefaultCapabilities();
		}

		String remoteAdress = this.configuration.getWebDriverSettings()
				.getRemoteAddress();
		if (remoteAdress == null) {//local starting
			prelaunch(supportedDriver, this.configuration, capabilities);
			constructorBody(supportedDriver, capabilities,
					(URL) null);
			return;
		}

		try {
			URL remoteUrl = new URL(remoteAdress);
			constructorBody(supportedDriver, capabilities, remoteUrl);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/** creates instance using externally initiated webdriver **/
	public WebDriverEncapsulation(WebDriver externallyInitiatedWebDriver) {
		actoinsAfterWebDriverCreation(externallyInitiatedWebDriver);
	}

	/** creates instance using externally initiated webdriver **/
	public WebDriverEncapsulation(WebDriver externallyInitiatedWebDriver,
			Configuration configuration) {
		this.configuration = configuration;
		actoinsAfterWebDriverCreation(externallyInitiatedWebDriver);
	}

	protected WebDriverEncapsulation() {
		super();
	}

	// other methods:

	public void destroy() {
		InnerSPIServises.removeBy(this);
		if (closedDriver == null) {
			return;
		}
		try {
			unregisterAll();
			closedDriver.quit();
		} catch (WebDriverException e) // it may be already dead
		{
			return;
		}
	}

	public WebDriver getWrappedDriver() {
		return (closedDriver);
	}

	public Awaiting getAwaiting() {
		return (awaiting);
	}

	public IWebElementHighlighter getHighlighter() {
		return (elementHighLighter);
	}

	public PageFactoryWorker getPageFactoryWorker() {
		return (pageFactoryWorker);
	}

	public ScriptExecutor getScriptExecutor() {
		return (scriptExecutor);
	}

	public FrameSupport getFrameSupport() {
		return (frameSupport);
	}

	public Cookies getCookies() {
		return cookies;
	}

	public Ime getIme() {
		return ime;
	}

	public DriverLogs getLogs() {
		return logs;
	}

	public TimeOut getTimeOut() {
		return timeout;
	}

	public Interaction getInteraction() {
		return interaction;
	}

	public Capabilities getCapabilities() {
		return closedDriver.getCapabilities();
	}
	
	public ByAccessibilityId getByAccessibilityId(){
		return byAccessibilityId;
	}
	
	public ByAndroidUIAutomator getByAndroidUIAutomator(){
		return byAndroidUIAutomator;
	}
	
	public ByIosUIAutomation getByIosUIAutomation(){
		return byIosUIAutomation;
	}
	
	public Rotator getRotator(){
		return rotator;
	}

	private void registerAll() {
		InnerSPIServises servises = InnerSPIServises.getBy(this);
		List<IExtendedWebDriverEventListener> listeners = servises
				.getServices(IExtendedWebDriverEventListener.class);
		for (IExtendedWebDriverEventListener listener : listeners) {
			closedDriver.register(listener);
		}

		List<WebDriverEventListener> listeners2 = servises
				.getServices(WebDriverEventListener.class);
		for (WebDriverEventListener listener : listeners2) {
			closedDriver.register(listener);
		}
	}

	private void actoinsAfterWebDriverCreation(WebDriver createdDriver) {
		Log.message("Getting started with "
				+ createdDriver.getClass().getSimpleName());
		Log.message("Capabilities are: "
				+ ((HasCapabilities) createdDriver).getCapabilities().asMap()
						.toString());

		closedDriver = new ClosedFiringWebDriver(createdDriver);

		elementHighLighter = new WebElementHighLighter();

		awaiting = new Awaiting(closedDriver);
		pageFactoryWorker = new PageFactoryWorker(closedDriver);
		scriptExecutor = ComponentFactory.getComponent(ScriptExecutor.class, closedDriver);
		frameSupport = new FrameSupport(closedDriver);
		cookies = new Cookies(closedDriver);
		timeout = new TimeOut(closedDriver, configuration);
		logs = ComponentFactory.getComponent(DriverLogs.class, closedDriver);
		ime = new Ime(closedDriver);
		interaction = ComponentFactory.getComponent(Interaction.class, closedDriver);
		byAccessibilityId = ComponentFactory.getComponent(ByAccessibilityId.class, closedDriver);
		byAndroidUIAutomator = ComponentFactory.getComponent(ByAndroidUIAutomator.class, closedDriver);
		byIosUIAutomation    = ComponentFactory.getComponent(ByIosUIAutomation.class, closedDriver);
		rotator              = ComponentFactory.getComponent(Rotator.class, closedDriver);

		configurableElements.addConfigurable(timeout);
		configurableElements.addConfigurable(elementHighLighter);

		// some services are implemented. They have their special logic
		InnerSPIServises servises = InnerSPIServises.getBy(this);
		configurableElements.addConfigurable((IConfigurable) servises
				.getDafaultService(IWindowListener.class));
		DefaultWebdriverListener webdriverListener = (DefaultWebdriverListener) servises
				.getDafaultService(IExtendedWebDriverEventListener.class);
		webdriverListener.setHighLighter(elementHighLighter);

		registerAll();
		resetAccordingTo(configuration);
	}

	// if attempt to create a new web driver instance has been failed
	protected void actoinsOnConstructFailure(RuntimeException e) {
		Log.error(
				"Attempt to create a new web driver instance has been failed! "
						+ e.getMessage(), e);
		destroy();
		throw e;

	}

	public void registerListener(WebDriverEventListener listener) {
		closedDriver.register(listener);
	}

	public void registerListener(IExtendedWebDriverEventListener listener) {
		closedDriver.register(listener);
	}

	public void unregisterListener(WebDriverEventListener listener) {
		closedDriver.unregister(listener);
	}

	public void unregisterListener(IExtendedWebDriverEventListener listener) {
		closedDriver.unregister(listener);
	}

	private void unregisterAll() {
		InnerSPIServises servises = InnerSPIServises.getBy(this);
		List<IExtendedWebDriverEventListener> listeners = servises
				.getServices(IExtendedWebDriverEventListener.class);
		for (IExtendedWebDriverEventListener listener : listeners) {
			unregisterListener(listener);
		}

		List<WebDriverEventListener> listeners2 = servises
				.getServices(WebDriverEventListener.class);
		for (WebDriverEventListener listener : listeners2) {
			unregisterListener(listener);
		}
	}

	public synchronized void resetAccordingTo(Configuration config) {
		configuration = config;
		configurableElements.resetAccordingTo(configuration);
	}

	// it goes to another URL
	public void getTo(String url) {
		closedDriver.get(url);
	}
}
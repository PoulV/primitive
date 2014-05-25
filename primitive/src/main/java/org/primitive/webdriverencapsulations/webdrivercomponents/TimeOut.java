package org.primitive.webdriverencapsulations.webdrivercomponents;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Timeouts;
import org.openqa.selenium.WebDriverException;
import org.primitive.configuration.Configuration;
import org.primitive.configuration.interfaces.IConfigurable;
import org.primitive.logging.Log;
import org.primitive.webdriverencapsulations.interfaces.ITimeOutsGetter;

public final class TimeOut extends WebdriverComponent implements Timeouts, ITimeOutsGetter,
		IConfigurable {

	public TimeOut(WebDriver driver, Configuration configuration) {
		super(driver);
		resetAccordingTo(configuration);
	}

	private final long defaultTimeOut = 20000; // 20 seconds
	private final TimeUnit defaultTimeUnit = TimeUnit.MILLISECONDS;
	
	private long implicitlyWaitTimeOut = defaultTimeOut;
	private TimeUnit implicitlyWaitTimeUnit = defaultTimeUnit;
	
	private long pageLoadTimeOut = defaultTimeOut;
	private TimeUnit pageLoadTimeUnit = defaultTimeUnit;
	
	private long scriptTimeOut = defaultTimeOut;
	private TimeUnit scriptTimeUnit = defaultTimeUnit;

	private Long getTimeOutValue(Long longObjParam) {
		if (longObjParam == null) {
			longObjParam = defaultTimeOut;
		}
		return longObjParam;
	}

	public Timeouts implicitlyWait(long timeOut, TimeUnit timeUnit) {
		boolean timeOutsAreSetWell = true;
		try {
			return driver.manage().timeouts().implicitlyWait(timeOut, timeUnit);
			
		} catch (WebDriverException e) {
			Log.debug(
					"Setting of an implicitly wait timeout is not supported.",
					e);
			timeOutsAreSetWell = false;
			return null;
		}
		finally{
			if (timeOutsAreSetWell){
				implicitlyWaitTimeOut = timeOut;
				implicitlyWaitTimeUnit = timeUnit;
			}
		}
	}

	public Timeouts pageLoadTimeout(long timeOut, TimeUnit timeUnit) {
		boolean timeOutsAreSetWell = true;
		try {
			return driver.manage().timeouts()
					.pageLoadTimeout(timeOut, timeUnit);
		} catch (WebDriverException e) {
			Log.debug("Setting of a page load timeout is not supported.", e);
			timeOutsAreSetWell = false;
			return null;
		}
		finally{
			if (timeOutsAreSetWell){
				pageLoadTimeOut = timeOut;
				pageLoadTimeUnit = timeUnit;
			}
		}
	}

	// set values of time outs according to configuration
	public synchronized void resetAccordingTo(Configuration config) {
		TimeUnit settingTimeUnit = config.getWebDriverTimeOuts().getTimeUnit();
		if (settingTimeUnit == null) {
			settingTimeUnit = defaultTimeUnit;
		}

		Long timeOut = getTimeOutValue(config.getWebDriverTimeOuts()
				.getImplicitlyWaitTimeOut());
		implicitlyWait(timeOut, settingTimeUnit);

		timeOut = getTimeOutValue(config.getWebDriverTimeOuts()
				.getScriptTimeOut());
		setScriptTimeout(timeOut, settingTimeUnit);

		timeOut = getTimeOutValue(config.getWebDriverTimeOuts()
				.getLoadTimeout());
		pageLoadTimeout(timeOut, settingTimeUnit);
	}

	public Timeouts setScriptTimeout(long timeOut, TimeUnit timeUnit) {
		boolean timeOutsAreSetWell = true;
		try {
			return driver.manage().timeouts()
					.setScriptTimeout(timeOut, timeUnit);
		} catch (WebDriverException e) {
			Log.debug(
					"Setting of a script execution timeout is not supported.",
					e);
			timeOutsAreSetWell = false;
			return null;
		}
		finally{
			if (timeOutsAreSetWell){
				scriptTimeOut = timeOut;
				scriptTimeUnit = timeUnit;
			}
		}		
	}

	@Override
	public long getImplicitlyWaitTimeOut() {
		return implicitlyWaitTimeOut;
	}

	@Override
	public TimeUnit getImplicitlyWaitTimeUnit() {
		return implicitlyWaitTimeUnit;
	}

	@Override
	public long getPageLoadTimeOut() {
		return pageLoadTimeOut;
	}

	@Override
	public TimeUnit getPageLoadTimeUnit() {
		return pageLoadTimeUnit;
	}

	@Override
	public long getScriptTimeOut() {
		return scriptTimeOut;
	}

	@Override
	public TimeUnit getScriptTimeUnit() {
		return scriptTimeUnit;
	}

}

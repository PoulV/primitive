package org.primitive.configuration.commonhelpers;

import java.util.concurrent.TimeUnit;

import org.primitive.configuration.AbstractConfigurationAccessHelper;
import org.primitive.configuration.Configuration;
import org.primitive.configuration.interfaces.ITimeUnitSetting;

/**
 * @author s.tihomirov
 * Specified webdriver time out parameters
 */
public class WebDriverTimeOuts extends AbstractConfigurationAccessHelper
		implements ITimeUnitSetting {

	private final String implicitlyWaitTimeOutSetting = "implicitlyWait";
	private final String pageLoadTimeoutSetting = "pageLoadTimeout";
	private final String scriptTimeOutSetting = "setScriptTimeout";
	private final String webDriverTimeOutsGroup 	    = "webDriverTimeOuts";
	public  final String timeUnitSetting = "timeUnit";

	public WebDriverTimeOuts(Configuration configuration) {
		super(configuration);
	}

	public Long getImplicitlyWaitTimeOut()
	{
		return (Long) getSetting(implicitlyWaitTimeOutSetting);
	}

	public Long getLoadTimeout()
	{
		return (Long) getSetting(pageLoadTimeoutSetting);
	}

	public Long getScriptTimeOut()
	{
		return (Long) getSetting(scriptTimeOutSetting);
	}

	public Object getSetting(String name)
	{
		return  getSettingValue(webDriverTimeOutsGroup, name);
	}

	public TimeUnit getTimeUnit()
	{
		String timeUnitStr = (String) getSetting(timeUnitSetting);
		if (timeUnitStr != null)
		{
			return TimeUnit.valueOf(timeUnitStr.toUpperCase());
		}
		else
		{
			return null;
		}
	}

}

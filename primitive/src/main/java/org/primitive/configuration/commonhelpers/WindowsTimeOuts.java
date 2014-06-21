package org.primitive.configuration.commonhelpers;

import org.primitive.configuration.AbstractConfigurationAccessHelper;
import org.primitive.configuration.Configuration;
import org.primitive.configuration.interfaces.IHasAlertTimeOut;

public class WindowsTimeOuts extends AbstractConfigurationAccessHelper implements IHasAlertTimeOut {
	private final String newWindowTimeOutSetting = "newWindowTimeOutSec";
	private final String windowCountTimeOutSetting = "windowCountTimeOutSec";
	private final String windowClosingTimeOutSetting = "windowClosingTimeOutSec";
	private final String windowsTimeOutsGroup = "windowsTimeOuts";

	public WindowsTimeOuts(Configuration configuration) {
		super(configuration);
	}

	@Override
	public Object getSetting(String name) {
		return getSettingValue(windowsTimeOutsGroup, name);
	}

	public Long getNewWindowTimeOutSec() {
		return (Long) getSetting(newWindowTimeOutSetting);
	}

	public Long getWindowCountTimeOutSec() {
		return (Long) getSetting(windowCountTimeOutSetting);
	}

	public Long getWindowClosingTimeOutSec() {
		return (Long) getSetting(windowClosingTimeOutSetting);
	}

	@Override
	public Long getSecsForAwaitinAlertPresent() {
		return (Long) getSetting(awaitinForAlertPresentSetting);
	}

}

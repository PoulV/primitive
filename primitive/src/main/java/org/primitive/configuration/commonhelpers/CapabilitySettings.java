package org.primitive.configuration.commonhelpers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.primitive.configuration.AbstractConfigurationAccessHelper;
import org.primitive.configuration.Configuration;

/**
 * @author s.tihomirov There are specified webdriver capabilities settings
 */
public class CapabilitySettings extends AbstractConfigurationAccessHelper
		implements HasCapabilities, Capabilities {

	// specified settings for capabilities
	private final String capabilityGroup = "DesiredCapabilities";
	private final DesiredCapabilities builtCapabilities = new DesiredCapabilities();
	private final String appCapability = "app";

	public CapabilitySettings(Configuration configuration) {
		super(configuration);
		buildCapabilities();
	}

	@Override
	public Capabilities getCapabilities() {
		return builtCapabilities;
	}

	@Override
	public Object getSetting(String name) {
		return getSettingValue(capabilityGroup, name);
	}

	@Override
	public Map<String, ?> asMap() {
		return builtCapabilities.asMap();
	}

	@Override
	public String getBrowserName() {
		return builtCapabilities.getBrowserName();
	}

	@Override
	public Object getCapability(String capabilityName) {
		return builtCapabilities.getCapability(capabilityName);
	}

	@Override
	public Platform getPlatform() {
		return builtCapabilities.getPlatform();
	}

	@Override
	public String getVersion() {
		return builtCapabilities.getVersion();
	}

	@Override
	public boolean is(String capabilityName) {
		return builtCapabilities.is(capabilityName);
	}

	@Override
	public boolean isJavascriptEnabled() {
		return builtCapabilities.isJavascriptEnabled();
	}

	private void buildCapabilities() {
		HashMap<String, Object> capabilities = getGroup(capabilityGroup);
		if (capabilities == null) {
			return;
		}

		List<String> capabilityStrings = new ArrayList<String>(
				capabilities.keySet());

		for (String capabilityStr : capabilityStrings) {
			if (capabilities.get(capabilityStr) != null) {
				builtCapabilities.setCapability(capabilityStr, capabilities.get(capabilityStr));
			}
		}
		transformCapabilities();
	}
	
	//transforms capabilities values if they need to be changed
	private void transformCapabilities(){
		//transforms relative path to application into absolute
		Object pathToApp = getCapability(appCapability);
		if (pathToApp != null){
			File app = new File(String.valueOf(pathToApp));
			builtCapabilities.setCapability(appCapability, app.getAbsolutePath());
		}		
		//if other actions need to be implemented code will be below 
	}

}

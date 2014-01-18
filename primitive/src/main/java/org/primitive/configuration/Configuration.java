/**
 * 
 */
package org.primitive.configuration;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Set;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.Capabilities;
import org.primitive.configuration.commonhelpers.WindowsTimeOuts;
import org.primitive.configuration.commonhelpers.CapabilitySettings;
import org.primitive.configuration.commonhelpers.ChromeDriverExe;
import org.primitive.configuration.commonhelpers.IEDriverExe;
import org.primitive.configuration.commonhelpers.Logging;
import org.primitive.configuration.commonhelpers.PhantomJSDriver;
import org.primitive.configuration.commonhelpers.ScreenShots;
import org.primitive.configuration.commonhelpers.WebDriverSettings;
import org.primitive.configuration.commonhelpers.WebDriverTimeOuts;

//for customizing project
public class Configuration {
	private final static String commonFileName = "settings.json"; // default settings
    // file  should be  put in project directory
	public final static Configuration byDefault = get(getPathToDefault("."));

	private static final String typeTag = "type";
	private static final String valueTag = "value";

	private final HashMap<String, HashMap<String, Object>> mappedSettings = new HashMap<String, HashMap<String, Object>>();
	private final HashMap<Class<? extends AbstractConfigurationAccessHelper>, AbstractConfigurationAccessHelper> initedHelpers = new HashMap<>();

	private static String getPathToDefault(String startPath) {
		// attempt to find configuration in the specified directory
		File defaultConfig = new File(startPath);
		File list[] = defaultConfig.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(commonFileName);
			}

		});

		if (list.length > 0) {
			return list[0].getPath();
		}

		if (list.length == 0) {
			File inner[] = defaultConfig.listFiles();
			String result = null;
			for (int i = 0; i < inner.length; i++) {
				if (inner[i].isDirectory()) {
					result = getPathToDefault(inner[i].getPath());
				}
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

	public static Configuration get(String filePath) {
		Callback interceptor = new ConfigurationInterceptor();

		Enhancer enhancer = new Enhancer();
		enhancer.setCallback(interceptor);
		enhancer.setSuperclass(Configuration.class);

		return (Configuration) enhancer.create(new Class[] { String.class },
				new Object[] { filePath });
	}

	// parsing of each one setting
	private HashMap<String, Object> getParsedGroup(JSONObject jsonObject) {
		HashMap<String, Object> result = new HashMap<>();
		@SuppressWarnings("unchecked")
		Set<String> keys = jsonObject.keySet();

		for (String key : keys) {
			JSONObject value = (JSONObject) jsonObject.get(key);
			String type = (String) value.get(typeTag);

			EAvailableDataTypes requiredType = null;
			try {
				requiredType = EAvailableDataTypes.valueOf(type);
			} catch (IllegalArgumentException | NullPointerException e) {
				throw new RuntimeException(
						"Type specification that is not supported! Specification is "
								+ String.valueOf(type)
								+ ". "
								+ " STRING, BOOL, LONG, FLOAT, INT are suppurted. Setting name is "
								+ key, e);
			}

			Object returnValue = null;
			String strValue = (String) value.get(valueTag);

			if ("".equals(strValue)) {
				result.put(key, returnValue);
			} else {
				result.put(key, requiredType.getValue(String.valueOf(strValue)));
			}
		}

		return result;

	}

	// parsing of json configuration
	private void parseSettings(String filePath) {

		File settingFile = new File(filePath);
		if (!settingFile.exists()) {
			return;
		}
		try {
			JSONObject jsonObject = (JSONObject) new JSONParser()
					.parse(new FileReader(settingFile));
			@SuppressWarnings("unchecked")
			Set<String> keys = jsonObject.keySet(); // there are groups
			for (String key : keys) {
				mappedSettings.put(key,
						getParsedGroup((JSONObject) jsonObject.get(key)));
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Configuration building has failed! Please, check it. You can look at SAMPLE_SETTING.json for verifying. ",
					e);
		}
	}

	// gets setting group from mapped serrings
	public HashMap<String, Object> getSettingGroup(String groupName) {
		return mappedSettings.get(groupName);
	}

	public Object getSettingValue(String groupName, String settingName) {
		HashMap<String, Object> group = getSettingGroup(groupName);
		// if there is no group with specified name
		if (group == null) {
			return null;
		}
		return group.get(settingName);
	}

	/**
	 * @author s.tihomirov This method is similar as HashMap<String, Object>
	 *         getSettingGroup(String groupName). But it returns some helper
	 *         instead of HashMap. This helper makes access to required section
	 *         or set of setting sections easier. Class of the required helper
	 *         should be implemented with constructor like this: new
	 *         Helper(org.primitive.configuration.Configuration configuration)
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractConfigurationAccessHelper> T getSection(
			Class<T> requiredClass) {
		T helper = (T) initedHelpers.get(requiredClass);
		if (helper != null) // if helper is already initiated
		{
			return (T) helper;
		}

		try {
			Constructor<?> requiredConstructor = requiredClass
					.getConstructor(Configuration.class);
			helper = (T) requiredConstructor.newInstance(this);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}

		initedHelpers.put(requiredClass, helper);
		return helper;
	}

	public Capabilities getCapabilities() {
		return (Capabilities) getSection(CapabilitySettings.class);
	}

	public ChromeDriverExe getChromeDriverSettings() {
		return getSection(ChromeDriverExe.class);
	}

	public IEDriverExe getIEDriverSettings() {
		return getSection(IEDriverExe.class);
	}

	public PhantomJSDriver getPhantomJSDriverSettings() {
		return getSection(PhantomJSDriver.class);
	}

	public WebDriverTimeOuts getWebDriverTimeOuts() {
		return getSection(WebDriverTimeOuts.class);
	}

	public ScreenShots getScreenShots() {
		return getSection(ScreenShots.class);
	}

	public Logging getLogging() {
		return getSection(Logging.class);
	}

	public WebDriverSettings getWebDriverSettings() {
		return getSection(WebDriverSettings.class);
	}

	public WindowsTimeOuts getBrowserWindowsTimeOuts() {
		return getSection(WindowsTimeOuts.class);
	}

	protected Configuration(String filePath) {
		super();
		parseSettings(String.valueOf(filePath));
	}
}
package googletest.desctop;

import googledescripription.AnyPage;
import googledescripription.Google;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import junit.framework.Assert;

import org.openqa.selenium.Platform;
import org.primitive.configuration.Configuration;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;

public class HelloWorldGoogleTest3 {
	private final List<Configuration> configs = new ArrayList<Configuration>();
	private final String whereAreConfigs = "src/test/resources/configs/desctop/definedDriverServicesOnDifferentOS";

	private final HashMap<Platform, String> configEndsWithMap = new HashMap<Platform, String>() {
		private static final long serialVersionUID = 3206882138088360263L;
		{
			put(Platform.WINDOWS, "_win.json");
		}

	};

	@Test(description = "A test with defined paths to WD service binary files. This files are defined for each operating system")
	public void typeHelloWorldAndOpenTheFirstLink() {
		for (Configuration config: configs){
			Google google = Google.getNew(config);
			test(google);
		}
	}

	@BeforeClass
	public void beforeClass() {
		Platform current = Platform.getCurrent();
		Set<Entry<Platform, String>> rules = configEndsWithMap.entrySet();
		for (Entry<Platform, String> rule : rules) {
			if (rule.getKey().is(current)) {
				final String endsWith = rule.getValue();
				File[] configJSONs = new File(whereAreConfigs)
						.listFiles(new FilenameFilter() {
							@Override
							public boolean accept(File dir, String name) {
								return name.endsWith(endsWith);
							}

						});

				for (File configJSON : configJSONs) {
					configs.add(Configuration.get(configJSON.getAbsolutePath()));
				}
				return;
			}
		}
	}
  
	private void test(Google google) {
		try {
			google.performSearch("Hello world Wikipedia");
			Assert.assertEquals(10, google.getLinkCount());
			google.clickOnByMouse(1);
			AnyPage anyPage = google.getFromWindow(AnyPage.class, 1);
			anyPage.close();
			google.clickOnByMouse(1);
			anyPage = google.getFromWindow(AnyPage.class, 1);
		} finally {
			google.quit();
		}
	}

}

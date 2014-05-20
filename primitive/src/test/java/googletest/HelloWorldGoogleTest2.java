package googletest;
import googledescripription.AnyPage;
import googledescripription.Google;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import mocklistener.MockTestListener;

import org.openqa.selenium.Platform;
import org.primitive.configuration.Configuration;
import org.primitive.logging.Log;
import org.primitive.webdriverencapsulations.webdrivercomponents.Awaiting;
import org.primitive.webdriverencapsulations.webdrivercomponents.FluentWindowConditions;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

@Listeners(MockTestListener.class)
public class HelloWorldGoogleTest2 {
	
	
	private enum HowToGetANewWindow {
		BYPARTIALTITLE {
			@Override
			AnyPage get(Google google) {
				Awaiting awaiting = new Awaiting(google.getWrappedDriver());
				FluentWindowConditions fluentWindowConditions = new FluentWindowConditions(
						google.getWrappedDriver());
				try {
					awaiting.awaitCondition(4, fluentWindowConditions
							.newWindowIsAppeared("Hello, world*"));
					return super.get(google);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		},
		BYPARTIALURL{
			@Override
			AnyPage get(Google google) {
				Awaiting awaiting = new Awaiting(google.getWrappedDriver());
				ArrayList<String> expectedURLs = new ArrayList<String>() {
					private static final long serialVersionUID = 1L;
					{
						add("wikipedia.org/wiki/?(\\?.*)?");
					}
				};

				try {
					awaiting.awaitCondition(4, new FluentWindowConditions(
							google.getWrappedDriver())
							.newWindowIsAppeared(expectedURLs));
					return super.get(google);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			
		};
		
		AnyPage get(Google google){
			return google.getFromWindow(AnyPage.class, 1);
		}
	}

	private class WaitingThread extends Thread {
        private final Google google;
        private final HowToGetANewWindow howToGet;
        private Exception exception;
        private AnyPage anyPage;
        private boolean isRunning = false;
        
		private WaitingThread(Google google, HowToGetANewWindow howToGet){
			this.google = google;
			this.howToGet = howToGet; 
		}
		
		@Override
		public void run(){
			isRunning = true;
			try	{
				anyPage = howToGet.get(google);
			}
			catch (Exception e){
				exception = e;
				throw e;
			}
			finally{
				isRunning = false;
			}
		}
	}

	// settings according to current OS
	private final HashMap<Platform, List<String>> settings = new HashMap<Platform, List<String>>();
	
	private void test(Google google, HowToGetANewWindow howToGet, boolean toClickOnALinkWhichWasFound) throws Exception {
		WaitingThread waitingThread = new WaitingThread(google, howToGet);
		waitingThread.start();
		Thread.sleep(1000);
		if (!toClickOnALinkWhichWasFound){
			google.openLinkByIndex(1);
		}
		else{
			google.clickOnLinkByIndex(1);
		}
		while (waitingThread.isRunning){Log.message("Waiting for...");}
		if (waitingThread.exception != null) {
			throw new RuntimeException(waitingThread.exception);
		}
		Thread.sleep(3000);
		waitingThread.anyPage.close();
	}
	
	@Test(description = "This is just a test of basic functionality. It gets a new object by its partial title and url")
	@Parameters(value={"path", "toClick","configList","howToGetANewWindow"})
	public void typeHelloWorldAndOpenTheFirstLink(
			@Optional("src/test/resources/configs/desctop/") String path,
			@Optional("false") String toClick,
			@Optional("") String configList,
			@Optional("BYPARTIALURL") String howToGetANewWindow)
			throws Exception {
		
		List<String> configs = getConfigsByCurrentPlatform();
		String[] configNames = configList.split(",");
		
		for (String config: configNames){
			if (!configs.contains(config)){
				continue;
			}
			Configuration configuration = Configuration
					.get(path + config);
			Google google = Google.getNew(configuration);
			try {
				String[] howToVars = howToGetANewWindow.split(",");
				google.performSearch("Hello world Wikipedia");
				for (String howTo: howToVars){
					test(google, HowToGetANewWindow.valueOf(howTo), new Boolean(toClick));
				}
			} finally {
				google.quit();
			}
		}
	}

	@BeforeTest
	public void beforeTest() {
		//for Windows
		settings.put(Platform.WINDOWS, new ArrayList<String>(){
			private static final long serialVersionUID = -1718278594717074313L;
			{
				add("chrome_remote.json");
				add("chrome.json");
				
				add("firefox_remote.json");
				add("firefox.json");
				
				add("internetexplorer_remote.json");
				add("internetexplorer.json");
				
				add("phantomjs_remote.json");
				add("phantomjs.json");
				
				add("android_emulator_chrome.json");
			}
			
		});
		//for MAC
		settings.put(Platform.MAC, new ArrayList<String>(){
			private static final long serialVersionUID = -1718278594717074313L;
			{
				add("chrome_remote.json");
				add("chrome.json");
				
				add("firefox_remote.json");
				add("firefox.json");
				
				add("safari_remote.json");
				add("safari.json");
				
				add("phantomjs_remote.json");
				add("phantomjs.json");
			}
			
		});
		
	}

	List<String> getConfigsByCurrentPlatform(){
		Set<Entry<Platform, List<String>>> entries = settings.entrySet();
		for (Entry<Platform, List<String>> entry: entries){
			if (entry.getKey().is(Platform.getCurrent())){
				return entry.getValue();
			}
		}
		
		return new ArrayList<String>();
	}
}

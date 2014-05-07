package org.primitive.webdriverservices;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;
import org.primitive.configuration.Configuration;
import org.primitive.webdriverservices.interfaces.ILocalServerLauncher;

/**
 * 
 * Launches {@link RemoteWebDriver} server locally 
 *
 */
public class RemoteSeleniumServerLauncer implements ILocalServerLauncher {

	private SeleniumServer server;
	private final RemoteControlConfiguration rcc;
	final boolean slowResources = false;
	final String defaultLocalHost = "http://localhost:4444/wd/hub";
	final int defaulPort = 4444;
	
	public RemoteSeleniumServerLauncer() {
		rcc = new RemoteControlConfiguration();
		rcc.setPort(RemoteControlConfiguration.DEFAULT_PORT);
	}


	@Override
	public boolean isLaunched() {
		if (server == null){
			return false;
		}
		return server.getServer().isStarted();
	}

	@Override
	public int getPort() {
		return rcc.getPort();
	}

	@Override
	public void setPort(int port) {
		rcc.setPort(port);
	}

	@Override
	public void stop() {
		if (server == null){
			return;
		}
		server.stop();
	}


	@Override
	public void launch() throws Exception {		
		try {
			server = new SeleniumServer(slowResources, rcc);
			server.start();
		} catch (Exception e) {
			throw new WebDriverException(
					"Cann't start server on localhost!", e);
		}	
	}


	@Deprecated
	@Override
	public void resetAccordingTo(Configuration config) {
		//Does nothing		
	}


	@Override
	public URL getLocalHost() {
		URL localHost = null;
		try {
			localHost = new URL(defaultLocalHost.replace(
					Integer.toString(defaulPort),
					Integer.toString(rcc.getPort())));
		} catch (MalformedURLException ignored) {
		}
		return localHost;
	}

}

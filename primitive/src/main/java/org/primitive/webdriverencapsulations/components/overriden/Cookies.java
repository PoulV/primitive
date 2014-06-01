package org.primitive.webdriverencapsulations.components.overriden;

import java.util.Set;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.primitive.webdriverencapsulations.components.WebdriverComponent;

public final class Cookies extends WebdriverComponent {

	public Cookies(WebDriver driver) {
		super(driver);
	}

	public void addCookie(Cookie cookie) {
		driver.manage().addCookie(cookie);
	}

	public void deleteAllCookies() {
		driver.manage().deleteAllCookies();
	}

	public void deleteAllCookies(String cookieName) {
		driver.manage().deleteCookieNamed(cookieName);
	}

	public void deleteCookie(Cookie cookie) {
		driver.manage().deleteCookie(cookie);
	}

	public Cookie getCookieNamed(String cookieName) {
		return driver.manage().getCookieNamed(cookieName);
	}

	public Set<Cookie> getCookies() {
		return driver.manage().getCookies();
	}

}

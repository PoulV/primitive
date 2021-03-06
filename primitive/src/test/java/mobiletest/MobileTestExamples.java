package mobiletest;

import io.appium.java_client.AndroidKeyCode;
import junit.framework.Assert;
import mobiledescription.android.bbc.BBCMain;
import mobiledescription.android.bbc.TopicList;
import mobiledescription.android.selendroid.testapp.HomeScreenActivity;
import mobiledescription.android.selendroid.testapp.RegisterANewUser;
import mobiledescription.android.selendroid.testapp.Webview;
import mobiledescription.ios.uicatalog.ActionSheets;
import mobiledescription.ios.uicatalog.AlertView;
import mobiledescription.ios.uicatalog.UICatalog;

import org.primitive.configuration.Configuration;
import org.primitive.model.common.mobile.MobileAppliction;
import org.primitive.model.common.mobile.MobileFactory;
import org.testng.annotations.Test;

public class MobileTestExamples {
	
  @Test
  public void androidNativeAppTest() {
		Configuration config = Configuration
				.get("src/test/resources/configs/mobile/app/android/android_bbc.json");
		MobileAppliction bbc = MobileFactory.getApplication(
				MobileAppliction.class, config);
		try {
			BBCMain bbcMain = bbc.getPart(BBCMain.class);
			Assert.assertNotSame("", bbcMain.getAppStrings());
			Assert.assertNotSame(0, bbcMain.getArticleCount());
			Assert.assertNotSame("", bbcMain.getArticleTitle(1));
			Assert.assertNotSame(bbcMain.getArticleTitle(1),
					bbcMain.getArticleTitle(0));
			bbcMain.selectArticle(1);
			Assert.assertEquals(true, bbcMain.isArticleHere());
			bbcMain.pinchArticle();
			bbcMain.zoomArticle();
			
			bbcMain.refresh();
			bbcMain.edit();

			TopicList topicList = bbcMain.getPart(TopicList.class);
			topicList.setTopicChecked("LATIN AMERICA", true);
			topicList.setTopicChecked("UK", true);
			topicList.ok();

			bbcMain.edit();
			topicList.setTopicChecked("LATIN AMERICA", false);
			topicList.setTopicChecked("UK", false);
			topicList.ok();
			
			bbcMain.sendKeyEvent(AndroidKeyCode.ENTER);
			bbcMain.play();
		} finally {
			bbc.quit();
		}	  
  }
  
  @Test
  public void androidHybridAppTest() {
		Configuration config = Configuration
				.get("src/test/resources/configs/mobile/app/android/android_selendroid-test-app.json");
		MobileAppliction selendroidTestApp = MobileFactory.getApplication(
				MobileAppliction.class, config);		
		try {
			HomeScreenActivity homeScreenActivity = selendroidTestApp.getPart(HomeScreenActivity.class);
			homeScreenActivity.fillMyTextField("Test text. Hello world!");
			homeScreenActivity.clickOnVisibleButtonTest();
			homeScreenActivity.waitForVisibleTextIsVisible(10);
			Assert.assertEquals("Text is sometimes displayed", 
					homeScreenActivity.getVisibleTextView());
			homeScreenActivity.waitingButtonTestClick();
			
			RegisterANewUser registerForm = selendroidTestApp.getPart(RegisterANewUser.class);
			registerForm.inputUsername("MrSergeyTikhomirov");
			registerForm.inputEmail("tichomirovsergey@gmail.com");
			registerForm.inputPassword("test666");
			registerForm.inputName("Mr Sergey Tikhomirov");
			registerForm.clickVerifyUser();
			registerForm.clickRegisterUser();
			
			//hybrid part
			homeScreenActivity.startWebviewClick();
			Webview webview = selendroidTestApp.getFromHandle(Webview.class, 1);			
			webview.setName("Sergey");
			webview.selectCar("mercedes");
			webview.sendMeYourName();
			homeScreenActivity.goBackClick();
			
			homeScreenActivity.startWebviewClick();
			webview = selendroidTestApp.getFromHandle(Webview.class, "WEBVIEW_0");			
			webview.setName("Sergey");
			webview.selectCar("mercedes");
			webview.sendMeYourName();
			homeScreenActivity.goBackClick();
		} finally {
			selendroidTestApp.quit();
		}
	}
  
    @Test
  	public void iOSUICatalogTest() {
  		Configuration config = Configuration
				.get("src/test/resources/configs/mobile/app/ios/ios_uiCatalog.json");
		MobileAppliction uiCatalog = MobileFactory.getApplication(
				MobileAppliction.class, config);
		UICatalog uicatalog = uiCatalog.getPart(UICatalog.class);
		uicatalog.shake();
		uicatalog.selectItem("Action Sheets, AAPLActionSheetViewController");
		
		ActionSheets actionSheets = uiCatalog.getPart(ActionSheets.class);
		actionSheets.clickOnOk_Cancel();
		actionSheets.clickOnSplashButton("OK");
		actionSheets.clickOnOther();
		actionSheets.clickOnSplashButton("Safe Choice");
		
		uicatalog.backToMe();
		uicatalog.selectItem("Alert Views, AAPLAlertViewController");
		
		AlertView alertView = uiCatalog.getPart(AlertView.class);
		alertView.invokeSimpleAlert();		
		uiCatalog.getContextManager().getAlert().dismiss();	
		uicatalog.backToMe();
		
		uiCatalog.quit();
  	}
}

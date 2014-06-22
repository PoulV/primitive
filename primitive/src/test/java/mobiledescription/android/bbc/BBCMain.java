package mobiledescription.android.bbc;

import io.appium.java_client.TouchAction;

import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primitive.model.common.mobile.Context;
import org.primitive.webdriverencapsulations.SingleContext;
import org.primitive.webdriverencapsulations.interfaces.ISendsKeyEvent;


public class BBCMain extends Context implements IBar, ISendsKeyEvent{
	
	@FindBy(id = "bbc.mobile.news.ww:id/articleWrapper")
	private List<WebElement> articles;
	@FindBy(id = "bbc.mobile.news.ww:id/articleWebView")
	private WebElement currentArticle;			
	@FindBy(id = "bbc.mobile.news.ww:id/optMenuShareAction")
	private WebElement share;
	@FindBy(id = "bbc.mobile.news.ww:id/optMenuWatchListenAction")
	private WebElement play;
	@FindBy(id = "bbc.mobile.news.ww:id/optMenuEditAction")
	private WebElement edit;
	
	protected BBCMain(SingleContext context) {
		super(context);
		load();
	}

	@InteractiveMethod
	public int getArticleCount(){
		return articles.size();
	}
	
	@InteractiveMethod
	public String getArticleTitle(int index){
			
		WebElement article = null;
		try{
			article = articles.get(index);
		}
		catch (Exception e){
			int count = articles.size();
			throw new NoSuchElementException("Required article index is more than actual article list size " + count);
		}
		
		return article.findElement(By.id("bbc.mobile.news.ww:id/articleTitleId")).getText();
	}
	
	@InteractiveMethod
	public void selectArticle(int index){
		try{
			articles.get(index).click();
		}
		catch (Exception e){
			int count = articles.size();
			throw new NoSuchElementException("Required article index is more than actual article list size " + count);
		}		
	}
	
	@InteractiveMethod
	public boolean isArticleHere(){
		return currentArticle.isDisplayed();
	}

	@Override
	@InteractiveMethod
	public void refresh() {
		byAndroidUIAutomator
				.findElementByAndroidUIAutomator(
						"new UiSelector().resourceId(\"bbc.mobile.news.ww:id/optMenuRefreshAction\")")
				.click();		
	}

	@Override
	@InteractiveMethod
	public void share() {
		share.click();		
	}

	@Override
	@InteractiveMethod
	public void play() {
		play.click();		
	}

	@Override
	@InteractiveMethod
	public void edit() {
		TouchAction touchAction = touchActions.getTouchAction();
		touchAction.tap(edit);
		touchActionsPerformer.performTouchAction(touchAction);
	}

	@Override
	@InteractiveMethod
	public void sendKeyEvent(int key) {
		keyEventSender.sendKeyEvent(key);		
	}

}

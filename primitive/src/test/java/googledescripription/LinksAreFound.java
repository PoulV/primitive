package googledescripription;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primitive.exceptions.ConcstructTestObjectException;
import org.primitive.testobjects.FunctionalPart;
import org.primitive.webdriverencapsulations.SingleWindow;

public class LinksAreFound extends FunctionalPart implements IPerformsClickOnALink {
	
	@FindBy(xpath = ".//*[@class='r']/a")
	private List<WebElement> linksAreFound;
	
	//����� ������� �������� google. ����� ������ ����� � ���������� ����, �� ������� ����� 
	public LinksAreFound(SingleWindow browserWindow)
			throws ConcstructTestObjectException {
		super(browserWindow);
		load();
	}

	@InteractiveMethod
	public void clickOn(int index) {
		linksAreFound.get(0).click();		
	}

	@Deprecated
	public void clickOn(String text) {
		// It does nothing		
	}

	@InteractiveMethod
	public int getLinkCount() {
		return linksAreFound.size();
	}

}
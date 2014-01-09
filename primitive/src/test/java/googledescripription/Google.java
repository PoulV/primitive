package googledescripription;

import org.primitive.configuration.Configuration;
import org.primitive.testobjects.ConcstructTestObjectException;
import org.primitive.testobjects.Entity;
import org.primitive.testobjects.ObjectFactory;
import org.primitive.webdriverencapsulations.SingleWindow;
import org.primitive.webdriverencapsulations.WebDriverEncapsulation;

//������ google ��� ����������
public class Google extends Entity implements IPerformsSearch, IPerformsClickOnALink{
	
	private final static String url = "http://www.google.com/";
	private SearchBar searchBar;
	private LinksAreFound linksAreFound;
	
    //�� ��, ���������� ���� ��� ���� ����. ����� ���-���������� ����� ���� 
	//� �������� ������� ���������
	public Google(SingleWindow browserWindow)
			throws ConcstructTestObjectException {
		super(browserWindow);
		searchBar     = getPart(SearchBar.class);    //��� �������� �������� ��� �� ������ ����������
		linksAreFound = getPart(LinksAreFound.class); //����� ��� ��� ��� �� ��������� ���� �������
	}
	
	//��� ��������� google ����� � ����
	//���������� �� ������� ��������� ������������
	public static Google getNew()
	{
		return ObjectFactory.getEntity(Google.class, url);
	}
	
	public static Google getNew(Configuration config)
	{
		return ObjectFactory.getEntity(Google.class, config, url);
	}
	
	public static Google getNew(WebDriverEncapsulation externalEncapsulation)
	{
		return ObjectFactory.getEntity(externalEncapsulation, Google.class, url);
	}

	public void performSearch(String searchString) {
		searchBar.performSearch(searchString);		
	}

	public void clickOn(int index) {
		linksAreFound.clickOn(index);		
	}

	@Deprecated
	public void clickOn(String text) {
		//It does nothing		
	}

	public int getLinkCount() {
		return linksAreFound.getLinkCount();
	}
}

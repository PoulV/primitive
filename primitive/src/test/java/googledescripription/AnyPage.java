package googledescripription;

import org.primitive.exceptions.ConcstructTestObjectException;
import org.primitive.testobjects.FunctionalPart;
import org.primitive.webdriverencapsulations.SingleWindow;

//����� ��������� ��������, �������� ����� google
public class AnyPage extends FunctionalPart {

	public AnyPage(SingleWindow browserWindow)
			throws ConcstructTestObjectException {
		super(browserWindow);
	}
}

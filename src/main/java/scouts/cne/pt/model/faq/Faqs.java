package scouts.cne.pt.model.faq;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anco62000465 2018-09-07
 *
 */
public class Faqs
{
	private List< Faq > faqList = new ArrayList<>();
	/**
	 * constructor
	 * @author anco62000465 2018-09-07
	 */
	public Faqs()
	{
	}

	/**
	 * Getter for faqList
	 * 
	 * @author anco62000465 2018-09-07
	 * @return the faqList {@link List<Faq>}
	 */
	public List< Faq > getFaqList()
	{
		return faqList;
	}

	/**
	 * Setter for faqList
	 * 
	 * @author anco62000465 2018-09-07
	 * @param faqList the faqList to set
	 */
	public void setFaqList( List< Faq > faqList )
	{
		this.faqList = faqList;
	}
}

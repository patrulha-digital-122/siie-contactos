package scouts.cne.pt.model.emails;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anco62000465 2018-09-07
 *
 */
public class Templates
{
	private List< EmailTemplate > emailTemplateList = new ArrayList<>();

	/**
	 * constructor
	 * @author anco62000465 2018-09-07
	 */
	public Templates()
	{
	}

	/**
	 * Getter for emailTemplateList
	 * @author anco62000465 2018-09-27
	 * @return the emailTemplateList  {@link List<EmailTemplate>}
	 */
	public List< EmailTemplate > getEmailTemplateList()
	{
		return emailTemplateList;
	}

	/**
	 * Setter for emailTemplateList
	 * @author anco62000465 2018-09-27
	 * @param emailTemplateList the emailTemplateList to set
	 */
	public void setEmailTemplateList( List< EmailTemplate > emailTemplateList )
	{
		this.emailTemplateList = emailTemplateList;
	}
	
}

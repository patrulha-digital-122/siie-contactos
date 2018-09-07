package scouts.cne.pt.model.faq;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author anco62000465 2018-09-07
 *
 */
public class Faq
{
	@JsonProperty( "question" )
	private String	question;
	@JsonProperty( "response" )
	private String	response;
	@JsonProperty( "isHTML" )
	private boolean	isHTML;
	/**
	 * constructor
	 * @author anco62000465 2018-09-07
	 */
	public Faq()
	{
	}

	/**
	 * Getter for question
	 * 
	 * @author anco62000465 2018-09-07
	 * @return the question {@link String}
	 */
	public String getQuestion()
	{
		return question;
	}

	/**
	 * Setter for question
	 * 
	 * @author anco62000465 2018-09-07
	 * @param question the question to set
	 */
	public void setQuestion( String question )
	{
		this.question = question;
	}

	/**
	 * Getter for response
	 * 
	 * @author anco62000465 2018-09-07
	 * @return the response {@link String}
	 */
	public String getResponse()
	{
		return response;
	}

	/**
	 * Setter for response
	 * 
	 * @author anco62000465 2018-09-07
	 * @param response the response to set
	 */
	public void setResponse( String response )
	{
		this.response = response;
	}

	/**
	 * Getter for isHTML
	 * 
	 * @author anco62000465 2018-09-07
	 * @return the isHTML {@link boolean}
	 */
	public boolean isHTML()
	{
		return isHTML;
	}

	/**
	 * Setter for isHTML
	 * 
	 * @author anco62000465 2018-09-07
	 * @param isHTML the isHTML to set
	 */
	public void setHTML( boolean isHTML )
	{
		this.isHTML = isHTML;
	}
}

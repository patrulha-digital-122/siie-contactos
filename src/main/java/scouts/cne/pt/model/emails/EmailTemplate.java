package scouts.cne.pt.model.emails;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author anco62000465 2018-09-07
 *
 */
public class EmailTemplate
{
	@JsonProperty( "nome" )
	private String	nome;
	@JsonProperty( "value" )
	private String	value;
	/**
	 * constructor
	 * @author anco62000465 2018-09-07
	 */
	public EmailTemplate()
	{
	}
	
	/**
	 * Getter for nome
	 * 
	 * @author anco62000465 2018-09-27
	 * @return the nome {@link String}
	 */
	public String getNome()
	{
		return nome;
	}
	
	/**
	 * Setter for nome
	 * 
	 * @author anco62000465 2018-09-27
	 * @param nome the nome to set
	 */
	public void setNome( String nome )
	{
		this.nome = nome;
	}
	
	/**
	 * Getter for value
	 * 
	 * @author anco62000465 2018-09-27
	 * @return the value {@link String}
	 */
	public String getValue()
	{
		return value;
	}
	
	/**
	 * Setter for value
	 * 
	 * @author anco62000465 2018-09-27
	 * @param value the value to set
	 */
	public void setValue( String value )
	{
		this.value = value;
	}


}

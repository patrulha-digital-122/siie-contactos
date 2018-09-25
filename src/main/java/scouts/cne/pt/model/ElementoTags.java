package scouts.cne.pt.model;

import java.util.Objects;

/**
 * @author anco62000465 2018-09-25
 *
 */
public enum ElementoTags
{
	NIN( "nin", "NIN" ),
	NOME( "nome", "Nome do elemento" ),
	NIF( "nif", "Numero identificação fiscal" ),
	DATA_NASCIMENTO( "dtNasc", "Data de nascimento" ),
	MORADA( "morada", "Morada" ),
	LOCALIDADE( "localidade", "Localidade" ),
	CP4( "cp4", "Código Postal (Região - os primeiros 4 digitos)" ),
	CP3( "cp3", "Código Postal - os 3 últimos digitos" ),
	TELEFONE( "telefone", "Telefone" ),
	TELEMOVEL( "telemovel", "Telemóvel" ),
	EMAIL( "email", "E-mail" ),
	NOME_PAI( "nomepai", "Nome do Pai" ),
	PROFISSAO_PAI( "profissaopai", "Profissão do Pai" ),
	TELEFONE_PAI( "telefonepai", "Telefone do Pai" ),
	EMAIL_PAI( "emailpai", "E-mail do Pai" ),
	NOME_MAE( "nomemae", "Nome do Mãe" ),
	PROFISSAO_MAE( "profissaomae", "Profissão do Mãe" ),
	TELEFONE_MAE( "telefonemae", "Telefone do Mãe" ),
	EMAIL_MAE( "emailmae", "E-mail do Mãe" ),
	NATURALIDADE( "naturalidade", "Naturalidade" );
	//
	private final String	strTagId;
	private final String	strTagDescription;

	/**
	 * constructor
	 * 
	 * @author anco62000465 2018-09-25
	 * @param strTagId
	 * @param strTagDescription
	 * @param strTagReplace
	 */
	private ElementoTags( String strTagId, String strTagDescription )
	{
		this.strTagId = strTagId;
		this.strTagDescription = strTagDescription;
	}

	/**
	 * Getter for strTagId
	 * 
	 * @author anco62000465 2018-09-25
	 * @return the strTagId {@link String}
	 */
	public String getTagId()
	{
		return strTagId;
	}

	/**
	 * Getter for strTagDescription
	 * 
	 * @author anco62000465 2018-09-25
	 * @return the strTagDescription {@link String}
	 */
	public String getTagDescription()
	{
		return strTagDescription;
	}

	/**
	 * Getter for strTagReplace
	 * 
	 * @author anco62000465 2018-09-25
	 * @return the strTagReplace {@link String}
	 */
	public String getTagReplace()
	{
		return "%" + name() + "%";
	}

	/**
	 * The <b>convertHTML</b> method returns {@link String}
	 * 
	 * @author anco62000465 2018-09-25
	 * @param value
	 * @param next
	 * @return
	 */
	public static String convertHTML( String value, Elemento elemento )
	{
		for ( ElementoTags tags : values() )
		{
			value = value.replace( tags.getTagReplace(), Objects.toString( elemento.getAtributo( tags.getTagId() ), "" ) );
		}
		return value;
	}
}

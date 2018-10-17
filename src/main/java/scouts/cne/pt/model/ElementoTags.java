package scouts.cne.pt.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author anco62000465 2018-09-25
 *
 */
public enum ElementoTags
{
	NIN( "nin", "Número de Identificação Nacional - CNE" ),
	NOME( "nome", "Nome completo" ),
	NIF( "nif", "Número Identificação Fiscal" ),
	DATA_NASCIMENTO( "dtNasc", "Data de Nascimento" ),
	DATA_PROMESSA( "dtpromessa", "Data da Promessa" ),
	DATA_ADMISSAO( "dtadmissao", "Data de Admissão" ),
	MORADA( "morada", "Morada" ),
	LOCALIDADE( "localidade", "Localidade" ),
	CP4( "cp4", "Código Postal (os primeiros 4 digitos)" ),
	CP3( "cp3", "Código Postal (os 3 últimos digitos)" ),
	TELEFONE( "telefone", "Telefone" ),
	TELEMOVEL( "telemovel", "Telemóvel" ),
	EMAIL( "email", "E-mail" ),
	NOME_PAI( "nomepai", "Nome do Pai" ),
	PROFISSAO_PAI( "profissaopai", "Profissão do Pai" ),
	TELEFONE_PAI( "telefonepai", "Telefone do Pai" ),
	EMAIL_PAI( "emailpai", "E-mail do Pai" ),
	NOME_MAE( "nomemae", "Nome da Mãe" ),
	PROFISSAO_MAE( "profissaomae", "Profissão da Mãe" ),
	TELEFONE_MAE( "telefonemae", "Telefone da Mãe" ),
	EMAIL_MAE( "emailmae", "E-mail da Mãe" ),
	NOTAS( "notas", "Notas" ),
	OBSERVACOES( "observacoes", "Observações" ),
	TOTEM( "totem", "Totem" ),
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
		for ( final ElementoTags tags : values() )
		{
			value = value.replace( tags.getTagReplace(), Objects.toString( elemento.getAtributo( tags.getTagId().toLowerCase() ), "" ) );
		}
		return value;
	}

	public static List< ElementoTags > getGoogleImportTags()
	{
		final List< ElementoTags > tags = new LinkedList<>();
		tags.add( ElementoTags.NOME );
		tags.add( ElementoTags.NIF );
		tags.add( ElementoTags.DATA_NASCIMENTO );
		tags.add( ElementoTags.MORADA );
		tags.add( ElementoTags.TELEFONE );
		tags.add( ElementoTags.TELEMOVEL );
		tags.add( ElementoTags.EMAIL );
		// MAE
		tags.add( ElementoTags.NOME_MAE );
		tags.add( ElementoTags.EMAIL_MAE );
		tags.add( ElementoTags.TELEFONE_MAE );
		tags.add( ElementoTags.PROFISSAO_MAE );
		// Pai
		tags.add( ElementoTags.NOME_PAI );
		tags.add( ElementoTags.EMAIL_PAI );
		tags.add( ElementoTags.TELEFONE_PAI );
		tags.add( ElementoTags.PROFISSAO_PAI );

		return tags;
	}

	public static List< ElementoTags > getUnchagedTags()
	{
		final List< ElementoTags > tags = new LinkedList<>();
		tags.add( ElementoTags.NIN );
		tags.add( ElementoTags.DATA_PROMESSA );
		tags.add( ElementoTags.DATA_ADMISSAO );

		return tags;
	}
}

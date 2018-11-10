package scouts.cne.pt.model;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.mail.internet.InternetAddress;
import org.apache.commons.lang3.StringUtils;
import scouts.cne.pt.app.HasLogger;

/**
 * Created by Andr� on 03/10/2015.
 */
public class Elemento implements Comparable< Elemento >, HasLogger
{
	private final HashMap< String, Object > listaAtributos;

	/**
	 *
	 */
	public Elemento()
	{
		listaAtributos = new HashMap<>();
	}

	/**
	 * @return the nome
	 */
	public String getAgrupamento()
	{
		return StringUtils.trimToEmpty( getAtributo( "agrupamento" ).toString() );
	}

	/**
	 * @return the nome
	 */
	public String getNome()
	{
		return getAtributo( "nome" ).toString();
	}

	/**
	 * @return the nome
	 */
	public String getNomeProprio()
	{
		final LinkedList< String > l = new LinkedList<>();
		for ( final String string : getAtributo( "nome" ).toString().split( " " ) )
		{
			l.add( string );
		}
		return l.getFirst();
	}

	/**
	 * @return the nome
	 */
	public String getNomeApelido()
	{
		final LinkedList< String > l = new LinkedList<>();
		for ( final String string : getAtributo( "nome" ).toString().split( " " ) )
		{
			l.add( string );
		}
		return l.getLast();
	}

	/**
	 * @return the morada
	 */
	public String getMorada()
	{
		return getAtributo( "morada" ).toString();
	}

	/**
	 * @return the localidade
	 */
	public String getLocalidade()
	{
		return getAtributo( "localidade" ).toString();
	}

	/**
	 * @return the localidade
	 */
	public boolean isActivo()
	{
		return getAtributo( "situacao" ).toString().equals( "Activo" );
	}

	/**
	 * @return the localidade
	 */
	public boolean isMasculino()
	{
		return getAtributo( "sexo" ).toString().equals( "M" );
	}

	/**
	 * @return the telefone
	 */
	public String getTelefone()
	{
		return getAtributo( "telefone" ).toString();
	}

	/**
	 * @return the telefone
	 */
	public SECCAO getCategoria()
	{
		return SECCAO.findSeccao( getAtributo( "seccao" ).toString() );
	}

	/**
	 * @return the codigoPostal
	 */
	public String getCodigoPostal()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append( getAtributo( "cp4" ).toString() );
		sb.append( "-" );
		sb.append( getAtributo( "cp3" ).toString() );
		sb.append( " " );
		sb.append( getAtributo( "codigopostal" ).toString() );
		return sb.toString().trim();
	}

	/**
	 * @return the dataNascimento
	 */
	public Date getDataNascimento()
	{
		final Object dataNascimento = getAtributo( "dtnasc" );
		return getDate( dataNascimento );
	}

	/**
	 * @return the telemovel
	 */
	public String getTelemovel()
	{
		return getAtributo( "telemovel" ).toString();
	}

	/**
	 * @return the email
	 */
	public String getEmail()
	{
		return ( String ) getAtributo( "email" );
	}

	/**
	 * @return the email
	 */
	public String getEmailPrincipal()
	{
		String email = ( String ) getAtributo( "email" );
		if ( email == null )
		{
			email = getEmailMae();
			if ( email == null )
			{
				email = getEmailPai();
			}
		}
		return email;
	}

	/**
	 * @return the email
	 */
	public String getEmailPrincipalGoogle()
	{
		final StringBuilder stringBuilder = new StringBuilder();
		if ( !getEmail().isEmpty() )
		{
			stringBuilder.append( getEmail() );
		}
		if ( !getEmailMae().isEmpty() )
		{
			if ( stringBuilder.length() == 0 )
			{
				stringBuilder.append( getEmailMae() );
			}
			else
			{
				stringBuilder.append( ">; \"" );
				stringBuilder.append( getNomeMae() );
				stringBuilder.append( "\" <" );
				stringBuilder.append( getEmailMae() );
				stringBuilder.append( ">" );
			}
		}
		if ( !getEmailPai().isEmpty() )
		{
			if ( stringBuilder.length() == 0 )
			{
				stringBuilder.append( getEmailPai() );
			}
			else
			{
				stringBuilder.append( "; \"" );
				stringBuilder.append( getNomePai() );
				stringBuilder.append( "\" <" );
				stringBuilder.append( getEmailPai() );
				stringBuilder.append( ">" );
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * @return the naturalidade
	 */
	public String getNaturalidade()
	{
		return ( String ) getAtributo( "naturalidade" );
	}

	/**
	 * @return the profissao
	 */
	public String getProfissao()
	{
		return ( String ) getAtributo( "profissao" );
	}

	/**
	 * @return the nomePai
	 */
	public String getNomeEncEdu()
	{
		return ( String ) getAtributo( "nomeencedu" );
	}

	/**
	 * @return the telefonePai
	 */
	public String getTelefoneEncEdu()
	{
		return ( String ) getAtributo( "telefoneencedu" );
	}

	/**
	 * @return the emailPai
	 */
	public String getEmailEncEdu()
	{
		return ( String ) getAtributo( "emailencedu" );
	}

	/**
	 * @return the emailPai
	 */
	public String getProfissaoEncEdu()
	{
		return ( String ) getAtributo( "profissaoencedu" );
	}

	/**
	 * @return the nomePai
	 */
	public String getNomePai()
	{
		return ( String ) getAtributo( "nomepai" );
	}

	/**
	 * @return the telefonePai
	 */
	public String getTelefonePai()
	{
		return ( String ) getAtributo( "telefonepai" );
	}

	/**
	 * @return the emailPai
	 */
	public String getEmailPai()
	{
		return ( String ) getAtributo( "emailpai" );
	}

	/**
	 * @return the emailPai
	 */
	public String getProfissaoPai()
	{
		return ( String ) getAtributo( "profissaoPai" );
	}

	/**
	 * @return the emailPai
	 */
	public String getEnderecoWWW()
	{
		return ( String ) getAtributo( "enderecowww" );
	}

	/**
	 * @return the emailPai
	 */
	public String getNotas()
	{
		return ( String ) getAtributo( "notas" );
	}

	/**
	 * @return the emailPai
	 */
	public String getObservacoes()
	{
		return ( String ) getAtributo( "observacoes" );
	}

	/**
	 * @return the emailPai
	 */
	public String getTotem()
	{
		return ( String ) getAtributo( "totem" );
	}

	/**
	 * @return the nomeMae
	 */
	public String getNomeMae()
	{
		return ( String ) getAtributo( "nomemae" );
	}

	/**
	 * @return the telefoneMae
	 */
	public String getTelefoneMae()
	{
		return getAtributo( "telefonemae" ).toString();
	}

	/**
	 * @return the emailMae
	 */
	public String getEmailMae()
	{
		return ( String ) getAtributo( "emailmae" );
	}

	/**
	 * @return the emailMae
	 */
	public String getProfissaoMae()
	{
		return ( String ) getAtributo( "profissaomae" );
	}

	/**
	 * @return the emailMae
	 */
	public Date getDataPromessa()
	{
		final Object dataNascimento = getAtributo( "dtpromessa" );
		return getDate( dataNascimento );
	}

	/**
	 * @return the emailMae
	 */
	public Date getDataAdmissao()
	{
		final Object object = getAtributo( ElementoTags.DATA_ADMISSAO.getTagId() );
		return getDate( object );
	}

	/**
	 * @return the nif
	 */
	public String getNif()
	{
		return getAtributo( "nif" ).toString();
	}

	/**
	 * @return the nin
	 */
	public String getNin()
	{
		String string = getAtributo( "nin" ).toString();
		if ( !string.isEmpty() )
		{
			string = StringUtils.leftPad( string, 13, "0" );
		}
		return string;
	}

	/**
	 * @return the listaAtributos
	 */
	public HashMap< String, Object > getListaAtributos()
	{
		return listaAtributos;
	}

	/**
	 * @return the listaAtributos
	 */
	public Object getAtributo( String tag )
	{
		final Object object = listaAtributos.get( tag );
		if ( object == null )
		{
			return "";
		}

		if ( ElementoTags.NIN.getTagId().equalsIgnoreCase( tag ) )
		{
			final String strNin = object.toString();

			return StringUtils.leftPad( strNin, 13, "0" );
		}
		else
		{
			return object;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Elemento [listaAtributos=" + listaAtributos + "]";
	}

	/**
	 * The <b>getDate</b> method returns {@link Date}
	 *
	 * @author anco62000465 2018-09-27
	 * @param objectDate
	 * @return
	 */
	private Date getDate( Object objectDate )
	{
		if ( objectDate instanceof String )
		{
			final String strData = ( String ) objectDate;
			if ( strData.isEmpty() )
			{
				return null;
			}
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
			try
			{
				return simpleDateFormat.parse( ( String ) objectDate );
			}
			catch ( final ParseException e )
			{
				getLogger().error( e.getMessage() );
			}
			simpleDateFormat = new SimpleDateFormat( "dd-MM-yyyy" );
			try
			{
				return simpleDateFormat.parse( ( String ) objectDate );
			}
			catch ( final ParseException e )
			{
				getLogger().error( e.getMessage() );
			}
		}
		else if ( objectDate instanceof Date )
		{
			return ( Date ) objectDate;
		}
		return null;
	}

	public List< InternetAddress > getListEmails( boolean bUseParentsEmails )
	{
		final List< InternetAddress > list = new ArrayList< >();
		list.add( getInternetAddressEmail() );
		if ( bUseParentsEmails )
		{
			InternetAddress internetAddressEmailMae = getInternetAddressEmailMae();
			if(internetAddressEmailMae != null)
			{
				list.add( internetAddressEmailMae );
			}
			InternetAddress internetAddressEmailPai = getInternetAddressEmailPai();
			if(internetAddressEmailPai != null)
			{
				list.add( internetAddressEmailPai );
			}
		}
		return list;
	}

	/**
	 *
	 * @return
	 */
	public InternetAddress getInternetAddressEmail()
	{
		final String email = getEmail();
		if ( ( email != null ) && email.contains( "@" ) )
		{
			try
			{
				return new InternetAddress( email, getNome() );
			}
			catch ( final UnsupportedEncodingException e )
			{
				getLogger().error( e.getMessage() );
			}
		}
		return null;
	}

	/**
	 *
	 * @return
	 */
	public InternetAddress getInternetAddressEmailPai()
	{
		if ( ( getEmailPai() != null ) && getEmailPai().contains( "@" ) )
		{
			try
			{
				return new InternetAddress( getEmailPai(), getNomePai().toString() );
			}
			catch ( final UnsupportedEncodingException e )
			{
				getLogger().error( e.getMessage() );
			}
		}
		return null;
	}

	/**
	 *
	 * @return
	 */
	public InternetAddress getInternetAddressEmailMae()
	{
		if ( ( getEmailMae() != null ) && getEmailMae().contains( "@" ) )
		{
			try
			{
				return new InternetAddress( getEmailMae(), getNomeMae().toString() );
			}
			catch ( final UnsupportedEncodingException e )
			{
				getLogger().error( e.getMessage() );
			}
		}
		return null;
	}

	@Override
	public int compareTo( Elemento o )
	{
		return getNome().compareTo( o.getNome() );
	}

	public Map< String, String > getPDFSIIEMap()
	{
		final Map< String, String > map = new HashMap<>();
		map.put( "AGRNUCREGSC", getAgrupamento() );
		map.put( "NIN", StringUtils.leftPad( getNin(), 17, "-" ) );
		LocalDateTime dateTime = LocalDateTime.ofInstant( getDataAdmissao().toInstant(), ZoneId.systemDefault() );
		map.put( "DATA ADMISSÃO", StringUtils.leftPad( dateTime.format( DateTimeFormatter.ofPattern( "d" ) ), 2, "0" ) );
		map.put( "undefined", StringUtils.leftPad( dateTime.format( DateTimeFormatter.ofPattern( "M" ) ), 2, "0" ) );
		map.put( "undefined_2", dateTime.format( DateTimeFormatter.ofPattern( "y" ) ) );
		map.put( "CATEGORIA", getCategoria().getNome() );

		if ( getNome().length() <= 30 )
		{
			map.put( "NOME COMPLETO", getNome() );
		}
		else
		{
			map.put( "NOME COMPLETO", getNome().substring( 0, 30 ) );
			map.put( "SEXO", getNome().substring( 30 ) );
		}

		if ( isMasculino() )
		{
			map.put( "Masculino", "X" );
			map.put( "Feminino", " " );
		}
		else
		{
			map.put( "Masculino", " " );
			map.put( "Feminino", "X" );
		}

		map.put( "NIF", StringUtils.leftPad( getNif(), 14, "-" ) );

		dateTime = LocalDateTime.ofInstant( getDataNascimento().toInstant(), ZoneId.systemDefault() );
		map.put( "DATA NASCIMENTO", StringUtils.leftPad( dateTime.format( DateTimeFormatter.ofPattern( "d" ) ), 2, "0" ) );
		map.put( "undefined_3", StringUtils.leftPad( dateTime.format( DateTimeFormatter.ofPattern( "M" ) ), 2, "0" ) );
		map.put( "undefined_4", dateTime.format( DateTimeFormatter.ofPattern( "y" ) ) );

		map.put( "NATURALIDADE", getNaturalidade() );
		map.put( "MORADA", getMorada() );
		map.put( "LOCALIDADE", getLocalidade() );
		map.put( "CODIGO POSTAL", StringUtils.trim( getAtributo( "cp4" ).toString() ) );
		map.put( "undefined_5", StringUtils.trim( getAtributo( "cp3" ).toString() ) );
		map.put( "CONCELHO", "" );
		map.put( "DISTRITO", "" );
		if ( StringUtils.isNotBlank( getTelemovel() ) )
		{
			map.put( "TELEMÓVEL", StringUtils.leftPad( getTelemovel(), 13, "-" ) );
		}
		if ( StringUtils.isNotBlank( getTelefone() ) )
		{
			map.put( "TELEFONE", StringUtils.leftPad( getTelefone(), 13, "-" ) );
		}
		map.put( "CORREIO ELETRÓNICO", getEmail() );
		map.put( "HABILITAÇÕES", "" );
		map.put( "PROFISSÃO", getProfissao() );
		// Pai
		map.put( "PAI", getNomePai() );
		map.put( "PROFISSÃO_2", getProfissaoPai() );
		map.put( "TELEMÓVEL_2", getTelefonePai() );
		map.put( "CORREIO ELETRÓNICO_2", getEmailPai() );
		// Mae
		map.put( "MÃE", getNomeMae() );
		map.put( "PROFISSÃO_3", getProfissaoMae() );
		map.put( "TELEMÓVEL_3", getTelefoneMae() );
		map.put( "CORREIO ELETRÓNICO_3", getEmailMae() );
		// Enc Edu
		map.put( "ENC EDUCAÇÃO", getNomeEncEdu() );
		map.put( "PROFISSÃO_4", getProfissaoEncEdu() );
		map.put( "TELEMÓVEL_4", getTelefoneEncEdu() );
		map.put( "CORREIO ELETRÓNICO_4", getEmailEncEdu() );

		return map;
	}

	public void addEmailSeccao( List< InternetAddress > lstEmails )
	{
		if(!getAgrupamento().isEmpty() && ( getCategoria() != SECCAO.NONE ))
		{
			String email = "";
			String nome = "Agrupamento " + getAgrupamento() + " - " + getCategoria().getNome();
			switch ( getCategoria() )
			{
				case LOBITOS:
					email = "lobitos." + getAgrupamento();
					break;
				case EXPLORADORES:
					email = "exploradores." + getAgrupamento();
					break;
				case PIONEIROS:
					email = "pioneiros." + getAgrupamento();
					break;
				case CAMINHEIROS:
					email = "caminheiros." + getAgrupamento();
					break;
				case DIRIGENTES:
					email = "geral." + getAgrupamento();
					break;
				default:
					return;
			}
			try
			{
				email = email + "@escutismo.pt";
				lstEmails.add( new InternetAddress( email, nome ) );
			}
			catch ( UnsupportedEncodingException e )
			{
				getLogger().error( e.getMessage() );
			}
		}
	}
}

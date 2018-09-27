package scouts.cne.pt.model;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import javax.mail.internet.InternetAddress;

import org.apache.commons.lang3.StringUtils;

import scouts.cne.pt.app.HasLogger;

/**
 * Created by Andr� on 03/10/2015.
 */
public class Elemento implements Comparable< Elemento >, HasLogger
{
	/**
	 *
	 */
	private static final long	serialVersionUID	= 5041677499210798668L;
	private final HashMap< String, Object >	listaAtributos;

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
	public String getNome()
	{
		return getAtributo( "nome" ).toString();
	}

	/**
	 * @return the nome
	 */
	public String getNomeProprio()
	{
		LinkedList< String > l = new LinkedList<>();
		for ( String string : getAtributo( "nome" ).toString().split( " " ) )
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
		LinkedList< String > l = new LinkedList<>();
		for ( String string : getAtributo( "nome" ).toString().split( " " ) )
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
		StringBuilder sb = new StringBuilder();
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
		Object dataNascimento = getAtributo( "dtnasc" );
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
		StringBuilder stringBuilder = new StringBuilder();
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
		Object dataNascimento = getAtributo( "dtpromessa" );
		return getDate( dataNascimento );
	}

	/**
	 * @return the emailMae
	 */
	public Date getDataAdmissao()
	{
		Object object = getAtributo( ElementoTags.DATA_ADMISSAO.getTagId() );
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
		String string = getAtributo("nin").toString();
		if (!string.isEmpty()) {
			string = StringUtils.leftPad(string, 13, "0");
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
		Object object = listaAtributos.get( tag );
		if ( object == null )
		{
			return "";
		}
		return object;
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
			String strData = ( String ) objectDate;
			if ( strData.isEmpty() )
			{
				return null;
			}
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
			try
			{
				return simpleDateFormat.parse( ( String ) objectDate );
			}
			catch ( ParseException e )
			{
				getLogger().error(e.getMessage());
			}
			simpleDateFormat = new SimpleDateFormat( "dd-MM-yyyy" );
			try
			{
				return simpleDateFormat.parse( ( String ) objectDate );
			}
			catch ( ParseException e )
			{
				getLogger().error(e.getMessage());
			}
		}
		else if ( objectDate instanceof Date )
		{
			return ( Date ) objectDate;
		}
		return null;
	}

	/**
	 *
	 * @return
	 */
	public InternetAddress getInternetAddressEmail()
	{
		if ( ( getEmail() != null ) && getEmail().contains( "@" ) )
		{
			try
			{
				return new InternetAddress( getEmail(), getNome() );
			}
			catch ( UnsupportedEncodingException e )
			{
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
			catch ( UnsupportedEncodingException e )
			{
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
			catch ( UnsupportedEncodingException e )
			{
			}
		}
		return null;
	}

	@Override
	public int compareTo( Elemento o )
	{
		return getNome().compareTo( o.getNome() );
	}
}

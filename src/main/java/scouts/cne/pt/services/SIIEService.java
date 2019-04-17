package scouts.cne.pt.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.vaadin.flow.spring.annotation.UIScope;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.google.GoogleServerAuthenticationBean;
import scouts.cne.pt.model.Elemento;
import scouts.cne.pt.model.SECCAO;
import scouts.cne.pt.model.SIIIEImporterException;
import scouts.cne.pt.model.siie.CookieRestTemplate;
import scouts.cne.pt.model.siie.SIIEElementos;
import scouts.cne.pt.model.siie.SIIEUserLogin;
import scouts.cne.pt.model.siie.SIIEUserTokenRequest;
import scouts.cne.pt.utils.ValidationUtils;

@UIScope
@Service
public class SIIEService implements Serializable, HasLogger
{
	/**
	 *
	 */
	private static final long					serialVersionUID	= 1L;
	private File								file;
	private HashMap< String, Elemento >			map					= null;
	private EnumMap< SECCAO, List< Elemento > >	mapSeccaoElemento	= null;
	private String								strAcessToken;
	private String								strXSIIE;
	private List< String >						lstOriginalCookies;
	private final CookieRestTemplate			restTemplate;
	@Autowired
	private GoogleServerAuthenticationBean		googleServerAuthentication;

	public SIIEService()
	{
		super();
		mapSeccaoElemento = new EnumMap<>( SECCAO.class );
		for ( final SECCAO seccao : SECCAO.getListaSeccoes() )
		{
			mapSeccaoElemento.put( seccao, new ArrayList<>() );
		}
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		Proxy proxy = new Proxy( Type.HTTP, new InetSocketAddress( "localhost", 808 ) );
		requestFactory.setProxy( proxy );
		restTemplate = new CookieRestTemplate( requestFactory );
	}

	/**
	 * Getter for logged
	 * 
	 * @author 62000465 2019-02-28
	 * @return the logged {@link boolean}
	 */
	public boolean isLogged()
	{
		return StringUtils.isNotBlank( strAcessToken );
	}

	public boolean authenticateSIIE( String strUsername, String strPassword )
	{
		try
		{

			URI uriLogin = new URI( "https://siie.escutismo.pt/api/logintoken" );

			SIIEUserTokenRequest tokenRequest = new SIIEUserTokenRequest();
			tokenRequest.setUsername( strUsername );
			tokenRequest.setPassword( strPassword );
			ResponseEntity< SIIEUserLogin > postForEntity =
							restTemplate.exchange( uriLogin, HttpMethod.POST, new HttpEntity<>( tokenRequest ), SIIEUserLogin.class );
			if ( postForEntity.getStatusCode() == HttpStatus.OK && postForEntity.hasBody() )
			{
				strAcessToken = postForEntity.getBody().getAcessToken();
				List< String > orDefault = postForEntity.getHeaders().getOrDefault( "xSIIE", Arrays.asList() );
				if ( !orDefault.isEmpty() )
				{
					strXSIIE = orDefault.get( 0 );
					lstOriginalCookies = postForEntity.getHeaders().get( HttpHeaders.SET_COOKIE );
					getLogger().info( "User {} login sucess!", strUsername );
					getElementosSIIE();
					return true;
				}
			}
		}
		catch ( final Exception e )
		{
			showError( e );
		}
		getLogger().info( "User {} error login!", strUsername );

		return false;
	}

	public void getElementosSIIE()
	{
		try
		{

			URI uri = new URI( "https://siie.escutismo.pt/elementos/list?xml=elementos/elementos/dados-completos" );

			HttpHeaders headers = new HttpHeaders();
			headers.add( "Authorization", "Bearer " + strAcessToken );
			headers.add( "xSIIE", strXSIIE );
			for ( String string : lstOriginalCookies )
			{
				headers.add( HttpHeaders.COOKIE, string );
			}
			ResponseEntity< String > forEntity = restTemplate.exchange( uri, HttpMethod.GET, new HttpEntity( null, headers ), String.class );
			if ( forEntity.getStatusCode() == HttpStatus.OK && forEntity.hasBody() )
			{
				String strWSApi = StringUtils.substringBetween( forEntity.getBody(), "wsapi: \"", "\"," );
				URI uriElementos = new URI( "https://siie.escutismo.pt" + strWSApi +
					"&%7B%22take%22%3A2%2C%22skip%22%3A0%2C%22page%22%3A1%2C%22pageSize%22%3A12%2C%22sort%22%3A%5B%5D%7D" );
				restTemplate.setAcessToken( strAcessToken );
				restTemplate.setCookies( lstOriginalCookies );
				ResponseEntity< SIIEElementos > elementosFor = restTemplate.getForEntity( uriElementos, SIIEElementos.class );
				showInfo( elementosFor.getStatusCode().getReasonPhrase() );
			}
			else
			{
				showError( "Erro a obter dados" );
			}
		}
		catch ( final Exception e )
		{
			showError( e );
		}
	}

	/**
	 * @param file the file to set
	 */
	public void setFile( File file )
	{
		this.file = file;
	}

	public void loadElementosGDrive( String id ) throws SIIIEImporterException
	{
		map = new HashMap<>();
		for ( final Entry< SECCAO, List< Elemento > > entry : mapSeccaoElemento.entrySet() )
		{
			entry.getValue().clear();
		}
		try
		{
			/**
			 * Spreadsheet ID /spreadsheets/d/([a-zA-Z0-9-_]+)
			 */
			String spreadsheetId = "";
			Pattern p = Pattern.compile( "/spreadsheets/d/([a-zA-Z0-9-_]+)" );
			Matcher m = p.matcher( id );
			if ( m.find() )
			{
				spreadsheetId = m.group( 1 );
			}
			/**
			 * Sheet ID [#&]gid=([0-9]+)
			 */
			Integer iSheetid = 0;
			p = Pattern.compile( "[#&]gid=([0-9]+)" );
			m = p.matcher( id );
			if ( m.find() )
			{
				iSheetid = Integer.parseInt( m.group( 1 ) );
			}
			if ( StringUtils.isBlank( spreadsheetId ) || iSheetid < 1 )
			{
				throw new SIIIEImporterException(
								"Por favor confirme se o link inserido é semelhante a https://docs.google.com/spreadsheets/d/spreadsheetId/edit#gid=sheetId" );
			}
			googleServerAuthentication.getSheetsService();
			final Sheets service = googleServerAuthentication.getSheetsService();
			Spreadsheet spreadsheet = null;
			try
			{
				spreadsheet = service.spreadsheets().get( spreadsheetId ).execute();
			}
			catch ( final Exception e )
			{
				throw new SIIIEImporterException(
								"Por favor confirme se o email gmail-server@siie-importer-server.iam.gserviceaccount.com tem autorização para ler o ficheiro" );
			}
			String strSheetName = null;
			for ( final Sheet sheet : spreadsheet.getSheets() )
			{
				getLogger().info( "Check sheetId: {} == {}", sheet.getProperties().getSheetId(), iSheetid );
				if ( sheet.getProperties().getSheetId().equals( iSheetid ) )
				{
					strSheetName = sheet.getProperties().getTitle();
					break;
				}
			}
			if ( strSheetName != null )
			{
				getLogger().info( "Start reading sheet {} on spreadsheet {}", strSheetName, spreadsheetId );
				final ValueRange response = service.spreadsheets().values().get( spreadsheetId, strSheetName ).execute();
				final List< List< Object > > values = response.getValues();
				final HashMap< Integer, String > headerRow = new HashMap<>();
				final Iterator< List< Object > > rowIterator = values.iterator();
				// read header
				final List< Object > rowHeader = rowIterator.next();
				for ( int i = 0; i < rowHeader.size(); i++ )
				{
					String value = Objects.toString( rowHeader.get( i ), "" );
					value = value.replace( " ", "" );
					value = value.replace( "-", "" );
					value = value.replace( ".", "" );
					value = value.toLowerCase();
					value = ValidationUtils.removeAcentos( value );
					if ( headerRow.containsValue( value ) )
					{
						if ( !headerRow.containsValue( value + "pai" ) )
						{
							headerRow.put( i, value + "pai" );
						}
						else if ( !headerRow.containsValue( value + "mae" ) )
						{
							headerRow.put( i, value + "mae" );
						}
						else
						{
							headerRow.put( i, value + "encedu" );
						}
					}
					else
					{
						headerRow.put( i, value );
					}
				}
				while ( rowIterator.hasNext() )
				{
					final List< Object > row = rowIterator.next();
					final Elemento elemento = new Elemento();
					for ( int i = 0; i < row.size(); i++ )
					{
						elemento.getListaAtributos().put( headerRow.get( i ), Objects.toString( row.get( i ), "" ) );
					}
					if ( elemento.isActivo() )
					{
						map.put( elemento.getNin(), elemento );
						mapSeccaoElemento.get( elemento.getCategoria() ).add( elemento );
					}
				}
				getLogger().info( "Readed {} contacts.", map.size() );
			}
			else
			{
				throw new SIIIEImporterException( "Folha não encontrada. " );
			}
		}
		catch ( GeneralSecurityException | IOException e )
		{
			showError( e );
		}
	}

	public void loadExploradoresSIIE() throws Exception
	{
		map = new HashMap<>();
		for ( final Entry< SECCAO, List< Elemento > > entry : mapSeccaoElemento.entrySet() )
		{
			entry.getValue().clear();
		}
		// ClassLoader classLoader = getClass().getClassLoader();
		try ( FileInputStream fis = new FileInputStream( file ); XSSFWorkbook myWorkBook = new XSSFWorkbook( fis ) )
		{
			// Return first sheet from the XLSX workbook
			final XSSFSheet mySheet = myWorkBook.getSheetAt( 0 );
			// Get iterator to all the rows in current sheet
			final Iterator< Row > rowIterator = mySheet.iterator();
			// Traversing over each row of XLSX file
			final HashMap< Integer, String > headerRow = new HashMap<>();
			Row row = rowIterator.next();
			Iterator< Cell > cellIterator = row.cellIterator();
			while ( cellIterator.hasNext() )
			{
				final Cell cell = cellIterator.next();
				String value = cell.getStringCellValue();
				value = value.replace( " ", "" );
				value = value.replace( "-", "" );
				value = value.replace( ".", "" );
				value = value.toLowerCase();
				value = ValidationUtils.removeAcentos( value );
				if ( headerRow.containsValue( value ) )
				{
					if ( !headerRow.containsValue( value + "pai" ) )
					{
						headerRow.put( cell.getColumnIndex(), value + "pai" );
					}
					else if ( !headerRow.containsValue( value + "mae" ) )
					{
						headerRow.put( cell.getColumnIndex(), value + "mae" );
					}
					else
					{
						headerRow.put( cell.getColumnIndex(), value + "encedu" );
					}
				}
				else
				{
					headerRow.put( cell.getColumnIndex(), value );
				}
			}
			while ( rowIterator.hasNext() )
			{
				row = rowIterator.next();
				final Elemento elemento = new Elemento();
				cellIterator = row.cellIterator();
				while ( cellIterator.hasNext() )
				{
					final Cell cell = cellIterator.next();
					switch ( cell.getCellType() )
					{
						case Cell.CELL_TYPE_BLANK:
							elemento.getListaAtributos().put( headerRow.get( cell.getColumnIndex() ), null );
							break;
						case Cell.CELL_TYPE_STRING:
						case Cell.CELL_TYPE_FORMULA:
							elemento.getListaAtributos().put(	headerRow.get( cell.getColumnIndex() ),
																StringUtils.trimToEmpty( cell.getStringCellValue() ) );
							break;
						case Cell.CELL_TYPE_BOOLEAN:
							elemento.getListaAtributos().put( headerRow.get( cell.getColumnIndex() ), cell.getBooleanCellValue() );
							break;
						case Cell.CELL_TYPE_NUMERIC:
							elemento.getListaAtributos().put( headerRow.get( cell.getColumnIndex() ), cell.getDateCellValue() );
							break;
						default:
							elemento.getListaAtributos().put( headerRow.get( cell.getColumnIndex() ), null );
							break;
					}
				}
				if ( elemento.isActivo() )
				{
					map.put( elemento.getNin(), elemento );
					mapSeccaoElemento.get( elemento.getCategoria() ).add( elemento );
				}
			}
		}
		catch ( final Exception e )
		{
			throw e;
		}
	}

	/**
	 * Getter for mapSeccaoElemento
	 *
	 * @author anco62000465 2018-01-27
	 * @return the mapSeccaoElemento {@link EnumMap<SECCAO,List<Explorador>>}
	 */
	public EnumMap< SECCAO, List< Elemento > > getMapSeccaoElemento()
	{
		return mapSeccaoElemento;
	}
}

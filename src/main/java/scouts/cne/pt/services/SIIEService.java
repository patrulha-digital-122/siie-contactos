package scouts.cne.pt.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.google.GoogleServerAuthenticationBean;
import scouts.cne.pt.model.Elemento;
import scouts.cne.pt.model.SECCAO;
import scouts.cne.pt.model.SIIIEImporterException;
import scouts.cne.pt.model.siie.CookieRestTemplate;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.model.siie.SIIEElementos;
import scouts.cne.pt.model.siie.authentication.SIIESessionData;
import scouts.cne.pt.model.siie.authentication.SIIEUserLogin;
import scouts.cne.pt.model.siie.authentication.SIIEUserTokenRequest;
import scouts.cne.pt.model.siie.types.SIIEOptions;
import scouts.cne.pt.model.siie.types.SIIESeccao;
import scouts.cne.pt.model.siie.types.SIIESituacao;
import scouts.cne.pt.ui.events.google.FinishSIIEUpdate;
import scouts.cne.pt.utils.Broadcaster;
import scouts.cne.pt.utils.ValidationUtils;

@Component
@VaadinSessionScope
public class SIIEService implements Serializable, HasLogger
{
	/**
	 *
	 */
	private static final long						serialVersionUID	= 1L;
	private File									file;
	private final SIIEElementos						eSiieElementos		= new SIIEElementos();
	private HashMap< String, Elemento >				map					= null;
	private EnumMap< SECCAO, List< Elemento > >		mapSeccaoElemento	= null;
	private EnumMap< SECCAO, List< SIIEElemento > >	mapSeccaoElementos	= new EnumMap<>( SECCAO.class );
	private SIIESessionData							siieSessionData;
	private CookieRestTemplate						restTemplate;
	@Autowired
	private GoogleServerAuthenticationBean			googleServerAuthentication;
	private String									strUserNIN;
	private Instant									lastUpdateInstant;

	public SIIEService()
	{
		super();
		getLogger().info( "New SIIEService :: " + Instant.now() );
		mapSeccaoElemento = new EnumMap<>( SECCAO.class );
	}

	/**
	 * Getter for lastLogin
	 * 
	 * @author 62000465 2019-10-04
	 * @return the lastLogin {@link Instant}
	 */
	public Instant getLastLogin()
	{
		return siieSessionData != null ? siieSessionData.getInstant() : null;
	}

	/**
	 * Getter for lastUpdateInstant
	 * 
	 * @author 62000465 2019-12-05
	 * @return the lastUpdateInstant {@link Instant}
	 */
	public Instant getLastUpdateInstant()
	{
		return lastUpdateInstant;
	}

	/**
	 * Getter for siieElementos
	 * 
	 * @author 62000465 2019-04-24
	 * @return the siieElementos {@link SIIEElementos}
	 */
	public SIIEElementos getSiieElementos()
	{
		return eSiieElementos;
	}

	public boolean isAuthenticated()
	{
		return siieSessionData != null;
	}

	/**
	 * Getter for userNIN
	 * 
	 * @author 62000465 2019-12-05
	 * @return the userNIN {@link String}
	 */
	public String getUserNIN()
	{
		return strUserNIN;
	}

	public void authenticateSIIE( String strUsername, String strPassword ) throws URISyntaxException, RestClientException
	{
		strUserNIN = strUsername;
		eSiieElementos.getData().clear();
		siieSessionData = null;
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		if ( StringUtils.equals( "Y", System.getenv().get( "USE_RPOXY" ) ) )
		{
			Proxy proxy = new Proxy( Type.HTTP, new InetSocketAddress( "localhost", 808 ) );
			requestFactory.setProxy( proxy );
		}
		restTemplate = new CookieRestTemplate( requestFactory );
		URI uriLogin = new URI( "https://siie.escutismo.pt/api/logintoken" );
		SIIEUserTokenRequest tokenRequest = new SIIEUserTokenRequest();
		tokenRequest.setUsername( strUserNIN );
		tokenRequest.setPassword( strPassword );
		ResponseEntity< SIIEUserLogin > postForEntity =
						restTemplate.exchange( uriLogin, HttpMethod.POST, new HttpEntity<>( tokenRequest ), SIIEUserLogin.class );
		if ( postForEntity.getStatusCode() == HttpStatus.OK && postForEntity.hasBody() )
		{
			List< String > orDefault = postForEntity.getHeaders().getOrDefault( "xSIIE", Arrays.asList() );
			if ( !orDefault.isEmpty() )
			{
				getLogger().info( "Login correcto de {}", strUsername );
				siieSessionData = new SIIESessionData( Instant.now() );
				siieSessionData.setAcessToken( postForEntity.getBody().getAcessToken() );
				siieSessionData.setOriginalXSIIE( orDefault.get( 0 ) );
				siieSessionData.setOriginalCookies( postForEntity.getHeaders().get( HttpHeaders.SET_COOKIE ) );
			}
		}
		else
		{
			throw new RestClientException( postForEntity.getStatusCode().getReasonPhrase() );
		}
	}

	public void updateFullSIIE() throws SIIIEImporterException
	{

		FinishSIIEUpdate finishSIIEUpdate = new FinishSIIEUpdate();
		eSiieElementos.getData().clear();
		if ( !isAuthenticated() )
		{
			finishSIIEUpdate.setDescription( strUserNIN + " não está autenticado" );
			Broadcaster.broadcast( finishSIIEUpdate );
			return;
		}

		try
		{
			updateDadosSIIE( SIIEOptions.DADOS_COMPLETOS );
			updateDadosSIIE( SIIEOptions.DADOS_SAUDE );
			lastUpdateInstant = Instant.now();
			Broadcaster.broadcast( finishSIIEUpdate );
		}
		catch ( Exception e )
		{
			printError( e );
			throw new SIIIEImporterException( "Erro a importar todos os dados do SIIE" );
		}
	}

	private void updateDadosSIIE( SIIEOptions siieOptions ) throws RestClientException, URISyntaxException, UnsupportedEncodingException
	{
		restTemplate.setAcessToken( "" );
		restTemplate.setCookies( new ArrayList<>() );
		ResponseEntity< String > forEntity =
						restTemplate.exchange(	new URI( siieOptions.getUrl() ),
												HttpMethod.GET,
												new HttpEntity<>( null, siieSessionData.getHeaders() ),
												String.class );
		if ( forEntity.getStatusCode() == HttpStatus.OK && forEntity.hasBody() )
		{
			int iPage = 0;
			int iSkip = 0;
			int iTake = 100;
			Long iCount = null;
			while ( iCount == null || ( iCount > 0 && eSiieElementos.getData().size() < iCount ) )
			{
				iSkip = iPage * iTake;
				iPage++;
				String encodeToString =
								"{\"take\":" + iTake + ",\"skip\":" + iSkip + ",\"page\":" + iPage + ",\"pageSize\":" + iTake + ",\"sort\":[]}";
				String strWSApi = StringUtils.substringBetween( forEntity.getBody(), "wsapi: \"", "\"," );
				URI uriElementos;
				uriElementos = new URI( "https://siie.escutismo.pt" + strWSApi + "&" + URLEncoder.encode( encodeToString, "UTF-8" ) );
				restTemplate.setAcessToken( siieSessionData.getAcessToken() );
				restTemplate.setCookies( siieSessionData.getOriginalCookies() );
				ResponseEntity< SIIEElementos > elementosFor = restTemplate.getForEntity( uriElementos, SIIEElementos.class );

				if ( iCount == null )
				{
					iCount = elementosFor.getBody().getCount();
				}

				mergeSIIElementos( elementosFor.getBody() );

				getLogger().info( siieOptions.getName() + " :: " + eSiieElementos.getData().size() + "/" + iCount );
			}
		}
	}

	private void mergeSIIElementos( SIIEElementos siieElementos )
	{
		if ( eSiieElementos.getData().isEmpty() )
		{
			eSiieElementos.getData().addAll( siieElementos.getData() );
		}
		else
		{
			for ( SIIEElemento e : siieElementos.getData() )
			{
				int indexOf = eSiieElementos.getData().indexOf( e );
				if ( indexOf > -1 )
				{
					eSiieElementos.getData().get( indexOf ).merge( e );
				}
				else
				{
					eSiieElementos.getData().add( e );
				}
			}
		}
		Collections.sort( eSiieElementos.getData() );
	}

	public List< SIIEElemento > getElementosActivos()
	{
		return eSiieElementos.getData().stream().filter( p -> p.getSiglasituacao().equals( SIIESituacao.A ) ).collect( Collectors.toList() );
	}

	public List< SIIEElemento > getElementosActivosBySeccao( SIIESeccao siieSeccao )
	{
		return eSiieElementos.getData().stream().filter( ( p ) ->
		{
			return p.getSiglasituacao().equals( SIIESituacao.A ) && p.getSiglaseccao().equals( siieSeccao );
		} ).collect( Collectors.toList() );
	}

	public List< SIIEElemento > getAllElementos()
	{
		return new ArrayList<>( eSiieElementos.getData() );
	}

	public Optional< SIIEElemento > getElementoByNIN( String strNIN )
	{
		return eSiieElementos.getData().stream().filter( p -> StringUtils.equals( p.getNin(), strNIN ) ).findFirst();
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
						case BLANK:
							elemento.getListaAtributos().put( headerRow.get( cell.getColumnIndex() ), null );
							break;
						case STRING:
						case FORMULA:
							elemento.getListaAtributos().put(	headerRow.get( cell.getColumnIndex() ),
																StringUtils.trimToEmpty( cell.getStringCellValue() ) );
							break;
						case BOOLEAN:
							elemento.getListaAtributos().put( headerRow.get( cell.getColumnIndex() ), cell.getBooleanCellValue() );
							break;
						case NUMERIC:
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

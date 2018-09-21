package scouts.cne.pt.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
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
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.google.GoogleServerAuthenticationBean;
import scouts.cne.pt.model.Elemento;
import scouts.cne.pt.model.SECCAO;
import scouts.cne.pt.utils.ValidationUtils;

@SpringComponent
@UIScope
public class SIIEService implements Serializable, HasLogger
{
	/**
	 *
	 */
	private static final long					serialVersionUID	= 1L;
	private File								file;
	private HashMap< String, Elemento >			map					= null;
	private EnumMap< SECCAO, List< Elemento > >	mapSeccaoElemento	= null;
	@Autowired
	private GoogleServerAuthenticationBean		googleServerAuthentication;

	public SIIEService()
	{
		super();
		mapSeccaoElemento = new EnumMap<>( SECCAO.class );
		for ( SECCAO seccao : SECCAO.getListaSeccoes() )
		{
			mapSeccaoElemento.put( seccao, new ArrayList<>() );
		}
	}

	/**
	 * @param file the file to set
	 */
	public void setFile( File file )
	{
		this.file = file;
	}

	public void loadElementosGDrive( String id )
	{
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

			if ( StringUtils.isNotBlank( spreadsheetId ) )
			{
				googleServerAuthentication.getSheetsService();
				Sheets service = googleServerAuthentication.getSheetsService();
				Spreadsheet spreadsheet = service.spreadsheets().get( spreadsheetId ).execute();
				String strSheetName = null;
				for ( Sheet sheet : spreadsheet.getSheets() )
				{
					if ( sheet.getProperties().getSheetId() == iSheetid )
					{
						strSheetName = sheet.getProperties().getTitle();
						break;
					}
				}
				if ( strSheetName != null )
				{
					getLogger().info( "Start reading sheet {} on spreadsheet {}", strSheetName, spreadsheetId );
					ValueRange response = service.spreadsheets().values().get( spreadsheetId, strSheetName ).execute();
					List< List< Object > > values = response.getValues();
					HashMap< Integer, String > headerRow = new HashMap<>();
					Iterator< List< Object > > rowIterator = values.iterator();
					// read header
					List< Object > rowHeader = rowIterator.next();
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
						List< Object > row = rowIterator.next();
						Elemento elemento = new Elemento();
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

			}
		}
		catch ( GeneralSecurityException | IOException e )
		{
			e.printStackTrace();
		}
	}

	public void loadExploradoresSIIE() throws Exception
	{
		map = new HashMap<>();
		for ( Entry< SECCAO, List< Elemento > > entry : mapSeccaoElemento.entrySet() )
		{
			entry.getValue().clear();
		}
		// ClassLoader classLoader = getClass().getClassLoader();
		try ( FileInputStream fis = new FileInputStream( file ); XSSFWorkbook myWorkBook = new XSSFWorkbook( fis ) )
		{
			// Return first sheet from the XLSX workbook
			XSSFSheet mySheet = myWorkBook.getSheetAt( 0 );
			// Get iterator to all the rows in current sheet
			Iterator< Row > rowIterator = mySheet.iterator();
			// Traversing over each row of XLSX file
			HashMap< Integer, String > headerRow = new HashMap<>();
			Row row = rowIterator.next();
			Iterator< Cell > cellIterator = row.cellIterator();
			while ( cellIterator.hasNext() )
			{
				Cell cell = cellIterator.next();
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
				Elemento elemento = new Elemento();
				cellIterator = row.cellIterator();
				while ( cellIterator.hasNext() )
				{
					Cell cell = cellIterator.next();
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
		catch ( Exception e )
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

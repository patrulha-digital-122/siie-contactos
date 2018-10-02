package scouts.cne.pt.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.Elemento;

public class PDFUtils implements HasLogger
{

	private static Logger logger = LoggerFactory.getLogger( PDFUtils.class );

	public static File getFichaInscricaoList( Collection< Elemento > elemento ) throws IOException
	{
		final File file = File.createTempFile( "18-8-CN_List", ".pdf" );

		final PDFMergerUtility mergerUtility = new PDFMergerUtility();
		mergerUtility.setDestinationStream( new FileOutputStream( file ) );
		elemento.forEach( p ->
		{
			try
			{
				mergerUtility.addSource( getFichaInscricao( p ) );
			}
			catch ( final Exception e )
			{
				logger.error( e.getMessage(), e );
			}
		} );

		mergerUtility.mergeDocuments( MemoryUsageSetting.setupTempFileOnly() );

		return file;
	}

	public static File getFichaInscricaoZip( Collection< Elemento > elemento ) throws IOException
	{
		final File zipFile = File.createTempFile( "18-8-CN_List", ".zip" );
		zipFile.deleteOnExit();
		try ( ZipOutputStream zipOutputStream = new ZipOutputStream( new FileOutputStream( zipFile ) ) )
		{
			elemento.forEach( p ->
			{
				try
				{
					final File file = getFichaInscricao( p );
					final FileInputStream fis = new FileInputStream( file );
					final ZipEntry zipEntry = new ZipEntry( getElementoMAFSIIENome( p ) + ".pdf" );
					zipOutputStream.putNextEntry( zipEntry );

					final byte[] bytes = new byte[ 1024 ];
					int length;
					while ( ( length = fis.read( bytes ) ) >= 0 )
					{
						zipOutputStream.write( bytes, 0, length );
					}

					zipOutputStream.closeEntry();
					fis.close();
				}
				catch ( final Exception e )
				{
					logger.error( e.getMessage(), e );
				}
			} );
		}

		return zipFile;
	}

	public static String getElementoMAFSIIENome( Elemento elemento )
	{
		final StringBuilder sb = new StringBuilder();

		sb.append( elemento.getNomeProprio() );
		sb.append( " " );
		sb.append( elemento.getNomeApelido() );
		sb.append( "_" );
		sb.append( elemento.getNin() );

		return sb.toString();
	}

	public static File getFichaInscricao( Elemento elemento ) throws IOException
	{
		final File file = File.createTempFile( "18-8-CN_" + elemento.getNin(), ".pdf" );
		file.deleteOnExit();
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try ( InputStream inputStream = classLoader.getResourceAsStream( "18-8-CN.pdf" ); PDDocument pdfDocument = PDDocument.load( inputStream ); )
		{
			// get the document catalog
			final PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();
			acroForm.setNeedAppearances( true );
			final PDResources resources = new PDResources();
			resources.put( COSName.getPDFName( "Cour" ), PDType1Font.COURIER );
			acroForm.setDefaultResources( resources );

			for ( final Entry< String, String > elementoEntry : elemento.getPDFSIIEMap().entrySet() )
			{
				final String key = elementoEntry.getKey();
				final String strValue = elementoEntry.getValue();

				final PDField field = acroForm.getField( key );
				field.setValue( strValue );
				if ( Arrays.asList( "NIN", "AGRNUCREGSC", "CATEGORIA", "DATA ADMISSÃO", "undefined", "undefined_2" ).contains( key ) )
				{
					field.setReadOnly( true );
				}
			}

			pdfDocument.save( file );
		}
		return file;
	}
}

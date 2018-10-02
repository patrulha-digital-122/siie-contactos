package scouts.cne.pt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map.Entry;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import scouts.cne.pt.model.Elemento;
import scouts.cne.pt.model.SECCAO;
import scouts.cne.pt.services.SIIEService;
import scouts.cne.pt.utils.PDFUtils;

public class PDFTest
{
	public static void main( String[] args )
	{
		try
		{
			final File pdfFolder = new File( "C:\\Users\\62000465\\Downloads\\PDF\\" );
			final File file = new File( pdfFolder, "18-8-CN.pdf" );
			final File siiefile = new File( "C:\\Users\\62000465\\Downloads\\SIIE.xlsx" );

			try ( PDDocument pdfDocument = PDDocument.load( file ); )
			{
				// editPDF( pdfFolder, pdfDocument );
				// createPDFs( siiefile, pdfFolder, pdfDocument );
				testPDFUtils( pdfFolder, siiefile );
			}
			catch ( final Exception e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		catch ( final Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void testPDFUtils( final File pdfFolder, final File siiefile ) throws Exception, IOException, FileNotFoundException
	{
		final SIIEService siieService = new SIIEService();
		siieService.setFile( siiefile );
		siieService.loadExploradoresSIIE();

		final File fichaInscricao = PDFUtils.getFichaInscricao( siieService.getMapSeccaoElemento().get( SECCAO.LOBITOS ).get( 0 ) );

		final File newFile = new File( pdfFolder, fichaInscricao.getName() );

		IOUtils.copy( new FileInputStream( fichaInscricao ), new FileOutputStream( newFile ) );
	}

	private static void editPDF( final File pdfFolder, PDDocument pdfDocument ) throws IOException, UnsupportedEncodingException
	{
		final PDPage page = pdfDocument.getPage( 1 );
		final PDFStreamParser parser = new PDFStreamParser( page );
		parser.parse();
		final List tokens = parser.getTokens();
		for ( int j = 0; j < tokens.size(); j++ )
		{
			final Object next = tokens.get( j );
			if ( next instanceof Operator )
			{
				final Operator op = ( Operator ) next;
				String pstring = "";
				int prej = 0;
				// Tj and TJ are the two operators that display strings in a PDF
				if ( op.getName().equals( "Tj" ) )
				{
					// Tj takes one operator and that is the string to display so lets update that operator
					final COSString previous = ( COSString ) tokens.get( j - 1 );
					final String string = previous.getString();
					// string = string.replaceFirst(searchString, replacement);
					// System.out.println( string );
					previous.setValue( string.getBytes() );
				}
				else if ( op.getName().equals( "TJ" ) )
				{
					final COSArray previous = ( COSArray ) tokens.get( j - 1 );
					for ( int k = 0; k < previous.size(); k++ )
					{
						final Object arrElement = previous.getObject( k );
						if ( arrElement instanceof COSString )
						{
							final COSString cosString = ( COSString ) arrElement;
							final String string = cosString.getString();
							if ( j == prej )
							{
								pstring += string;
							}
							else
							{
								prej = j;
								pstring = string;
							}
						}
					}
					// System.out.println( "###\n" + pstring.trim() + "\n###" );
					if ( "pelo CNE".equals( pstring.trim() ) )
					{
						final COSString cosString2 = ( COSString ) previous.getObject( 0 );
						cosString2.setValue( "pelo CNE, bem como a administração de paracetamol (em caso de pequenas dores/febres durante as actividades)"
										.getBytes( "ISO-8859-1" ) );
						final int total = previous.size() - 1;
						for ( int k = total; k > 0; k-- )
						{
							previous.remove( k );
						}
					}
				}
			}
		}
		// pdfDocument.save( file );
		// now that the tokens are updated we will replace the page content stream.
		final PDStream updatedStream = new PDStream( pdfDocument );
		final OutputStream out = updatedStream.createOutputStream( COSName.FLATE_DECODE );
		final ContentStreamWriter tokenWriter = new ContentStreamWriter( out );
		tokenWriter.writeTokens( tokens );
		out.close();
		page.setContents( updatedStream );
		pdfDocument.save( new File( pdfFolder, "NEW.pdf" ) );
	}

	private static void createPDFs( final File siiefile, final File pdfFolder, PDDocument pdfDocument ) throws Exception, IOException
	{
		if ( !pdfFolder.exists() )
		{
			pdfFolder.mkdirs();
		}
		final SIIEService siieService = new SIIEService();
		siieService.setFile( siiefile );
		siieService.loadExploradoresSIIE();

		for ( final Entry< SECCAO, List< Elemento > > entry : siieService.getMapSeccaoElemento().entrySet() )
		{
			entry.getKey();
			final List< Elemento > value = entry.getValue();
			for ( final Elemento elemento : value )
			{
				if ( elemento.isActivo() )
				{
					// get the document catalog
					final PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();
					acroForm.setNeedAppearances( true );
					final PDResources resources = new PDResources();
					resources.put( COSName.getPDFName( "Cour" ), PDType1Font.COURIER );
					acroForm.setDefaultResources( resources );

					// acroForm.getField( "AGRNUCREGSC" ).setValue( "0122 - Torres Vedras" );
					for ( final Entry< String, String > elementoEntry : elemento.getPDFSIIEMap().entrySet() )
					{
						final String key = elementoEntry.getKey();
						final String strValue = elementoEntry.getValue();

						final PDField field = acroForm.getField( key );
						field.setValue( strValue );
					}

					final StringBuilder sbFileName = new StringBuilder();
					sbFileName.append( elemento.getCategoria().name() );
					sbFileName.append( "_" );
					sbFileName.append( elemento.getNin() );
					sbFileName.append( "_" );
					sbFileName.append( elemento.getNome() );
					sbFileName.append( ".pdf" );
					pdfDocument.save( new File( pdfFolder, sbFileName.toString() ) );
				}
			}
		}
	}
}

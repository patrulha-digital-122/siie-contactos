package scouts.cne.pt.component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.google.GoogleAuthenticationBean;
import scouts.cne.pt.model.Elemento;
import scouts.cne.pt.utils.PDFUtils;

/**
 * @author anco62000465 2018-09-24
 *
 */
public class AutorizationFilesWindow extends Window implements Serializable, HasLogger
{
	private static final long				serialVersionUID	= 3816724843669785606L;

	private Button							btnPreview;
	private Button							btnEnviarEmail;

	private final VerticalLayout			mainLayout;
	private final List< Elemento >			lstElementos;

	private final GoogleAuthenticationBean	googleAuthentication;
	private final int						iElementCount		= 1;

	/**
	 * constructor
	 * 
	 * @author anco62000465 2018-09-25
	 * @param googleAuthentication
	 */
	public AutorizationFilesWindow( Collection< Elemento > lst, GoogleAuthenticationBean googleAuthentication )
	{
		super();
		lstElementos = new LinkedList<>( lst );
		this.googleAuthentication = googleAuthentication;
		setCaption( "Fazer download da ficha inscrição associado - 18-8-CN - v.1.0-26/09/2018" );
		center();
		setResizable( true );
		setHeight( "600px" );
		setWidth( "1000px" );
		setModal( true );
		mainLayout = new VerticalLayout();
		mainLayout.setSpacing( false );
		mainLayout.setMargin( new MarginInfo( true, true, false, true ) );
		mainLayout.setSizeFull();
		mainLayout.setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );

		final Button btnDownloadAll = new Button( "Fazer download único ficheiro PDF", VaadinIcons.FILE_O );
		btnDownloadAll.setDisableOnClick( true );
		final FileDownloader fileDownloader = new FileDownloader( createAllFile() );
		fileDownloader.extend( btnDownloadAll );
		final Label labelDownloadAll = new Label( "Para fazer o download de um único ficheiro .pdf com as autorizações de " + lstElementos.size() +
			" elementos. Esta operação pode demorar algum tempo." );
		labelDownloadAll.setWidth( "100%" );

		final Button btnZipAll = new Button( "Fazer download ficheiro zip", VaadinIcons.FILE_ZIP );
		btnZipAll.setDisableOnClick( true );
		final FileDownloader zipFileDownloader = new FileDownloader( createZipFile() );
		zipFileDownloader.extend( btnZipAll );
		final Label labelZipAll = new Label( "Para fazer o download de um ficheiro .zip com as autorizações individuais de " + lstElementos.size() +
			" elementos. Esta operação pode demorar algum tempo." );
		labelZipAll.setWidth( "100%" );

		final Button btnSendEmail = new Button( "Enviar por email", VaadinIcons.FILE_ZIP );
		btnSendEmail.setDisableOnClick( true );
		btnSendEmail.setEnabled( false );
		final Label labelSendEmail = new Label( "Para enviar por email as autorizações individuais de " + lstElementos.size() +
			" elementos. As autorizações serão enviadas indidualmente pelo email " + googleAuthentication.getUserEmail() );
		labelSendEmail.setWidth( "100%" );

		mainLayout.addComponents( labelZipAll, btnZipAll, labelDownloadAll, btnDownloadAll );
		setContent( mainLayout );
	}

	private StreamResource createZipFile()
	{
		return new StreamResource( new StreamSource()
		{
			/**
			 *
			 */
			private static final long serialVersionUID = 721545760831041530L;

			@Override
			public InputStream getStream()
			{
				try
				{
					return new FileInputStream( PDFUtils.getFichaInscricaoZip( lstElementos ) );
				}
				catch ( final IOException e )
				{
					getLogger().error( e.getMessage(), e );
				}
				return null;
			};
		}, "18-8-CN-Ficha de recolha de dados do CNE.zip" );
	}

	private StreamResource createAllFile()
	{
		String fileName = "18-8-CN-Ficha de recolha de dados do CNE.pdf";
		if ( lstElementos.size() == 1 )
		{
			fileName = "18-8-CN-" + PDFUtils.getElementoMAFSIIENome( lstElementos.get( 0 ) ) + ".pdf";
		}
		return new StreamResource( new StreamSource()
		{
			/**
			 *
			 */
			private static final long serialVersionUID = 721545760831041530L;

			@Override
			public InputStream getStream()
			{
				try
				{
					return new FileInputStream( PDFUtils.getFichaInscricaoList( lstElementos ) );
				}
				catch ( final IOException e )
				{
					getLogger().error( e.getMessage(), e );
				}
				return null;
			};
		}, fileName );
	}
}

package scouts.cne.pt.layouts;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.FinishedListener;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.listeners.FileUploader;
import scouts.cne.pt.services.SIIEService;
import scouts.cne.pt.utils.HTMLUtils;

/**
 * @author anco62000465 2018-01-27
 *
 */
public class UploadFileLayout extends Panel implements HasLogger, FinishedListener
{
	private static final long serialVersionUID = 5253307196908771291L;
	private final SIIEService				siieService;
	private final EscolherElementosLayout	escolherElementosLayout;
	private FileUploader					fileUploader;

	/**
	 * constructor
	 *
	 * @author anco62000465 2018-01-27
	 * @param siieService
	 */
	public UploadFileLayout( SIIEService siieService, EscolherElementosLayout escolherElementosLayout )
	{
		super( "Primeiro Passo - Fazer upload do ficheiro do SIIE" );
		setSizeFull();
		this.siieService = siieService;
		this.escolherElementosLayout = escolherElementosLayout;
		this.fileUploader = new FileUploader( siieService );
		Upload upload = new Upload( HTMLUtils.strHTMLHelpFicheiroSIIE, fileUploader );
		upload.setCaptionAsHtml( true );
		upload.setButtonCaption( "Upload" );
		upload.setImmediateMode( true );
		upload.addStartedListener( fileUploader );
		upload.addFailedListener( fileUploader );
		upload.addFinishedListener( this );
		upload.setReceiver( fileUploader );
		HorizontalLayout horizontalLayout = new HorizontalLayout( upload );
		horizontalLayout.setSizeFull();
		horizontalLayout.setComponentAlignment( upload, Alignment.MIDDLE_CENTER );
		horizontalLayout.setDefaultComponentAlignment( Alignment.TOP_CENTER );
		setContent( horizontalLayout );
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.ui.Upload.FinishedListener#uploadFinished(com.vaadin.ui.Upload.FinishedEvent)
	 */
	@Override
	public void uploadFinished( FinishedEvent event )
	{
		try
		{
			// upload.setReceiver( fileUploader );
			siieService.setFile( fileUploader.getFile() );
			siieService.loadExploradoresSIIE();
			escolherElementosLayout.prencherTabela( siieService );
		}
		catch ( Exception e )
		{
			showError( e );
		}
	}
}

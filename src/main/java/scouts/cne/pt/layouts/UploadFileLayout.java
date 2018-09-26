package scouts.cne.pt.layouts;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.FinishedListener;
import com.vaadin.ui.VerticalLayout;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.listeners.FileUploader;
import scouts.cne.pt.model.SIIIEImporterException;
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
		super( "Primeiro Passo - Fazer upload do ficheiro do SIIE (utilizando um ficheiro do computador ou um ficheiro do google drive)" );
		setSizeFull();
		this.siieService = siieService;
		this.escolherElementosLayout = escolherElementosLayout;
		this.fileUploader = new FileUploader( siieService );

		VerticalLayout uploadFromGoogleDriveLayout = getUploadFromGoogleDriveLayout();
		VerticalLayout uploadFromLocalFileLayout = getUploadFromLocalFileLayout();

		HorizontalSplitPanel horizontalLayout = new HorizontalSplitPanel( uploadFromLocalFileLayout, uploadFromGoogleDriveLayout );
		horizontalLayout.setWidth( "100%" );
		horizontalLayout.setHeight( "100%" );

		VerticalLayout mainLayout = new VerticalLayout( horizontalLayout );
		mainLayout.setSizeFull();
		mainLayout.setMargin( false );
		setContent( mainLayout );
	}

	private VerticalLayout getUploadFromLocalFileLayout() {

		Label label = new Label( HTMLUtils.strHTMLHelpFicheiroSIIE, ContentMode.HTML );
		label.setWidth( "100%" );

		Upload upload = new Upload();
		upload.setReceiver( fileUploader );
		upload.setButtonCaption( "Upload" );
		upload.setImmediateMode( true );
		upload.addStartedListener( fileUploader );
		upload.addFailedListener( fileUploader );
		upload.addFinishedListener( this );
		upload.setReceiver( fileUploader );

		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setWidth( "100%" );
		verticalLayout.setSpacing( false );
		verticalLayout.setMargin( new MarginInfo( false, true ) );
		verticalLayout.setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );
		verticalLayout.addComponents( label, upload );

		return verticalLayout;
	}
	/**
	 * The <b>getUploadFromGoogleDriveLayout</b> method returns {@link VerticalLayout}
	 *
	 * @author anco62000465 2018-09-21
	 * @return
	 */
	private VerticalLayout getUploadFromGoogleDriveLayout()
	{

		TextField textField = new TextField( "Colocar link para ficheiro do google drive" );
		textField.setWidth( "100%" );
		Button button = new Button( "Upload", VaadinIcons.UPLOAD );
		button.setResponsive( true );
		Label labelAjuda = new Label();
		labelAjuda.setWidth( "100%" );
		labelAjuda.setVisible( false );
		button.addClickListener( new ClickListener()
		{
			private static final long serialVersionUID = -1136938770611062489L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				try
				{
					siieService.loadElementosGDrive( textField.getValue() );
					escolherElementosLayout.refreshGrids();
					//labelAjuda.setValue( "No futuro poderá utilizar este url: https://cnhefe122.herokuapp.com/?" + MyUI.parameterSHEET_ID + "=" + textField.getValue() );
				}
				catch ( SIIIEImporterException e )
				{
					showError( e );
					labelAjuda.setValue( e.getMessage() );
				}
				labelAjuda.setVisible( true );
			}
		} );

		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setWidth( "100%" );
		verticalLayout.setMargin( new MarginInfo( false, true ) );
		verticalLayout.setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );
		verticalLayout.addComponent( textField );
		verticalLayout.addComponent( labelAjuda );
		verticalLayout.addComponent( button );
		return verticalLayout;
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
			escolherElementosLayout.refreshGrids();
		}
		catch ( Exception e )
		{
			showError( e );
		}
	}
}

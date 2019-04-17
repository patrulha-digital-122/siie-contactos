package scouts.cne.pt.layouts;

import java.net.URLEncoder;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.FinishedListener;
import scouts.cne.pt.MainView;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.listeners.FileUploader;
import scouts.cne.pt.services.SIIEService;
import scouts.cne.pt.utils.HTMLUtils;

/**
 * @author anco62000465 2018-01-27
 *
 */
public class UploadFileLayout extends VerticalLayout implements HasLogger, FinishedListener
{
	private static final long				serialVersionUID	= 5253307196908771291L;
	private final SIIEService				siieService;
	private final EscolherElementosLayout	escolherElementosLayout;
	private final FileUploader				fileUploader;

	/**
	 * constructor
	 *
	 * @author anco62000465 2018-01-27
	 * @param siieService
	 */
	public UploadFileLayout( SIIEService siieService, EscolherElementosLayout escolherElementosLayout )
	{
		super();
		setSizeFull();
		// "Primeiro Passo - Fazer upload do ficheiro do SIIE (utilizando um ficheiro do computador ou um ficheiro do
		// google drive)"
		this.siieService = siieService;
		this.escolherElementosLayout = escolherElementosLayout;
		fileUploader = new FileUploader( siieService );

		final VerticalLayout uploadFromGoogleDriveLayout = getUploadFromGoogleDriveLayout();
		final VerticalLayout uploadFromLocalFileLayout = getUploadFromLocalFileLayout();

		final HorizontalLayout horizontalLayout = new HorizontalLayout( uploadFromLocalFileLayout, uploadFromGoogleDriveLayout );
		horizontalLayout.setWidth( "100%" );
		horizontalLayout.setHeight( "100%" );

		final VerticalLayout mainLayout = new VerticalLayout( horizontalLayout );
		mainLayout.setSizeFull();
		mainLayout.setMargin( false );
		add( mainLayout );
	}

	private VerticalLayout getUploadFromLocalFileLayout()
	{

		final Label label = new Label( HTMLUtils.strHTMLHelpFicheiroSIIE );
		label.setWidth( "100%" );

		final Upload upload = new Upload();
		upload.setReceiver( fileUploader );
		upload.setButtonCaption( "Upload" );
		upload.setImmediateMode( true );
		upload.addStartedListener( fileUploader );
		upload.addFailedListener( fileUploader );
		upload.addFinishedListener( this );
		upload.setReceiver( fileUploader );

		final VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setWidth( "100%" );
		verticalLayout.setSpacing( false );
		verticalLayout.setAlignItems( Alignment.CENTER );
		verticalLayout.add( label );

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

		final TextField textField = new TextField( "Colocar link para ficheiro do google drive" );
		textField.setWidth( "100%" );
		final Button button = new Button( "Upload", VaadinIcon.UPLOAD.create() );
		button.setDisableOnClick( true );
		final Label labelAjuda = new Label();
		labelAjuda.setWidth( "100%" );
		labelAjuda.setVisible( false );
		button.addClickListener( new ComponentEventListener< ClickEvent< Button > >()
		{
			@Override
			public void onComponentEvent( com.vaadin.flow.component.ClickEvent< Button > event )
			{
				try
				{
					siieService.loadElementosGDrive( textField.getValue() );
					escolherElementosLayout.refreshGrids();
					labelAjuda.setText( "No futuro poderá utilizar este url: https://cnhefe122.herokuapp.com/?" + MainView.parameterSHEET_ID + "=" +
						URLEncoder.encode( textField.getValue(), "UTF-8" ) );
				}
				catch ( final Exception e )
				{
					showError( e );
					labelAjuda.setText( e.getMessage() );
				}
				labelAjuda.setVisible( true );
				button.setEnabled( true );
			}
		} );

		final VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setWidth( "100%" );
		verticalLayout.setAlignItems( Alignment.CENTER );
		verticalLayout.add( textField );
		verticalLayout.add( labelAjuda );
		verticalLayout.add( button );
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
		catch ( final Exception e )
		{
			showError( e );
		}
	}
}

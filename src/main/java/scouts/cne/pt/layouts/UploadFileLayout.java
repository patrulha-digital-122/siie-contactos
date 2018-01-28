package scouts.cne.pt.layouts;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import scouts.cne.pt.MyUI;
import scouts.cne.pt.listeners.FileUploader;
import scouts.cne.pt.services.SIIEService;

/**
 * @author anco62000465 2018-01-27
 *
 */
public class UploadFileLayout extends VerticalLayout
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5253307196908771291L;

	/**
	 * constructor
	 * 
	 * @author anco62000465 2018-01-27
	 * @param siieService
	 */
	public UploadFileLayout()
	{
		super();
		setSizeFull();
		setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );
	}

	public Component getLayout( MyUI ui, SIIEService siieService )
	{
		FileUploader fileUploader = new FileUploader( ui, siieService );
		Upload upload = new Upload( "Upload Ficheiro .xlsx do SIIE", fileUploader );
		upload.addSucceededListener( fileUploader );
		upload.setImmediateMode( true );
		
		addComponent( upload );
		return this;
	}
}

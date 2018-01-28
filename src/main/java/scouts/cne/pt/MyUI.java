package scouts.cne.pt;

import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import scouts.cne.pt.google.GoogleAuthenticationBean;
import scouts.cne.pt.layouts.EscolherElementosLayout;
import scouts.cne.pt.layouts.UploadFileLayout;
import scouts.cne.pt.services.SIIEService;

@SpringUI
@Push
public class MyUI extends UI
{
	/**
	 *
	 */
	private static final long			serialVersionUID	= -8505226283440302479L;
	@Autowired
	SIIEService							siieService;
	@Autowired
	private GoogleAuthenticationBean	googleAuthentication;
	
	private EscolherElementosLayout		elementosLayout;

	@Override
	protected void init( VaadinRequest vaadinRequest )
	{
		UploadFileLayout uploadFileLayout = new UploadFileLayout();
		System.out.println( "New Session: " + getEmbedId() );
		setContent( uploadFileLayout.getLayout( this, siieService ) );
		getPage().setTitle( "SIIE - importer" );
		// FirebaseManager.getInstance().addLogMessage("App started");
	}

	public void receiveGoogleCode( String code )
	{
		elementosLayout.setGoogleCode( code );
		push();
	}

	public void showMenus()
	{
		elementosLayout = new EscolherElementosLayout( googleAuthentication );
		setContent( elementosLayout.getLayout( siieService, getEmbedId() ) );
	}
}

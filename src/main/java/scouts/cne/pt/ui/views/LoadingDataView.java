package scouts.cne.pt.ui.views;

import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import scouts.cne.pt.services.SIIEService;
import scouts.cne.pt.ui.components.MasterVerticalLayout;

/**
 * @author anco62000465 2018-05-02
 *
 */

public class LoadingDataView extends MasterVerticalLayout
{
	private static final long	serialVersionUID	= -7307531245923749532L;
	public static final String	VIEW_NAME			= "loading-data";
	public static final String	VIEW_DISPLAY_NAME	= VIEW_NAME + "-title";
	public static final Icon	VIEW_ICON			= VaadinIcon.KEY.create();
	private final SIIEService	siieService;

	@Autowired
	public LoadingDataView( SIIEService siieService )
	{
		this.siieService = siieService;
		H3 h3 = new H3( "Importar dados do SIIE" );
		add( h3 );
		add( getSIIELoginForm() );
		// final UploadFileLayout uploadFileLayout = new UploadFileLayout( siieService );
		// add( uploadFileLayout );
	}

	/**
	 * The <b>getSIIELoginForm</b> method returns {@link Component}
	 * 
	 * @author 62000465 2019-04-24
	 * @return
	 */
	private VerticalLayout getSIIELoginForm()
	{
		VerticalLayout verticalLayout = new MasterVerticalLayout();
		TextField username = new TextField( "NIN" );
		username.setValue( "0512050496002" );
		PasswordField pass = new PasswordField( "Password" );
		pass.setValue( "agr122tvd" );
		Button loginBtn = new Button( "Autenticar" );
		loginBtn.addClickListener( event ->
		{
			if ( siieService.authenticateSIIE( username.getValue(), pass.getValue() ) )
			{
				if ( siieService.getElementosSIIE() )
				{
					showNotification( "Foram obtidos: " + siieService.getSiieElementos().getCount() + " do SIIE", true );
					UI.getCurrent().navigate( EscolherElementosView.VIEW_NAME );
				}
			}
			else
			{
				showNotification( "Login errado", true );
			}
		} );
		add( username, pass, loginBtn );
		return verticalLayout;
	}
}

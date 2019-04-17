package scouts.cne.pt.app.view;

import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;
import scouts.cne.pt.layouts.MasterVerticalView;
import scouts.cne.pt.services.SIIEService;

/**
 * @author anco62000465 2018-05-02
 *
 */
@Tag( value = SIIELoginView.VIEW_NAME )
@Route( value = SIIELoginView.VIEW_NAME )
public class SIIELoginView extends MasterVerticalView implements AfterNavigationObserver, PageConfigurator
{
	private static final long	serialVersionUID	= -7307531245923749532L;
	public static final String	VIEW_NAME			= "siie-login";
	public static final String	VIEW_DISPLAY_NAME	= "menu.masterdata.info";
	public static final Icon	VIEW_ICON			= VaadinIcon.TAGS.create();

	public SIIELoginView( @Autowired SIIEService siieService )
	{
		getLogger().info( "new SIIELoginView" );
		TextField textFieldUserName = new TextField( "Utilizador ", "NIN" );
		textFieldUserName.setValue( "0512050496002" );
		PasswordField textFieldPassword = new PasswordField( "Password" );
		textFieldPassword.setValue( "agr122tvd" );
		Button btn = new Button( "Entrar", VaadinIcon.ENTER.create() );
		btn.addClickListener( e ->
		{
			if ( siieService.authenticateSIIE( textFieldUserName.getValue(), textFieldPassword.getValue() ) )
			{
				setVisible( false );
				UI.getCurrent().navigate( MailingListView.class );
			}
		} );
		add( textFieldUserName, textFieldPassword, btn );
	}

	@Override
	public void configurePage( InitialPageSettings settings )
	{
		// Force login page to use Shady DOM to avoid problems with browsers and
		// password managers not supporting shadow DOM
		settings.addInlineWithContents(	InitialPageSettings.Position.PREPEND,
										"if(window.customElements) {" + "window.customElements.forcePolyfill=true;" +
											"window.ShadyDOM={force:true}};",
										InitialPageSettings.WrapMode.JAVASCRIPT );
	}

	@Override
	public void afterNavigation( AfterNavigationEvent event )
	{
		// boolean error = event.getLocation().getQueryParameters().getParameters().containsKey("error");
		getLogger().error( event.getLocation().getQueryParameters().getParameters().get( "error" ).toString() );
	}
}

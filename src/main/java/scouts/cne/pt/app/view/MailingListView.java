package scouts.cne.pt.app.view;

import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import scouts.cne.pt.MainView;
import scouts.cne.pt.layouts.MasterVerticalView;
import scouts.cne.pt.services.SIIEService;

/**
 * @author anco62000465 2018-05-02
 *
 */
@Route( value = MailingListView.VIEW_NAME, layout = MainView.class )
public class MailingListView extends MasterVerticalView
{
	private static final long			serialVersionUID	= -7307531245923749532L;
	public static final String	VIEW_NAME			= "mailing-list";
	public static final String			VIEW_DISPLAY_NAME	= "menu.masterdata.info";
	public static final Icon			VIEW_ICON			= VaadinIcon.TAGS.create();

	public MailingListView( @Autowired SIIEService siieService )
	{

		Button btn = new Button( "Entrar", VaadinIcon.ENTER.create() );
		btn.addClickListener( e ->
		{
			Notification.show( "Autenticado? " + siieService.isLogged() );
		} );
		add( btn );
	}
}

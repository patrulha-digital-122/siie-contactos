package scouts.cne.pt.ui.views.elementos;

import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.services.GoogleAuthentication;
import scouts.cne.pt.ui.MainLayout;
import scouts.cne.pt.ui.components.navigation.bar.AppBar;
import scouts.cne.pt.ui.views.ViewFrame;
import scouts.cne.pt.ui.views.tab.GoogleGroupsSyncTab;
import scouts.cne.pt.ui.views.tab.GoogleSyncTab;

@Route( value = ImportContactsListView.VIEW_NAME, layout = MainLayout.class )
@PageTitle( ImportContactsListView.VIEW_DISPLAY_NAME )
public class ImportContactsListView extends ViewFrame implements HasLogger
{
	private static final long			serialVersionUID	= 3776271782151856570L;
	public static final String			VIEW_NAME			= "import-contacts";
	public static final String			VIEW_DISPLAY_NAME	= "Importar para Google Contactos";
	public static final VaadinIcon		VIEW_ICON			= VaadinIcon.USERS;
	@Autowired
	private GoogleAuthentication	googleAuthentication;
	private Tab						groupTab;
	private Tab						syncManual;


	@Override
	protected void onAttach( AttachEvent attachEvent )
	{
		super.onAttach( attachEvent );
		initAppBar();
		if ( googleAuthentication.getGoogleAuthInfo() == null )
		{
			groupTab.setEnabled( false );
			syncManual.setEnabled( false );
		}
	}

	private void initAppBar()
	{
		AppBar appBar = MainLayout.get().getAppBar();
		appBar.removeAllActionItems();
		appBar.removeAllTabs();
		syncManual = appBar.addTab( "Sincronização individual", GoogleSyncTab.class );
		groupTab = appBar.addTab( "Grupos", GoogleGroupsSyncTab.class );
		appBar.centerTabs();
	}

}

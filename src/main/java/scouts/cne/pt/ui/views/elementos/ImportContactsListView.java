package scouts.cne.pt.ui.views.elementos;

import java.util.ArrayList;
import java.util.List;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.ui.MainLayout;
import scouts.cne.pt.ui.components.navigation.bar.AppBar;
import scouts.cne.pt.ui.views.ViewFrame;
import scouts.cne.pt.ui.views.tab.GoogleSyncTab;

@Route( value = ImportContactsListView.VIEW_NAME, layout = MainLayout.class )
@PageTitle( ImportContactsListView.VIEW_DISPLAY_NAME )
public class ImportContactsListView extends ViewFrame implements HasLogger
{
	private static final long			serialVersionUID	= 3776271782151856570L;
	public static final String			VIEW_NAME			= "import-contacts";
	public static final String			VIEW_DISPLAY_NAME	= "Importar para Google Contactos";
	public static final VaadinIcon		VIEW_ICON			= VaadinIcon.USERS;

	private final List< SIIEElemento >	lstElementos		= new ArrayList<>();

	private Tab							tabSyncGoogle;

	@Override
	protected void onAttach( AttachEvent attachEvent )
	{
		super.onAttach( attachEvent );
		initAppBar();
	}

	private void initAppBar()
	{
		AppBar appBar = MainLayout.get().getAppBar();
		appBar.removeAllActionItems();
		appBar.removeAllTabs();
		tabSyncGoogle = appBar.addTab( "GoogleSyncTab", GoogleSyncTab.class );
		appBar.centerTabs();
	}

}

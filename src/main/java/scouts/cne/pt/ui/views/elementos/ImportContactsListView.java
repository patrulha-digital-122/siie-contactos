package scouts.cne.pt.ui.views.elementos;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.services.SIIEService;
import scouts.cne.pt.ui.MainLayout;
import scouts.cne.pt.ui.components.grids.ElementosGrid;
import scouts.cne.pt.utils.UIUtils;

@Route( value = ImportContactsListView.VIEW_NAME, layout = MainLayout.class )
@PageTitle( ImportContactsListView.VIEW_DISPLAY_NAME )
public class ImportContactsListView extends VerticalLayout implements HasLogger
{
	private static final long	serialVersionUID	= 3776271782151856570L;
	public static final String			VIEW_NAME			= "import-contacts";
	public static final String			VIEW_DISPLAY_NAME	= "Importar para Google Contactos";
	public static final VaadinIcon		VIEW_ICON			= VaadinIcon.USERS;
	@Autowired
	private SIIEService					siieService;

	private final List< SIIEElemento >	lstServices			= new ArrayList<>();
	private final ElementosGrid			grid;

	public ImportContactsListView()
	{
		setSizeFull();
		setAlignItems( Alignment.STRETCH );
		setSpacing( false );

		grid = new ElementosGrid( true, lstServices );
		grid.setSelectionMode( SelectionMode.MULTI );
		grid.setMinHeight( "50%" );
		
		
		Button btnImport = UIUtils.createPrimaryButton( "Importar para o Google Contacts", VaadinIcon.GOOGLE_PLUS_SQUARE );
		btnImport.setWidthFull();
		add( grid, btnImport );
	}


	@Override
	protected void onAttach( AttachEvent attachEvent )
	{
		super.onAttach( attachEvent );
		lstServices.clear();
		lstServices.addAll( siieService.getAllElementos() );

		grid.getDataProvider().refreshAll();
	}
}

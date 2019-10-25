package scouts.cne.pt.ui.views.elementos;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.google.GoogleAuthenticationBean;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.services.SIIEService;
import scouts.cne.pt.ui.MainLayout;
import scouts.cne.pt.ui.components.GoogleSignin;
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
	@Autowired
	private GoogleAuthenticationBean	googleAuthenticationBean;

	private final List< SIIEElemento >	lstServices			= new ArrayList<>();
	private final ElementosGrid			grid;
	private final Anchor				anchor				= new Anchor( "", "Go!" );
	private final GoogleSignin			signin;

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

		anchor.setTarget( "_self" );
		
		signin = new GoogleSignin( "144161278101-p9n2k3rk1i66jt6d80c9ve0ckdf25a53.apps.googleusercontent.com" );
		signin.setWidth( GoogleSignin.Width.WIDE );
		signin.setBrand( GoogleSignin.Brand.GOOGLEPLUS );
		signin.setHeight( GoogleSignin.Height.STANDARD );
		signin.setTheme( GoogleSignin.Theme.DARK );
		signin.setScopes( StringUtils.join( GoogleAuthenticationBean.SCOPES, " " ) );
		signin.setLabelSignin( "Autorizar edição no Google Contacts" );
		signin.setLabelSignout( "Retiraer autorizaração para edição no Google Contacts" );
		add( grid, signin );
	}


	@Override
	protected void onAttach( AttachEvent attachEvent )
	{
		super.onAttach( attachEvent );
		lstServices.clear();
		lstServices.addAll( siieService.getAllElementos() );

		grid.getDataProvider().refreshAll();
		
		signin.addLoginListener( e ->
		{
			showInfo( "Olá " + e.getGoogleProfile().getNome() );
			googleAuthenticationBean.addSession( e.getGoogleAcessInfo().getAccess_token() );
			signin.setVisible( false );
		} );
		signin.addLogoutListener( () ->
		{
			showInfo( "Adeus" );
			googleAuthenticationBean.addSession( "" );
			signin.setVisible( true );
		} );

	}
}

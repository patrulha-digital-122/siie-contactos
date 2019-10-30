package scouts.cne.pt.ui.views.elementos;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Person;
import com.google.api.services.people.v1.model.UserDefined;
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
import scouts.cne.pt.services.GoogleAuthentication;
import scouts.cne.pt.services.SIIEService;
import scouts.cne.pt.ui.MainLayout;
import scouts.cne.pt.ui.components.GoogleSignin;
import scouts.cne.pt.ui.components.grids.ElementosGrid;
import scouts.cne.pt.utils.UIUtils;

@Route( value = ImportContactsListView.VIEW_NAME, layout = MainLayout.class )
@PageTitle( ImportContactsListView.VIEW_DISPLAY_NAME )
public class ImportContactsListView extends VerticalLayout implements HasLogger
{
	private static final long			serialVersionUID	= 3776271782151856570L;
	public static final String			VIEW_NAME			= "import-contacts";
	public static final String			VIEW_DISPLAY_NAME	= "Importar para Google Contactos";
	public static final VaadinIcon		VIEW_ICON			= VaadinIcon.USERS;
	@Autowired
	private SIIEService					siieService;
	@Autowired
	private GoogleAuthentication		googleAuthentication;
	private final List< SIIEElemento >	lstElementos		= new ArrayList<>();
	private final ElementosGrid			grid;
	private final Anchor				anchor				= new Anchor( "", "Go!" );
	private final GoogleSignin			signin;
	private final Button				btnImport			= UIUtils.createPrimaryButton( "Importar para o Google Contacts", VaadinIcon.FILE_ADD );

	public ImportContactsListView()
	{
		setSizeFull();
		setAlignItems( Alignment.STRETCH );
		setSpacing( false );
		grid = new ElementosGrid( true, lstElementos );
		grid.setSelectionMode( SelectionMode.MULTI );
		grid.setMinHeight( "50%" );

		btnImport.setWidthFull();
		btnImport.addClickListener( e -> importContacts() );
		anchor.setTarget( "_self" );
		signin = new GoogleSignin();
		signin.setWidth( GoogleSignin.Width.WIDE );
		signin.setBrand( GoogleSignin.Brand.GOOGLEPLUS );
		signin.setHeight( GoogleSignin.Height.STANDARD );
		signin.setTheme( GoogleSignin.Theme.DARK );
		signin.setScopes( StringUtils.join( GoogleAuthenticationBean.SCOPES, " " ) );
		signin.setLabelSignin( "Autorizar edição no Google Contacts" );
		signin.setLabelSignout( "Retirar autorizaração para edição no Google Contacts" );
		add( grid, signin, btnImport );
	}

	@Override
	protected void onAttach( AttachEvent attachEvent )
	{
		super.onAttach( attachEvent );
		signin.setClientId( googleAuthentication.getClientId() );
		lstElementos.clear();
		lstElementos.addAll( siieService.getAllElementos() );
		grid.getDataProvider().refreshAll();
		signin.addLoginListener( e ->
		{
			showInfo( "Olá " + e.getGoogleProfile().getNome() );
			getLogger().info( e.getGoogleAcessInfo().getExpires_at().toString() );
			googleAuthentication.setGoogleAuthInfo( e );

			signin.setVisible( false );
		} );
		signin.addLogoutListener( () ->
		{
			showInfo( "Adeus" );
			googleAuthentication.setGoogleAuthInfo( null );
			signin.setVisible( true );
		} );
		grid.addSelectionListener( e -> btnImport.setEnabled( !e.getAllSelectedItems().isEmpty() ) );
	}

	/**
	 * The <b>importContacts</b> method returns {@link Object}
	 * @author 62000465 2019-10-29
	 * @return 
	 */
	private void importContacts()
	{
		// for ( SIIEElemento siieElemento : grid.getSelectedItems() )
		// {
		ListConnectionsResponse execute;
			try
			{
			execute = googleAuthentication.getPeopleService().people().connections().list( "people/me" )
							.setPersonFields( "addresses,ageRanges,biographies,birthdays,braggingRights,coverPhotos,emailAddresses,events,genders,imClients,interests,locales,memberships,metadata,names,nicknames,occupations,organizations,phoneNumbers,photos,relations,relationshipInterests,relationshipStatuses,residences,sipAddresses,skills,taglines,urls,userDefined" )
							.setPageSize( 2000 )
							.execute();
			for ( Person person : execute.getConnections() )
			{
				Optional< UserDefined > findFirst =
								ListUtils.emptyIfNull( person.getUserDefined() ).stream().filter( p -> p.getKey().equals( "NIN" ) ).findFirst();
				if ( findFirst.isPresent() )
				{
					System.out.println( person.getNames().get( 0 ).getDisplayName() + " :: " + findFirst.get().getValue() );
				}
			}
			}
			catch ( IOException | GeneralSecurityException e )
			{
				e.printStackTrace();
			}
		// }
	}

}

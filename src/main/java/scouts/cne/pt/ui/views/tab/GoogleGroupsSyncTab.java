package scouts.cne.pt.ui.views.tab;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.api.services.people.v1.model.ContactGroup;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.siie.types.SIIESeccao;
import scouts.cne.pt.services.GoogleAuthentication;
import scouts.cne.pt.services.GoogleContactGroupsService;
import scouts.cne.pt.ui.MainLayout;
import scouts.cne.pt.ui.components.Badge;
import scouts.cne.pt.ui.components.FlexBoxLayout;
import scouts.cne.pt.ui.components.ListItem;
import scouts.cne.pt.ui.layout.size.Horizontal;
import scouts.cne.pt.ui.layout.size.Vertical;
import scouts.cne.pt.ui.util.BoxShadowBorders;
import scouts.cne.pt.ui.util.LumoStyles;
import scouts.cne.pt.ui.util.css.FlexDirection;
import scouts.cne.pt.utils.UIUtils;

/**
 * @author 62000465 2019-10-30
 *
 */
@Route( value = GoogleGroupsSyncTab.VIEW_NAME, layout = MainLayout.class )
@PageTitle( GoogleGroupsSyncTab.VIEW_TITLE )
public class GoogleGroupsSyncTab extends FlexBoxLayout implements HasLogger
{
	/**
	 * 
	 */
	private static final long			serialVersionUID	= -7135758008679193088L;
	public static final String			VIEW_NAME			= "google-sync-groups-tab";
	public static final String			VIEW_TITLE			= "Google :: Sincronização Grupos";
	@Autowired
	private GoogleAuthentication		googleAuthentication;
	@Autowired
	private GoogleContactGroupsService	googleContactGroupsService;
	private final Div					content				= new Div();

	/**
	 * constructor
	 * 
	 * @author 62000465 2019-10-30
	 * @param components
	 */
	public GoogleGroupsSyncTab()
	{
		super();
		
		setFlexDirection( FlexDirection.COLUMN );
		setMargin( Horizontal.AUTO );
		setPadding( Horizontal.RESPONSIVE_L, Vertical.L );
		setSizeFull();
		
		add( createHearderContent() );
		
		content.setSizeFull();
		content.addClassNames( BoxShadowBorders.BOTTOM, LumoStyles.Padding.Bottom.L );
		
		FlexBoxLayout flexBoxLayout = new FlexBoxLayout( content );
		flexBoxLayout.setFlexDirection( FlexDirection.COLUMN );
		flexBoxLayout.setMargin( Horizontal.AUTO );
		flexBoxLayout.setPadding( Horizontal.RESPONSIVE_L, Vertical.L );
		
		add( content );
	}

	/**
	 * The <b>createHearderContent</b> method returns {@link Component}
	 * 
	 * @author 62000465 2019-11-04
	 * @return
	 */
	private Component createHearderContent()
	{
		TextField textFieldHearder = new TextField();
		textFieldHearder.setWidthFull();
		textFieldHearder.setTitle( "Gestão dos grupos existentes no Google Contacts" );
		textFieldHearder.setValue( "Nesta zona podem confirmar se os grupos que são utilizados por esta plataforma já existem na sua conta do Google contacts, e caso não exista possam ser criados de forma manual." );
		textFieldHearder.setEnabled( false );
		return textFieldHearder;
	}

	private void updateContent()
	{
		content.removeAll();
		for ( SIIESeccao siieSeccao : SIIESeccao.values() )
		{
			Badge lable = siieSeccao.getLabel();
			lable.setWidth( "200px" );
			ListItem listItem;
			ContactGroup contactGroup = googleContactGroupsService.getListGroups().get( siieSeccao );
			if ( contactGroup != null )
			{
				listItem = new ListItem( lable, "Data criação do grupo: " + contactGroup.getMetadata().getUpdateTime().replace( "T", " " ) );
				listItem.setSecondaryText( "Número de contactos associados ao grupo: " + Objects.toString( contactGroup.getMemberCount(), "0" ) );
			}
			else
			{
				listItem = new ListItem( lable, "Grupo '" + siieSeccao.getNome() + "' ainda não foi criado" );
			}
			Button button;
			if ( googleContactGroupsService.getListGroups().get( siieSeccao ) != null )
			{
				button = UIUtils.createErrorPrimaryButton( "Apagar Grupo", VaadinIcon.MINUS );
				button.addClickListener( e ->
				{
					try
					{
						googleContactGroupsService.deleteGroup( googleAuthentication.getPeopleService(), siieSeccao );
						showInfo( "Grupo '" + siieSeccao.getNome() + "' apagado com sucesso" );
						updateContent();
					}
					catch ( Exception e1 )
					{
						showError( "Problema a eliminar grupo. " + e1 );
					}
				} );
			}
			else
			{
				button = UIUtils.createSuccessPrimaryButton( "Criar Grupo", VaadinIcon.PLUS );
				button.addClickListener( e ->
				{
					try
					{
						googleContactGroupsService.createGroup( googleAuthentication.getPeopleService(), siieSeccao );
						showInfo( "Grupo '" + siieSeccao.getNome() + "' criado com sucesso" );
						updateContent();
					}
					catch ( Exception e1 )
					{
						showError( "Problema a criar grupo. " + e1.getMessage() );
					}
				} );
			}
			button.setDisableOnClick( true );
			listItem.setSuffix( button );
			listItem.setDividerVisible( true );
			content.add( listItem );
		}
	}

	@Override
	protected void onAttach( AttachEvent attachEvent )
	{
		super.onAttach( attachEvent );
		updateContent();
	}
}

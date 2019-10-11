package scouts.cne.pt.ui.views;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.services.SIIEService;
import scouts.cne.pt.ui.MainLayout;
import scouts.cne.pt.ui.components.FlexBoxLayout;
import scouts.cne.pt.ui.layout.size.Horizontal;
import scouts.cne.pt.ui.layout.size.Right;
import scouts.cne.pt.ui.layout.size.Uniform;
import scouts.cne.pt.ui.util.css.FlexDirection;
import scouts.cne.pt.ui.util.css.FlexWrap;
import scouts.cne.pt.ui.views.admin.SIIELoginView;
import scouts.cne.pt.utils.UIUtils;

@PageTitle( Home.VIEW_DISPLAY_NAME )
@Route( value = "", layout = MainLayout.class )
public class Home extends ViewFrame implements HasUrlParameter< String >, HasLogger
{
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -3259624123726894321L;
	public static final String	VIEW_NAME			= "home";
	public static final String	VIEW_DISPLAY_NAME	= "Início";
	public static final VaadinIcon	VIEW_ICON			= VaadinIcon.HOME;
	
	@Autowired
	private SIIEService	siieService;
	private final Label		labelLastLogin	= new Label();
	private Button					siieLoginButtom		= UIUtils.createPrimaryButton( "Fazer login no SIIE", SIIELoginView.VIEW_ICON );
	private String					siieUser;
	private String					siiePassword;

	public Home()
	{
		setId( "home" );
		setViewHeader( createHeaderContent() );
		setViewContent( createContent() );
		siieLoginButtom.setWidthFull();
		siieLoginButtom.addClickListener( e ->
		{
			siieLoginButtom.getUI().ifPresent( ui -> ui.navigate( SIIELoginView.VIEW_NAME ) );
		} );
	}

	@Override
	protected void onAttach( AttachEvent attachEvent )
	{
		super.onAttach( attachEvent );
		siieLoginButtom.setVisible( !siieService.isAuthenticated() );
		if ( StringUtils.isNoneEmpty( siieUser, siiePassword ) )
		{
			labelLastLogin.setText( "A carregar dados do SIIE..." );
			siieLoginButtom.setVisible( false );
			new Thread( () ->
			{
				if ( siieService.authenticateSIIE( siieUser, siiePassword ) && siieService.updateDadosCompletosSIIE() )
				{
					attachEvent.getUI().access( () ->
					{
						showInfo( siieUser + " autenticado com sucesso" );
						// getLogger().info( siieService.get );
						labelLastLogin.setText( "Última vez que foram obtidas informações do SIIE :: " +
							UIUtils.formatDateTime( siieService.getLastLogin() ) );
						Optional< SIIEElemento > elementoByNIN = siieService.getElementoByNIN( siieUser );
						if ( elementoByNIN.isPresent() )
						{
							MainLayout.get().getAppBar().getAvatar()
											.setSrc( String.format( UIUtils.SIIE_IMG_PATH, elementoByNIN.get().getUploadgroup(), siieUser ) );
						}
					} );
				}
				else
				{
					siieLoginButtom.setVisible( true );
				}
			} ).start();
		}
		else
		{
			labelLastLogin.setText( "Ainda não foi feito nenhum login para obter as informações do SIIE." );
		}
	}

	private Component createHeaderContent()
	{
		Html features = new Html( "<p>Este site tem o objectivo de ajudar os dirigentes em 'processar' a informação do SIIE.</p>" );
		Anchor starter = new Anchor( "https://siie.escutismo.pt", UIUtils.createButton( "Aceder ao SIIE", VaadinIcon.EXTERNAL_LINK ) );

		FlexBoxLayout links = new FlexBoxLayout( starter );
		links.setFlexWrap( FlexWrap.WRAP );
		links.setSpacing( Right.S );
		FlexBoxLayout content = new FlexBoxLayout( features, links );
		content.setFlexDirection( FlexDirection.COLUMN );
		content.setMargin( Horizontal.AUTO );
		content.setPadding( Uniform.RESPONSIVE_L );
		return content;
	}

	private Component createContent()
	{
		FlexBoxLayout content = new FlexBoxLayout( labelLastLogin, siieLoginButtom );
		content.setAlignItems( Alignment.CENTER );
		content.setFlexDirection( FlexDirection.COLUMN );
		content.setMargin( Horizontal.AUTO );
		content.setPadding( Uniform.RESPONSIVE_L );
		return content;
	}


	@Override
	public void setParameter( BeforeEvent event, @OptionalParameter String parameter )
	{
		Location location = event.getLocation();
		QueryParameters queryParameters = location.getQueryParameters();
		Map< String, List< String > > parametersMap = queryParameters.getParameters();
		siieUser = parametersMap.getOrDefault( "siieUser", Arrays.asList( "" ) ).get( 0 );
		siiePassword = parametersMap.getOrDefault( "siiePassword", Arrays.asList( "" ) ).get( 0 );
	}
}

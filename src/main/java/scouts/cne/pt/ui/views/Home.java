package scouts.cne.pt.ui.views;

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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import scouts.cne.pt.app.HasLogger;
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
public class Home extends HasSIIELoginUrl implements HasLogger
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
		attachEvent.getUI().navigate( VIEW_NAME );
		if ( siieService.isAuthenticated() )
		{
			labelLastLogin.setText( "Última vez que foram obtidas informações do SIIE :: " + UIUtils.formatDateTime( siieService.getLastLogin() ) );
		}
		else
		{
			if ( StringUtils.isNotBlank( getSiieUser() ) )
			{
				labelLastLogin.setText( "A carregar dados do SIIE..." );
				siieLoginButtom.setVisible( false );
				new Thread( () ->
				{
					if ( authenticate( siieService, attachEvent.getUI() ) )
					{
						attachEvent.getUI().access( () ->
						{
							labelLastLogin.setText( "Última vez que foram obtidas informações do SIIE :: " +
								UIUtils.formatDateTime( siieService.getLastLogin() ) );
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
}

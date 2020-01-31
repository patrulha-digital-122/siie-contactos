package scouts.cne.pt.ui.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.ui.MainLayout;
import scouts.cne.pt.ui.util.TextColor;
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
	
	private final Label		labelLastLogin	= new Label();
	private Button					siieLoginButtom		= UIUtils.createPrimaryButton( "Fazer login no SIIE", SIIELoginView.VIEW_ICON );


	public Home()
	{
		setId( "home" );
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
		attachEvent.getUI().navigate( VIEW_NAME );

	}

	private Component createContent()
	{
		VerticalLayout content = new VerticalLayout();
		content.setAlignItems( Alignment.STRETCH );
		content.add( UIUtils
						.createH4Label( "Esta plataforma tem o objectivo de ajudar os dirigentes em 'processar' a informação do SIIE para as situações do dia a dia, como por exemplo obter a lista de email de todos os elementos de uma secção." ) );
		content.add( new Html(
						"<p>Os dados apresentados são obtidos directamente do SIIE, que está disponível neste link <a href=\"https://siie.escutismo.pt\" target=\"_blank\">SIIE</a></p>" ) );
		content.add( UIUtils
						.createH5Label( "Para acedermos aos dados no SIIE é necessário que façam login na nossa plataforma com as credenciais do SIIE. Para tal tens de ir à opção '" +
							SIIELoginView.VIEW_DISPLAY_NAME + "'. Se carregares neste botão serás re-encaminhado directamente para lá." ) );
		content.add( siieLoginButtom );
		Label warnignLabel = UIUtils
						.createH3Label( "Esta plataforma não guardar as credencias utilizadas para o login no SIIE! Apenas as utiliza para obter as informações do SIIE." );
		UIUtils.setTextColor( TextColor.ERROR, warnignLabel );
		content.add( warnignLabel );

		content.add( new Html(
						"<p>Esta plataforma foi criada pela Patrulha Digital do Agrupamento 122 Torres Vedras - Núcleo Oeste / Região de Lisboa.</br>" +
							"Apesar de ser feita da melhor vontade, por vezes acontecem erros e problemas. Nesses casos por favor envia um email para: <b><a href=\"mailto:patrulha.digital.122@escutismo.pt?Subject=[CNhEfe]%20-%20\" target=\"_top\">patrulha.digital.122@escutismo.pt</a></b></br>" +
							"Obviamente que sugestões de melhoria ou novas funcionalidades são muito bem vindas.</p>" ) );
		Label boaCaca = UIUtils.createH4Label( "Boa Caça!" );
		content.add( boaCaca );
		Label boaPesca = UIUtils.createH4Label( "Boa Pesca!" );
		content.add( boaPesca );
		Image image = new Image(
						"https://drive.google.com/uc?export=view&id=17TQrPkjWTWXBPcVBRcznm4kpCRjbrP-T",
						"" );
		image.setWidthFull();
		image.setMaxWidth( "350px" );
		content.add( image );
		content.setAlignSelf( Alignment.CENTER, image );
		content.setAlignSelf( Alignment.END, boaCaca, boaPesca );
		return content;
	}
}

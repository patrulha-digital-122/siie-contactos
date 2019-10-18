package scouts.cne.pt.ui.views.admin;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.services.SIIEService;
import scouts.cne.pt.ui.MainLayout;
import scouts.cne.pt.ui.components.FlexBoxLayout;
import scouts.cne.pt.ui.layout.size.Horizontal;
import scouts.cne.pt.ui.layout.size.Uniform;
import scouts.cne.pt.ui.util.css.FlexDirection;
import scouts.cne.pt.ui.views.ViewFrame;
import scouts.cne.pt.utils.UIUtils;

@PageTitle( SIIELoginView.VIEW_DISPLAY_NAME )
@Route( value = SIIELoginView.VIEW_NAME, layout = MainLayout.class )
public class SIIELoginView extends ViewFrame implements HasLogger
{
	/**
	 * 
	 */
	private static final long		serialVersionUID	= -2277784926398100145L;
	public static final String		VIEW_NAME			= "siieLogin";
	public static final String		VIEW_DISPLAY_NAME	= "SIIE :: Login";
	public static final VaadinIcon	VIEW_ICON			= VaadinIcon.ACADEMY_CAP;
	
	@Autowired
	private SIIEService	siieService;
	private final LoginForm	loginForm;
	private final TextField			labelNextUrlLogin	= new TextField( "URL pré-preenchido que poderá utilizar no futuro." );
	private final TextField			textFieldTotalElementos		= new TextField( "Total de elementos obtidos do SIIE" );
	private final TextField			textFieldDataUltimoUpdate	= new TextField( "Data da última actualização de dados do SIIE" );
	private String					strUrl						= "?user=%s&password=%s";

	public SIIELoginView()
	{
		setId( "home" );
		loginForm = new LoginForm( createPortugueseI18n() );
		loginForm.setForgotPasswordButtonVisible( false );
		setViewContent( createContent() );
		loginForm.addLoginListener( e ->
		{
			try
			{
				siieService.authenticateSIIE( e.getUsername(), e.getPassword() );
				labelNextUrlLogin.setValue( String.format( strUrl, e.getUsername(), e.getPassword() ) );
				labelNextUrlLogin.setVisible( true );
				siieService.updateDadosCompletosSIIE();
				updateSIIEMetaData();
				Optional< SIIEElemento > elementoByNIN = siieService.getElementoByNIN( e.getUsername() );
				if ( elementoByNIN.isPresent() )
				{
					MainLayout.get().getAppBar().getAvatar()
									.setSrc( String.format( UIUtils.SIIE_IMG_PATH, elementoByNIN.get().getUploadgroup(), e.getUsername() ) );
				}
			}
			catch ( Exception ex )
			{
				loginForm.setError( true );
				showError( ex );
				ex.printStackTrace();
			}

			loginForm.setEnabled( !siieService.isAuthenticated() );
		} );

		labelNextUrlLogin.setEnabled( false );
		labelNextUrlLogin.setWidthFull();
		labelNextUrlLogin.setVisible( false );
		textFieldDataUltimoUpdate.setEnabled( false );
		textFieldDataUltimoUpdate.setWidthFull();
		textFieldTotalElementos.setEnabled( false );
		textFieldTotalElementos.setWidthFull();
	}

	@Override
	protected void onAttach( AttachEvent attachEvent )
	{
		super.onAttach( attachEvent );

		strUrl = getLocation() + strUrl;
		updateSIIEMetaData();
	}

	private Component createContent()
	{
		Html features = new Html(
						"<p><b>Este site não guarda nenhuma password.<b/> Apenas utiliza a password do SIIE para obter os dados completos.</p>" );
		FlexBoxLayout content = new FlexBoxLayout( features, loginForm, labelNextUrlLogin, textFieldDataUltimoUpdate, textFieldTotalElementos );
		content.setAlignItems( Alignment.CENTER );
		content.setFlexDirection( FlexDirection.COLUMN );
		content.setMargin( Horizontal.AUTO );
		content.setPadding( Uniform.RESPONSIVE_L );
		return content;
	}

	private LoginI18n createPortugueseI18n()
	{
		final LoginI18n i18n = LoginI18n.createDefault();
		i18n.setHeader( new LoginI18n.Header() );
		i18n.getHeader().setTitle( "Login no SIIE" );
		i18n.getHeader().setDescription( "Descrição do SIIE" );
		i18n.getForm().setUsername( "NIN" );
		i18n.getForm().setTitle( "Login no SIIE" );
		i18n.getForm().setSubmit( "Entrar" );
		i18n.getForm().setPassword( "Senha" );
		i18n.getForm().setForgotPassword( "Esqueci minha senha" );
		i18n.getErrorMessage().setTitle( "Usuário/senha inválidos" );
		i18n.getErrorMessage().setMessage( "Confira seu usuário e senha e tente novamente." );
		i18n.setAdditionalInformation( "Sempre que efectuar um novo login os dados apresentados neste site serãop actualizados com a informação do SIIE" );
		return i18n;
	}


	private String getLocation()
	{
		VaadinServletRequest request = ( VaadinServletRequest ) VaadinService.getCurrentRequest();
		StringBuffer uriString = request.getRequestURL();
		return uriString.toString().replace( VIEW_NAME, "" );
	}

	private void updateSIIEMetaData()
	{
		if ( siieService.isAuthenticated() )
		{
			textFieldDataUltimoUpdate.setValue( UIUtils.formatDateTime( siieService.getLastLogin() ) );
			textFieldTotalElementos.setValue( String.valueOf( siieService.getSiieElementos().getCount() ) );
		}
		else
		{
			textFieldDataUltimoUpdate.setValue( "" );
			textFieldTotalElementos.setValue( "" );
		}
		loginForm.setEnabled( !siieService.isAuthenticated() );
	}
}

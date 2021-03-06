package scouts.cne.pt.ui.views.admin;

import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.shared.Registration;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.localStorage.SIIELocalStorageConfigurations;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.services.LocalStorageService;
import scouts.cne.pt.services.SIIEService;
import scouts.cne.pt.ui.MainLayout;
import scouts.cne.pt.ui.components.FlexBoxLayout;
import scouts.cne.pt.ui.events.google.FinishSIIEUpdate;
import scouts.cne.pt.ui.layout.size.Horizontal;
import scouts.cne.pt.ui.layout.size.Uniform;
import scouts.cne.pt.ui.util.css.FlexDirection;
import scouts.cne.pt.ui.views.ViewFrame;
import scouts.cne.pt.utils.Broadcaster;
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
	@Autowired
	private LocalStorageService		localStorageService;
	private final LoginForm	loginForm;
	private final TextField			textFieldTotalElementos		= new TextField( "Total de elementos obtidos do SIIE" );
	private final TextField			textFieldDataUltimoUpdate	= new TextField( "Data da última actualização de dados do SIIE" );
	private String					strUrl						= "mailing-list?user=%s&password=%s";
	protected Registration			broadcasterRegistration;
	private UI						ui;

	public SIIELoginView()
	{
		setId( "home" );
		loginForm = new LoginForm( createPortugueseI18n() );
		loginForm.setForgotPasswordButtonVisible( false );
		setViewContent( createContent() );

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
		ui = attachEvent.getUI();
		updateSIIEMetaData();

		loginForm.addLoginListener( e ->
		{
			ui.access( () ->
			{
				textFieldDataUltimoUpdate.setValue( "" );
				textFieldTotalElementos.setValue( "" );
			} );
			SIIELocalStorageConfigurations siieLocalStorageConfigurations = new SIIELocalStorageConfigurations();
			try
			{
				if ( !( siieService.isAuthenticated() && StringUtils.equals( e.getUsername(), siieService.getUserNIN() ) ) )
				{
					siieService.authenticateSIIE( e.getUsername(), e.getPassword() );
					siieLocalStorageConfigurations.setSIIEUser( e.getUsername() );
					siieLocalStorageConfigurations.setSIIEPassword( e.getPassword() );
					// labelNextUrlLogin.setVisible( true );
				}
				siieService.updateFullSIIE();
			}
			catch ( Exception ex )
			{
				loginForm.setError( true );
			}
			localStorageService.setSiieLocalStorageConfigurations( siieLocalStorageConfigurations );

			loginForm.setEnabled( true );
		} );

		broadcasterRegistration = Broadcaster.register( newMessage ->
		{
			if ( newMessage instanceof FinishSIIEUpdate )
			{
				FinishSIIEUpdate finishSIIEUpdate = ( FinishSIIEUpdate ) newMessage;
				getLogger().info( "Dados do SIIE actualizados em :: " + finishSIIEUpdate.getDuration().toString() );
				ui.access( () ->
				{
					Optional< SIIEElemento > elementoByNIN = siieService.getElementoByNIN( siieService.getUserNIN() );
					if ( elementoByNIN.isPresent() )
					{
						MainLayout.get().getAppBar().getAvatar().setSrc( String
										.format( UIUtils.SIIE_IMG_PATH, elementoByNIN.get().getUploadgroup(), siieService.getUserNIN() ) );
					}
					updateSIIEMetaData();
				} );
			}
		} );
	}

	private Component createContent()
	{
		Html features = new Html(
						"<p><b>Este site não guarda nenhuma password.<b/> Apenas utiliza a password do SIIE para obter os dados completos.</p>" );
		FlexBoxLayout content = new FlexBoxLayout( features, loginForm, textFieldDataUltimoUpdate, textFieldTotalElementos );
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
		i18n.getErrorMessage().setTitle( "NIN/senha inválida" );
		i18n.getErrorMessage().setMessage( "Confira NIN e senha e tente novamente." );
		i18n.setAdditionalInformation( "Sempre que efectuar um novo login os dados apresentados neste site serão actualizados com a informação do SIIE" );
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
			textFieldDataUltimoUpdate.setValue( UIUtils.formatDateTime( siieService.getLastUpdateInstant() ) );
			textFieldTotalElementos.setValue( String.valueOf( siieService.getSiieElementos().getData().size() ) );
		}
		else
		{
			textFieldDataUltimoUpdate.setValue( "" );
			textFieldTotalElementos.setValue( "" );
		}
	}
}

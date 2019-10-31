package scouts.cne.pt.ui.views.admin;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Person;
import com.google.api.services.people.v1.model.UserDefined;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.google.GoogleAuthenticationBean;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.services.GoogleAuthentication;
import scouts.cne.pt.services.SIIEService;
import scouts.cne.pt.ui.MainLayout;
import scouts.cne.pt.ui.components.FlexBoxLayout;
import scouts.cne.pt.ui.components.GoogleSignin;
import scouts.cne.pt.ui.layout.size.Horizontal;
import scouts.cne.pt.ui.layout.size.Uniform;
import scouts.cne.pt.ui.util.css.FlexDirection;
import scouts.cne.pt.ui.views.ViewFrame;

@PageTitle( GoogleAdminView.VIEW_DISPLAY_NAME )
@Route( value = GoogleAdminView.VIEW_NAME, layout = MainLayout.class )
public class GoogleAdminView extends ViewFrame implements HasLogger
{
	/**
	 * 
	 */
	private static final long		serialVersionUID	= -2277784926398100145L;
	public static final String		VIEW_NAME			= "google-login";
	public static final String		VIEW_DISPLAY_NAME	= "Google :: Login";
	public static final VaadinIcon	VIEW_ICON			= VaadinIcon.ACADEMY_CAP;
	@Autowired
	private SIIEService				siieService;
	@Autowired
	private GoogleAuthentication	googleAuthentication;
	private final GoogleSignin		signin;
	private final Image				image				= new Image();
	private final TextField			nome				= new TextField( "Nome completo" );
	private final TextField			email				= new TextField( "Email" );
	private final Label				progressLabel		= new Label();
	private final ProgressBar		progressBar			= new ProgressBar();

	public GoogleAdminView()
	{
		setId( "home" );
		signin = new GoogleSignin();
		signin.setWidth( GoogleSignin.Width.WIDE );
		signin.setBrand( GoogleSignin.Brand.GOOGLEPLUS );
		signin.setHeight( GoogleSignin.Height.STANDARD );
		signin.setTheme( GoogleSignin.Theme.DARK );
		signin.setScopes( StringUtils.join( GoogleAuthenticationBean.SCOPES, " " ) );
		signin.setLabelSignin( "Autorizar edição no Google Contacts" );
		signin.setLabelSignout( "Retirar autorizaração para edição no Google Contacts" );
		nome.setWidthFull();
		nome.setEnabled( false );
		email.setWidthFull();
		email.setEnabled( false );
		progressBar.setWidthFull();
		progressBar.setMin( 0 );
		progressLabel.setWidthFull();
		setViewContent( createContent() );
	}

	@Override
	protected void onAttach( AttachEvent attachEvent )
	{
		super.onAttach( attachEvent );
		signin.setClientId( googleAuthentication.getClientId() );
		signin.addLoginListener( e ->
		{
			nome.setValue( e.getGoogleProfile().getNome() );
			email.setValue( e.getGoogleProfile().getEmail() );
			image.setSrc( e.getGoogleProfile().getUrlImage() );
			showInfo( "Olá " + e.getGoogleProfile().getNome() );
			googleAuthentication.setGoogleAuthInfo( e );
			signin.setVisible( false );
			new Thread( () -> importContacts( attachEvent.getUI() ) ).start();
		} );
		signin.addLogoutListener( () ->
		{
			showInfo( "Adeus" );
			googleAuthentication.setGoogleAuthInfo( null );
			nome.setValue( "" );
			email.setValue( "" );
			image.setSrc( "" );
			signin.setVisible( true );
		} );

		if ( googleAuthentication.getGoogleAuthInfo() != null )
		{
			signin.setVisible( false );
			nome.setValue( googleAuthentication.getGoogleAuthInfo().getGoogleProfile().getNome() );
			email.setValue( googleAuthentication.getGoogleAuthInfo().getGoogleProfile().getEmail() );
			image.setSrc( googleAuthentication.getGoogleAuthInfo().getGoogleProfile().getUrlImage() );
			new Thread( () -> importContacts( attachEvent.getUI() ) ).start();
		}
	}

	private Component createContent()
	{
		FlexBoxLayout content = new FlexBoxLayout( image, nome, email, progressLabel, progressBar, signin );
		content.setAlignItems( Alignment.CENTER );
		content.setFlexDirection( FlexDirection.COLUMN );
		content.setMargin( Horizontal.AUTO );
		content.setPadding( Uniform.RESPONSIVE_L );
		return content;
	}

	/**
	 * The <b>importContacts</b> method returns {@link Object}
	 * 
	 * @author 62000465 2019-10-29
	 * @param ui
	 * @return
	 */
	private void importContacts( UI ui )
	{
		ui.access( () ->
		{
			progressBar.setValue( 0 );
			progressBar.addThemeVariants( ProgressBarVariant.LUMO_SUCCESS );
			progressLabel.setText( "A receber dados do google" );
		} );
		ListConnectionsResponse execute;
		int iCount = 0;
		Integer totalItems;
		try
		{
			execute = googleAuthentication.getPeopleService().people().connections().list( "people/me" )
							.setPersonFields( GoogleAuthentication.PERSON_FIELDS )
							.setPageSize( 2000 ).execute();
			totalItems = execute.getTotalItems();
			ui.access( () ->
			{
				progressBar.setMax( totalItems );
			} );

			for ( Person person : execute.getConnections() )
			{
				int value = ( int ) ( progressBar.getValue() + 1 );
				if ( value % 10 == 0 )
				{
					ui.access( () ->
					{
						progressLabel.setText( String.format( "A processar dados do google (%d / %d)", value, totalItems ) );
						progressBar.setValue( value );
					} );
				}
				Optional< UserDefined > findFirst =
								ListUtils.emptyIfNull( person.getUserDefined() ).stream().filter( p -> p.getKey().equals( "NIN" ) ).findFirst();
				if ( findFirst.isPresent() )
				{
					Optional< SIIEElemento > siieOptional = siieService.getElementoByNIN( findFirst.get().getValue() );
					if ( siieOptional.isPresent() )
					{
						siieOptional.get().setGooglePerson( person );
						iCount++;
					}
				}
			}
			String s = "Actualização finalizada. Utilizados " + iCount + " contactos";
			ui.access( () ->
			{
				progressLabel.setText( s );
				progressBar.setValue( totalItems );
			} );
		}
		catch ( IOException | GeneralSecurityException e )
		{
			showError( e );
			progressBar.addThemeVariants( ProgressBarVariant.LUMO_ERROR );
		}

	}
}

package scouts.cne.pt.ui.views.admin;

import java.util.Date;
import java.util.Optional;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Person;
import com.google.api.services.people.v1.model.UserDefined;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.google.GoogleAuthenticationBean;
import scouts.cne.pt.model.google.GoogleAuthInfo;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.services.GoogleAuthentication;
import scouts.cne.pt.services.GoogleContactGroupsService;
import scouts.cne.pt.services.GoogleContactsService;
import scouts.cne.pt.services.SIIEService;
import scouts.cne.pt.ui.MainLayout;
import scouts.cne.pt.ui.components.FlexBoxLayout;
import scouts.cne.pt.ui.components.GoogleSignin;
import scouts.cne.pt.ui.components.LocalStorage;
import scouts.cne.pt.ui.events.google.FinishSIIEUpdate;
import scouts.cne.pt.ui.layout.size.Horizontal;
import scouts.cne.pt.ui.layout.size.Uniform;
import scouts.cne.pt.ui.util.css.FlexDirection;
import scouts.cne.pt.ui.views.ViewFrame;
import scouts.cne.pt.utils.Broadcaster;
import scouts.cne.pt.utils.UIUtils;

@PageTitle( GoogleAdminView.VIEW_DISPLAY_NAME )
@Route( value = GoogleAdminView.VIEW_NAME, layout = MainLayout.class )
@PreserveOnRefresh
public class GoogleAdminView extends ViewFrame implements HasLogger
{
	/**
	 * 
	 */
	private static final long			serialVersionUID	= -2277784926398100145L;
	public static final String			VIEW_NAME			= "google-login";
	public static final String			VIEW_DISPLAY_NAME	= "Google :: Login";
	public static final VaadinIcon		VIEW_ICON			= VaadinIcon.ACADEMY_CAP;
	@Autowired
	private SIIEService					siieService;
	@Autowired
	private GoogleAuthentication		googleAuthentication;
	@Autowired
	private GoogleContactGroupsService	googleContactGroupsService;
	@Autowired
	private GoogleContactsService		googleContactsService;
	private final GoogleSignin			googleSignin;
	private final Image					image				= new Image();
	private final TextField				nome				= new TextField( "Nome completo" );
	private final TextField				email				= new TextField( "Email" );
	private final Label					progressLabel		= new Label();
	private final ProgressBar			progressBar			= new ProgressBar( 0, 3, 0 );
	private final Button				logOutButton		=
					UIUtils.createErrorPrimaryButton( "Retirar autorizaração para edição no Google Contacts", VaadinIcon.DEL );
	private final Button				updateButton		= UIUtils.createPrimaryButton( "Sincronizar dados do Google", VaadinIcon.REFRESH );
	private UI							ui;
	private final LocalStorage			localStorage		= new LocalStorage();
	protected Registration				broadcasterRegistration;

	public GoogleAdminView()
	{
		googleSignin = new GoogleSignin();
		googleSignin.setWidth( GoogleSignin.Width.WIDE );
		googleSignin.setBrand( GoogleSignin.Brand.GOOGLEPLUS );
		googleSignin.setHeight( GoogleSignin.Height.STANDARD );
		googleSignin.setTheme( GoogleSignin.Theme.DARK );
		googleSignin.setScopes( StringUtils.join( GoogleAuthenticationBean.SCOPES, " " ) );
		googleSignin.setLabelSignin( "Autorizar edição no Google Contacts" );
		googleSignin.setLabelSignout( "Retirar autorizaração para edição no Google Contacts" );
		googleSignin.setVisible( false );

		nome.setWidthFull();
		nome.setEnabled( false );

		email.setWidthFull();
		email.setEnabled( false );

		progressBar.setWidthFull();
		progressBar.setMin( 0 );

		progressLabel.setWidthFull();

		updateButton.setWidthFull();
		updateButton.setDisableOnClick( true );
		updateButton.addClickListener( e -> importContacts() );
		logOutButton.setWidthFull();
		logOutButton.setDisableOnClick( true );
		logOutButton.addClickListener( e ->
		{
			executeLogOut();
		} );
		logOutButton.setVisible( false );

		setViewContent( createContent() );
	}

	@Override
	protected void onAttach( AttachEvent attachEvent )
	{
		super.onAttach( attachEvent );
		ui = attachEvent.getUI();
		googleSignin.setClientId( googleAuthentication.getClientId() );
		googleSignin.addLoginListener( e ->
		{
			nome.setValue( e.getGoogleProfile().getNome() );
			email.setValue( e.getGoogleProfile().getEmail() );
			image.setSrc( e.getGoogleProfile().getUrlImage() );
			showInfo( "Olá " + e.getGoogleProfile().getNome() );
			googleAuthentication.setGoogleAuthInfo( e );
			googleSignin.setVisible( false );
			logOutButton.setVisible( true );
			importContacts();
			try
			{
				localStorage.setValue( LocalStorage.GOOGLE_AUTH, new ObjectMapper().writeValueAsString( e ) );
			}
			catch ( JsonProcessingException e1 )
			{
				printError( e1 );
			}
		} );
		
		localStorage.addInitListener( ls ->
		{
			String googleObject = localStorage.getString( LocalStorage.GOOGLE_AUTH );
			if ( StringUtils.isNotBlank( googleObject ) )
			{
				try
				{
					GoogleAuthInfo googleAuthInfo = new ObjectMapper().readValue( googleObject, GoogleAuthInfo.class );
					if ( googleAuthInfo.getGoogleAcessInfo().getExpiresAt().after( new Date() ) )
					{
						googleAuthentication.setGoogleAuthInfo( googleAuthInfo );
						updateScreenInfo();
						googleSignin.setVisible( false );
						logOutButton.setVisible( true );
						if ( siieService.isAuthenticated() && !siieService.getSiieElementos().getData().isEmpty() )
						{
							importContacts();
						}
					}
					else
					{
						executeLogOut();
					}
				}
				catch ( Exception e1 )
				{
					printError( e1 );
				}
			}
		} );
		broadcasterRegistration = Broadcaster.register( newMessage ->
		{
			if ( newMessage instanceof FinishSIIEUpdate )
			{
				if ( googleAuthentication.getGoogleAuthInfo() != null )
				{
					importContacts();
				}
			}
		} );
		updateScreenInfo();
	}

	/**
	 * The <b>updateScreenInfo</b> method returns {@link void}
	 * @author 62000465 2019-12-09 
	 */
	private void updateScreenInfo()
	{
		if ( googleAuthentication.getGoogleAuthInfo() != null )
		{
			googleSignin.setVisible( false );
			logOutButton.setVisible( false );
			logOutButton.setEnabled( true );
			nome.setValue( googleAuthentication.getGoogleAuthInfo().getGoogleProfile().getNome() );
			email.setValue( googleAuthentication.getGoogleAuthInfo().getGoogleProfile().getEmail() );
			image.setSrc( googleAuthentication.getGoogleAuthInfo().getGoogleProfile().getUrlImage() );
		}
		else
		{
			nome.setValue( "" );
			email.setValue( "" );
			image.setSrc( "" );
			updateButton.setEnabled( false );
			googleSignin.setVisible( true );
			logOutButton.setVisible( false );
		}
	}

	private Component createContent()
	{
		FlexBoxLayout content =
						new FlexBoxLayout( image, nome, email, progressLabel, progressBar, googleSignin, logOutButton, updateButton, localStorage );
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
	private void importContacts()
	{
		new Thread( () ->
		{
			ui.access( () ->
			{
				progressBar.setValue( 0 );
				progressBar.addThemeVariants( ProgressBarVariant.LUMO_SUCCESS );
				progressLabel.setText( "(1/3) - A receber dados do google" );
			} );
			siieService.getAllElementos().forEach( p -> p.setGooglePerson( null ) );
			ListConnectionsResponse execute;
			int iCount = 0;
			int iPeopleProcessed = 0;
			Integer totalItems;
			StringBuilder sbFinalMessage = new StringBuilder();
			try
			{
				execute = googleAuthentication.getPeopleService().people().connections().list( "people/me" )
								.setPersonFields( GoogleAuthentication.PERSON_FIELDS ).setPageSize( 2000 ).execute();
				totalItems = execute.getTotalItems();
				ui.access( () ->
				{
					progressBar.setValue( 1 );
				} );
				while ( true )
				{
					for ( Person person : execute.getConnections() )
					{
						iPeopleProcessed++;
						Optional< UserDefined > findFirst = ListUtils.emptyIfNull( person.getUserDefined() ).stream()
										.filter( p -> p.getKey().equals( "NIN" ) ).findFirst();
						if ( findFirst.isPresent() )
						{
							Optional< SIIEElemento > siieOptional = siieService.getElementoByNIN( findFirst.get().getValue() );
							if ( siieOptional.isPresent() )
							{
								siieOptional.get().setGooglePerson( person );
								iCount++;
							}
						}
						googleContactsService.updateEmailAndPhoneList( person );
					}
					if ( iPeopleProcessed >= totalItems )
					{
						break;
					}
					else
					{
						execute = googleAuthentication.getPeopleService().people().connections().list( "people/me" )
										.setPersonFields( GoogleAuthentication.PERSON_FIELDS ).setPageToken( execute.getNextPageToken() )
										.setPageSize( 2000 ).execute();
					}
				}
				ui.access( () ->
				{
					progressLabel.setText( "(2/3) - Actualização dos grupos" );
					progressBar.setValue( 2 );
				} );
				googleContactGroupsService.updateAll( googleAuthentication.getPeopleService() );
				sbFinalMessage.append( "(3/3) - Actualização finalizada. Utilizados " + iCount + " contactos" );
			}
			catch ( Exception e )
			{
				sbFinalMessage.append( "(3/3) - Erro na actualização :: " + e.getMessage() );
				progressBar.addThemeVariants( ProgressBarVariant.LUMO_ERROR );
			}
			finally
			{
			ui.access( () ->
			{
				progressLabel.setText( sbFinalMessage.toString() );
				progressBar.setValue( 3 );
				updateButton.setEnabled( true );
			} );
			}
		} ).start();
	}

	/**
	 * The <b>executeLogOut</b> method returns {@link void}
	 * 
	 * @author 62000465 2019-12-09
	 */
	private void executeLogOut()
	{
		localStorage.setValue( LocalStorage.GOOGLE_AUTH, "" );
		googleSignin.logout();
		googleAuthentication.setGoogleAuthInfo( null );
		for ( SIIEElemento siieElemento : siieService.getSiieElementos().getData() )
		{
			siieElemento.setGooglePerson( null );
		}

		progressBar.setValue( 0 );
		progressBar.addThemeVariants( ProgressBarVariant.LUMO_SUCCESS );
		progressLabel.setText( "" );
		updateScreenInfo();
	}
}

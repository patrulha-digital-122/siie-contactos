package scouts.cne.pt.layouts;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.gdata.client.Query;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.batch.BatchOperationType;
import com.google.gdata.data.batch.BatchStatus;
import com.google.gdata.data.batch.BatchUtils;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.gdata.data.contacts.ContactGroupFeed;
import com.google.gdata.data.contacts.GroupMembershipInfo;
import com.google.gdata.data.extensions.ExtendedProperty;
import com.google.gdata.data.extensions.PhoneNumber;
import com.google.gdata.util.ServiceException;
import com.vaadin.annotations.Push;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.event.ContextClickEvent;
import com.vaadin.event.ContextClickEvent.ContextClickListener;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ClassResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import scouts.cne.pt.google.GoogleAuthenticationBean;
import scouts.cne.pt.model.Explorador;
import scouts.cne.pt.model.SECCAO;
import scouts.cne.pt.services.SIIEService;
import scouts.cne.pt.utils.ContactUtils;

/**
 * @author anco62000465 2018-01-27
 *
 */
@Push
public class EscolherElementosLayout extends VerticalLayout
{
	/**
	 * 
	 */
	private static final long					serialVersionUID	= 5253307196908771291L;
	private TabSheet							tabsheetContactos;
	private Map< SECCAO, List< Explorador > >	mapSelecionados;
	private Button								btImportacao;
	private Button								btAuthentication;
	private String								googleCode			= null;
	private GoogleAuthenticationBean			googleAuthentication;
	private GoogleCredential					credential			= null;
	@Value( "classpath:L.jpg" )
	private Resource							resourceLobitos;

	/**
	 * constructor
	 * 
	 * @author anco62000465 2018-01-27
	 * @param siieService
	 */
	public EscolherElementosLayout( GoogleAuthenticationBean googleAuthentication )
	{
		super();
		setSizeFull();
		setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );
		mapSelecionados = new EnumMap<>( SECCAO.class );
		for ( SECCAO component : SECCAO.getListaSeccoes() )
		{
			mapSelecionados.put( component, new ArrayList<>() );
		}
		this.googleAuthentication = googleAuthentication;
	}

	/**
	 * Getter for tabsheetContactos
	 * 
	 * @author anco62000465 2018-01-27
	 * @return the tabsheetContactos {@link TabSheet}
	 */
	public TabSheet getTabsheetContactos()
	{
		return tabsheetContactos;
	}

	public Component getLayout( SIIEService siieService, String embedId )
	{
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setSizeFull();
		verticalLayout.setSpacing( true );
		verticalLayout.setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );
		tabsheetContactos = new TabSheet();
		tabsheetContactos.setWidth( 100f, Unit.PERCENTAGE );
		siieService.loadExploradoresSIIE();
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		for ( SECCAO seccao : SECCAO.getListaSeccoes() )
		{
			// Tab dos Lobitos
			VerticalLayout tabLobitos = new VerticalLayout();
			Grid< Explorador > grid = new Grid<>();
			grid.setSizeFull();
			grid.removeAllColumns();
			grid.setSelectionMode( SelectionMode.MULTI );
			grid.setItems( siieService.getMapSeccaoElemento().get( seccao ) );
			tabLobitos.addComponent( grid );
			grid.addColumn( Explorador::getNome ).setCaption( "Nome" );
			grid.addColumn( Explorador::getNin ).setCaption( "NIN" );
			grid.addColumn( Explorador::getEmail ).setCaption( "Email" );
			grid.addColumn( Explorador::getNif ).setCaption( "NIF" );
			grid.addSelectionListener( new SelectionListener< Explorador >()
			{
				@Override
				public void selectionChange( SelectionEvent< Explorador > event )
				{
					mapSelecionados.get( seccao ).clear();
					mapSelecionados.get( seccao ).addAll( event.getAllSelectedItems() );
					int iSelecionados = 0;
					for ( List< Explorador > list : mapSelecionados.values() )
					{
						iSelecionados += list.size();
					}
					btImportacao.setCaption( "Iniciar Importação (" + iSelecionados + ")" );
					btImportacao.setEnabled( iSelecionados > 0 && !btAuthentication.isVisible() );
				}
			} );
			String nomeTab = seccao.getNome() + " - " + siieService.getMapSeccaoElemento().get( seccao ).size();
			FileResource resource = new FileResource( new File( basepath + "/WEB-INF/images/" + seccao.getId() + ".jpg" ) );
			tabsheetContactos.addTab( tabLobitos, nomeTab );
		}
		// rootLayout.addComponent( debugLabel );
		btAuthentication = new Button( "Dar permissão" );
		//btAuthentication.setVisible( false );
		btAuthentication.setEnabled( true );
		try
		{
			GoogleAuthorizationCodeRequestUrl googleAuthorizationCodeRequestUrl =
							googleAuthentication.getGoogleAuthorizationCodeRequestUrl( embedId );
			BrowserWindowOpener browserWindowOpener = new BrowserWindowOpener( googleAuthorizationCodeRequestUrl.build() );
			browserWindowOpener.setFeatures( "height=600,width=600" );
			browserWindowOpener.extend( btAuthentication );
			btAuthentication.click();
		}
		catch ( GeneralSecurityException | IOException e )
		{
			e.printStackTrace();
		}
		btImportacao = new Button( "Iniciar Importação (0)" );
		btImportacao.setEnabled( false );
		btImportacao.addClickListener( new ClickListener()
		{
			@Override
			public void buttonClick( ClickEvent event )
			{
				importProcess();
			}
		} );
		verticalLayout.addComponent( tabsheetContactos );
		verticalLayout.setExpandRatio( tabsheetContactos, 8 );
		verticalLayout.addComponent( btAuthentication );
		verticalLayout.setExpandRatio( btAuthentication, 1 );
		Label label = new Label( "Configurações avançadas" );
		VerticalLayout opcoesExtraLayout = new VerticalLayout();
		List< String > data = Arrays.asList( "Importar dados dos pais em separado", "Importar dados dos pais em conjunto" );
		CheckBoxGroup< String > chkBImportarPais = new CheckBoxGroup<>( "Opções:", data );
		opcoesExtraLayout.addComponent( chkBImportarPais );

//		verticalLayout.addComponent( label );
//		verticalLayout.addComponent( opcoesExtraLayout );
		//verticalLayout.setExpandRatio( opcoesExtraLayout, 1 );
		verticalLayout.addComponent( btImportacao );
		verticalLayout.setExpandRatio( btImportacao, 1 );
		addComponent( verticalLayout );
		return this;
	}

	/**
	 * The <b>getElementosSelecionados</b> method returns {@link Map<String,Explorador>}
	 * 
	 * @author anco62000465 2018-01-27
	 * @return
	 */
	public Map< String, Explorador > getElementosSelecionados()
	{
		Map< String, Explorador > map = new HashMap<>();
		for ( Entry< SECCAO, List< Explorador > > entry : mapSelecionados.entrySet() )
		{
			List< Explorador > list = entry.getValue();
			for ( Explorador explorador : list )
			{
				map.put( explorador.getNin().trim(), explorador );
			}
		}
		return map;
	}

	/**
	 * Setter for googleCode
	 * 
	 * @author anco62000465 2018-01-28
	 * @param googleCode the googleCode to set
	 */
	public void setGoogleCode( String googleCode )
	{
		this.googleCode = googleCode;
		// this.btImportacao.setEnabled( true );
		this.btAuthentication.setVisible( false );
	}

	public void importProcess()
	{
		try
		{
			Map< String, Explorador > elementosParaImportar = getElementosSelecionados();
			if ( elementosParaImportar.isEmpty() )
			{
				return;
			}
			if ( credential == null )
			{
				credential = googleAuthentication.getGoogleCredentials( googleCode );
			}
			String applicationName = googleAuthentication.getApplicationName();
			ContactsService contactsService = new ContactsService( applicationName );
			contactsService.setOAuth2Credentials( credential );
			URL feedUrl = new URL( "https://www.google.com/m8/feeds/contacts/default/full" );
			ContactFeed resultFeed = contactsService.getFeed( feedUrl, ContactFeed.class );
			// Print the results
			// Add a normal progress bar
			// elementosLayout.addComponent( progressBar );
			// elementosLayout.setComponentAlignment( progressBar, Alignment.MIDDLE_CENTER );
			//showDebugNotification( "Elementos para importar: " + elementosParaImportar.size() );
			Map< String, ContactEntry > elementosExistentes = new HashMap<>();
			List< ContactEntry > listContc = new ArrayList<>( resultFeed.getEntries() );
			while ( resultFeed.getNextLink() != null )
			{
				resultFeed = contactsService.getFeed( new URL( resultFeed.getNextLink().getHref() ), ContactFeed.class );
				listContc.addAll( resultFeed.getEntries() );
			}
			//showDebugNotification( "Resultados recebidos: " + resultFeed.getTotalResults() + " / " + listContc.size() );
			for ( ContactEntry contactEntry : listContc )
			{
				for ( PhoneNumber phoneNumber : contactEntry.getPhoneNumbers() )
				{
					if ( "NIN".equals( phoneNumber.getLabel() ) )
					{
						if ( elementosParaImportar.containsKey( phoneNumber.getPhoneNumber().trim() ) )
						{
							elementosExistentes.put( phoneNumber.getPhoneNumber(), contactEntry );
						}
					}
				}
			}
			Map< SECCAO, ContactGroupEntry > processarGrupo = processarGrupo( contactsService, elementosParaImportar );
			List< ContactFeed > listBatchFeeds = new ArrayList<>();
			ContactFeed batchRequestFeed = new ContactFeed();
			listBatchFeeds.add( batchRequestFeed );
			int i = 0;
			for ( Explorador explorador : elementosParaImportar.values() )
			{
				if ( ++i > 98 )
				{
					batchRequestFeed = new ContactFeed();
					listBatchFeeds.add( batchRequestFeed );
					i = 0;
				}
				ContactEntry contEntry;
				if ( elementosExistentes.containsKey( explorador.getNin() ) )
				{
					contEntry = ContactUtils.convertElementoToContactEntry( explorador, elementosExistentes.get( explorador.getNin() ) );
					// Actualizar
					System.out.println( "Actualizar: " + explorador.getNome() );
					BatchUtils.setBatchId( contEntry, "update" );
					BatchUtils.setBatchOperationType( contEntry, BatchOperationType.UPDATE );
				}
				else
				{
					contEntry = ContactUtils.convertElementoToContactEntry( explorador, null );
					// Adicionar elemento
					System.out.println( "Adicionar: " + explorador.getNome() );
					BatchUtils.setBatchId( contEntry, "create" );
					BatchUtils.setBatchOperationType( contEntry, BatchOperationType.INSERT );
				}
				Map< String, ContactGroupEntry > groupsToDelete = new HashMap<>();
				ContactGroupEntry myContacts = processarGrupo.get( SECCAO.NONE );
				for ( Entry< SECCAO, ContactGroupEntry > entry : processarGrupo.entrySet() )
				{
					SECCAO seccao = entry.getKey();
					ContactGroupEntry groupEntry = entry.getValue();
					if ( explorador.getCategoria() != seccao && groupEntry.getSystemGroup() == null )
					{
						groupsToDelete.put( groupEntry.getId(), groupEntry );
					}
				}
				ContactGroupEntry contactGroupEntry = processarGrupo.get( explorador.getCategoria() );
				boolean associarGroup = true;
				for ( GroupMembershipInfo groupMembershipInfo : contEntry.getGroupMembershipInfos() )
				{
					if ( groupsToDelete.containsKey( groupMembershipInfo.getHref() ) )
					{
						groupMembershipInfo.setDeleted( true );
					}
					else if ( groupMembershipInfo.getHref().equals( contactGroupEntry.getId() ) )
					{
						associarGroup = false;
						break;
					}
				}
				if ( associarGroup )
				{
					GroupMembershipInfo groupMembershipInfo = new GroupMembershipInfo();
					groupMembershipInfo.setHref( contactGroupEntry.getId() );
					contEntry.getGroupMembershipInfos().add( groupMembershipInfo );
				}
				if ( myContacts != null )
				{
					GroupMembershipInfo groupMembershipInfo = new GroupMembershipInfo();
					groupMembershipInfo.setHref( myContacts.getId() );
					contEntry.getGroupMembershipInfos().add( groupMembershipInfo );
				}
				batchRequestFeed.getEntries().add( contEntry );
			}
			List< ContactEntry > listOk = new ArrayList<>();
			List< ContactEntry > listCriados = new ArrayList<>();
			List< ContactEntry > listErro = new ArrayList<>();
			List< ContactEntry > listNaoModificado = new ArrayList<>();
			for ( ContactFeed contactFeed : listBatchFeeds )
			{
				// Submit the batch request to the server.
				ContactFeed responseFeed =
								contactsService.batch( new URL( "https://www.google.com/m8/feeds/contacts/default/full/batch" ), contactFeed );
				// Check the status of each operation.
				for ( ContactEntry entry : responseFeed.getEntries() )
				{
					// String batchId = BatchUtils.getBatchId( entry );
					BatchStatus status = BatchUtils.getBatchStatus( entry );
					switch ( status.getCode() )
					{
						case 200:
							listOk.add( entry );
							break;
						case 201:
							listCriados.add( entry );
							break;
						case 304:
							listNaoModificado.add( entry );
							break;
						default:
							System.out.println( "Erro a processar :" + entry.getPlainTextContent() + " | " + status.getCode() + ": " +
								status.getReason() );
							listErro.add( entry );
							break;
					}
				}
			}
			VerticalLayout verticalLayout = new VerticalLayout();
			Label labelOk = new Label( "Contactos actualizados com sucesso" );
			Label labelOkValue = new Label( "<center><b>" + listOk.size() + "</b></center>", ContentMode.HTML );
			Label labelNovos = new Label( "Contactos criados com sucesso" );
			Label labelNovosValue = new Label( "<center><b>" + listCriados.size() + "</b></center>", ContentMode.HTML );
			Label labelErro = new Label( "Contactos não importados" );
			Label labelErroValue = new Label( "<font color=\"red\"><center><b>" + listErro.size() + "</b></center></font>", ContentMode.HTML );
			verticalLayout.addComponent( labelOk );
			verticalLayout.addComponent( labelOkValue );
			verticalLayout.addComponent( labelNovos );
			verticalLayout.addComponent( labelNovosValue );
			verticalLayout.addComponent( labelErro );
			verticalLayout.addComponent( labelErroValue );
			for ( ContactEntry contactEntry : listErro )
			{
				verticalLayout.addComponent( new Label( contactEntry.getName().getFullName().getValue() ) );
			}
			Window subWindow = new Window( "Resultado" );
			subWindow.setContent( verticalLayout );
			// Center it in the browser window
			subWindow.center();
			// Open it in the UI
			getUI().addWindow( subWindow );
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	private void showDebugNotification( String message )
	{
		System.out.println( message );
		// Notification notification = new Notification( message, Type.TRAY_NOTIFICATION );
		// notification.setDelayMsec( 1000 );
		// notification.setPosition( Position.TOP_RIGHT );
		Notification.show( message, Type.TRAY_NOTIFICATION );
	}

	/**
	 * The <b>processarGrupo</b> method returns {@link void}
	 * 
	 * @author anco62000465 2018-01-27
	 * @param contactsService
	 * @param elementosParaImportar
	 * @param contEntry
	 * @param seccao
	 * @return
	 */
	private Map< SECCAO, ContactGroupEntry > processarGrupo( ContactsService contactsService, Map< String, Explorador > elementosParaImportar )
	{
		Map< String, SECCAO > listSeccaoNecessaris = new HashMap<>();
		for ( Explorador explorador : elementosParaImportar.values() )
		{
			listSeccaoNecessaris.put( explorador.getCategoria().getNome(), explorador.getCategoria() );
		}
		// Create query and submit a request
		URL feedUrl;
		try
		{
			feedUrl = new URL( "https://www.google.com/m8/feeds/groups/default/full" );
			Query myQuery = new Query( feedUrl );
			ContactGroupFeed resultFeed = contactsService.query( myQuery, ContactGroupFeed.class );
			// Print the results
			ContactGroupEntry contactGroupEntry = null;
			Map< SECCAO, ContactGroupEntry > mapGrupos = new EnumMap<>( SECCAO.class );
			ContactGroupEntry myContacts = null;
			for ( ContactGroupEntry entry : resultFeed.getEntries() )
			{
				if ( listSeccaoNecessaris.containsKey( entry.getTitle().getPlainText() ) )
				{
					mapGrupos.put( listSeccaoNecessaris.get( entry.getTitle().getPlainText() ), entry );
					listSeccaoNecessaris.remove( entry.getTitle().getPlainText() );
				}
				// Adicionar aos contactos pessoais
				if ( entry.getSystemGroup() != null && entry.getSystemGroup().getId() != null && "Contacts".equals( entry.getSystemGroup().getId() ) )
				{
					myContacts = entry;
				}
			}
			for ( SECCAO seccao : listSeccaoNecessaris.values() )
			{
				if ( !mapGrupos.containsKey( seccao ) )
				{
					// Cria um novo grupo
					contactGroupEntry = new ContactGroupEntry();
					contactGroupEntry.setTitle( new PlainTextConstruct( seccao.getNome() ) );
					ExtendedProperty additionalInfo = new ExtendedProperty();
					additionalInfo.setName( "Informações do grupo" );
					additionalInfo.setValue( seccao.getDescricao() );
					contactGroupEntry.addExtendedProperty( additionalInfo );
					// Ask the service to insert the new entry
					URL postUrl = new URL( "https://www.google.com/m8/feeds/groups/default/full" );
					mapGrupos.put( seccao, contactsService.insert( postUrl, contactGroupEntry ) );
				}
			}
			if ( myContacts != null )
			{
				mapGrupos.put( SECCAO.NONE, myContacts );
			}
			return mapGrupos;
		}
		catch ( IOException | ServiceException e )
		{
			System.err.println( e.getMessage() );
		}
		return null;
	}
}

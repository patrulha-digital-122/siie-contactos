package scouts.cne.pt;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.Position;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import scouts.cne.pt.google.GoogleAuthenticationBean;
import scouts.cne.pt.layouts.EscolherElementosLayout;
import scouts.cne.pt.layouts.UploadFileLayout;
import scouts.cne.pt.model.Explorador;
import scouts.cne.pt.model.SECCAO;
import scouts.cne.pt.services.SIIEService;
import scouts.cne.pt.utils.ContactUtils;

@SpringUI
public class MyUI extends UI
{
	/**
	 *
	 */
	private static final long			serialVersionUID	= -8505226283440302479L;
	public final static String			VIEW_NAME			= "main";
	@Autowired
	SIIEService							siieService;
	private TextArea					debugTextArea;
	private StringBuilder				sbLog;
	@Autowired
	private GoogleAuthenticationBean	googleAuthentication;
	private EscolherElementosLayout		elementosLayout;

	@Override
	protected void init( VaadinRequest vaadinRequest )
	{
		UploadFileLayout uploadFileLayout = new UploadFileLayout();
		System.out.println( "New Session: " + getEmbedId() );
		setContent( uploadFileLayout.getLayout( this, siieService ) );
		getPage().setTitle( "SIIE - importer" );
		// FirebaseManager.getInstance().addLogMessage("App started");
	}

	public void receiveGoogleCode( String code )
	{
		try
		{
			GoogleCredential credential = googleAuthentication.getGoogleCredentials( code );
			String applicationName = googleAuthentication.getApplicationName();
			ContactsService contactsService = new ContactsService( applicationName );
			contactsService.setOAuth2Credentials( credential );
			URL feedUrl = new URL( "https://www.google.com/m8/feeds/contacts/default/full" );
			ContactFeed resultFeed = contactsService.getFeed( feedUrl, ContactFeed.class );
			// Print the results
			showDebugNotification( "Resultados recebidos: " + resultFeed.getTotalResults() );
			// Add a normal progress bar
			// elementosLayout.addComponent( progressBar );
			// elementosLayout.setComponentAlignment( progressBar, Alignment.MIDDLE_CENTER );
			Map< String, Explorador > elementosParaImportar = elementosLayout.getElementosSelecionados();
			showDebugNotification( "Elementos para importar: " + elementosParaImportar.size() );
			Map< String, ContactEntry > elementosExistentes = new HashMap<>();
			List<ContactEntry> listContc = new ArrayList<>( resultFeed.getEntries() );
			
			while ( resultFeed.getNextLink() != null )
			{
				resultFeed = contactsService.getFeed( new URL( resultFeed.getNextLink().getHref() ), ContactFeed.class );
				listContc.addAll( resultFeed.getEntries() );
			}
			
			for ( ContactEntry contactEntry : listContc )
			{
				try
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
				catch ( RuntimeException e )
				{
					System.err.println( e.getMessage() );
				}
			}
			Map< SECCAO, ContactGroupEntry > processarGrupo = processarGrupo( contactsService );
			List<ContactFeed> listBatchFeeds = new ArrayList<>();
			ContactFeed requestFeed = new ContactFeed();
			listBatchFeeds.add( requestFeed );
			int i = 0;
			for ( Explorador explorador : elementosParaImportar.values() )
			{
				if(++i > 98) {
					requestFeed = new ContactFeed();
					listBatchFeeds.add( requestFeed );
				}
				ContactEntry contEntry;
				if ( elementosExistentes.containsKey( explorador.getNin() ) )
				{
					contEntry = ContactUtils.convertElementoToContactEntry( explorador, elementosExistentes.get( explorador.getNin() ) );
					// Actualizar
					showDebugNotification( "Actualizar: " + explorador.getNome() );
					BatchUtils.setBatchId( contEntry, "update" );
					BatchUtils.setBatchOperationType( contEntry, BatchOperationType.UPDATE );
				}
				else
				{
					contEntry = ContactUtils.convertElementoToContactEntry( explorador, null );
					// Adicionar elemento
					showDebugNotification( "Adicionar: " + explorador.getNome() );
					BatchUtils.setBatchId( contEntry, "create" );
					BatchUtils.setBatchOperationType( contEntry, BatchOperationType.INSERT );
				}
				Map< String, ContactGroupEntry > groupsToDelete = new HashMap<>();
				for ( Entry< SECCAO, ContactGroupEntry > entry : processarGrupo.entrySet() )
				{
					SECCAO seccao = entry.getKey();
					ContactGroupEntry groupEntry = entry.getValue();
					if ( explorador.getCategoria() != seccao )
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
				requestFeed.getEntries().add( contEntry );
				// progressBar.setValue( totalElementos / ( ++iElementosImportados ) );
				elementosLayout.updateProgressBar();
			}
			for ( ContactFeed contactFeed : listBatchFeeds )
			{
				// Submit the batch request to the server.
				ContactFeed responseFeed =
								contactsService.batch( new URL( "https://www.google.com/m8/feeds/contacts/default/full/batch" ), contactFeed );
				// Check the status of each operation.
				for ( ContactEntry entry : responseFeed.getEntries() )
				{
					String batchId = BatchUtils.getBatchId( entry );
					BatchStatus status = BatchUtils.getBatchStatus( entry );
					System.out.println( batchId + ": " + status.getCode() + " (" + status.getReason() + ")" );
				}
			}
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

	/**
	 * The <b>processarGrupo</b> method returns {@link void}
	 * 
	 * @author anco62000465 2018-01-27
	 * @param contactsService
	 * @param contEntry
	 * @param seccao
	 * @return
	 */
	private Map< SECCAO, ContactGroupEntry > processarGrupo( ContactsService contactsService )
	{
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
			Map< String, SECCAO > listSeccao = new HashMap<>();
			for ( SECCAO seccao : SECCAO.getListaSeccoes() )
			{
				listSeccao.put( seccao.getNome(), seccao );
			}
			for ( ContactGroupEntry entry : resultFeed.getEntries() )
			{
				if ( listSeccao.containsKey( entry.getTitle().getPlainText() ) )
				{
					mapGrupos.put( listSeccao.get( entry.getTitle().getPlainText() ), entry );
				}
			}
			for ( SECCAO seccao : SECCAO.getListaSeccoes() )
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
			return mapGrupos;
		}
		catch ( IOException | ServiceException e )
		{
			System.err.println( e.getMessage() );
		}
		return null;
	}

	private void showDebugNotification( String message )
	{
		System.out.println( message );
		Notification notification = new Notification( message, Type.TRAY_NOTIFICATION );
		notification.setDelayMsec( 1000 );
		notification.setPosition( Position.TOP_RIGHT );
		//notification.show( getPage() );
	}

	public void showMenus()
	{
		elementosLayout = new EscolherElementosLayout();
		setContent( elementosLayout.getLayout( googleAuthentication, siieService, getEmbedId() ) );
	}

	public void addDebugInfo( String text )
	{
		access( new Runnable()
		{
			@Override
			public void run()
			{
				sbLog.append( text ).append( "\n" );
				debugTextArea.setValue( sbLog.toString() );
			}
		} );
	}
}

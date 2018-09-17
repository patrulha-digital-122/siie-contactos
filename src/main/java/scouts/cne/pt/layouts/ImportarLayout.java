package scouts.cne.pt.layouts;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;
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
import com.google.gdata.data.contacts.UserDefinedField;
import com.google.gdata.data.extensions.ExtendedProperty;
import com.google.gdata.data.extensions.PhoneNumber;
import com.google.gdata.util.ServiceException;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.google.GoogleAuthenticationBean;
import scouts.cne.pt.model.Explorador;
import scouts.cne.pt.model.SECCAO;
import scouts.cne.pt.utils.ContactUtils;
import scouts.cne.pt.utils.ContactVCardUtils;

/**
 * @author anco62000465 2018-09-17
 *
 */
public class ImportarLayout extends Panel implements HasLogger
{
	private static final long	serialVersionUID	= -6763770502811814642L;
	private Button btImportacao;
	private Button					btImportacaoVCard;
	private EscolherElementosLayout	elementosLayout;
	private GoogleAuthenticationBean	googleAuthentication;

	private GoogleCredential			credential			= null;

	/**
	 * constructor
	 * 
	 * @author anco62000465 2018-09-17
	 * @param button
	 */
	public ImportarLayout( EscolherElementosLayout elementosLayout, GoogleAuthenticationBean googleAuthentication )
	{
		super( "Terceiro Passo - Dar permissão para fazer a importação" );
		setSizeFull();
		this.elementosLayout = elementosLayout;
		this.googleAuthentication = googleAuthentication;
		btImportacao = new Button( "Iniciar Importação (0)" );
		btImportacao.setEnabled( false );
		btImportacao.addClickListener( event ->
		{
			if ( ( googleAuthentication.getRefreshToken() != null ) && ( elementosLayout.getSelecionados() > 0 ) )
			{
				importProcess();
			}
		} );
		btImportacaoVCard = new Button( "Download como VCard (0)" );
		btImportacaoVCard.setEnabled( false );
		StreamResource myResource = createVCardFile();
		FileDownloader fileDownloader = new FileDownloader( myResource );
		fileDownloader.extend( btImportacaoVCard );
		Label labelFooter = new Label(
		      						"<p><strong>Powered by:</strong> Patrulha Digital 122 - <a href=\"mailto:patrulha.digital.122@escutismo.pt?Subject=SIIE%20Contactos%20\" target=\"_top\">patrulha.digital.122@escutismo.pt</a> </p>",
		      						ContentMode.HTML );
		HorizontalLayout horizontalLayoutBtn = new HorizontalLayout( btImportacao, btImportacaoVCard );
		horizontalLayoutBtn.setWidth( "100%" );
		horizontalLayoutBtn.setComponentAlignment( btImportacao, Alignment.MIDDLE_CENTER );
		horizontalLayoutBtn.setComponentAlignment( btImportacaoVCard, Alignment.MIDDLE_CENTER );
		
		VerticalLayout verticalLayout = new VerticalLayout( horizontalLayoutBtn, labelFooter );
		verticalLayout.setComponentAlignment( labelFooter, Alignment.MIDDLE_CENTER );
		verticalLayout.setSizeFull();
		verticalLayout.setMargin( true );

		setContent( verticalLayout );
	}

	/**
	 * Getter for btImportacao
	 * 
	 * @author anco62000465 2018-09-17
	 * @return the btImportacao {@link Button}
	 */
	public Button getBtImportacao()
	{
		return btImportacao;
	}

	/**
	 * Getter for btImportacaoVCard
	 * 
	 * @author anco62000465 2018-09-17
	 * @return the btImportacaoVCard {@link Button}
	 */
	public Button getBtImportacaoVCard()
	{
		return btImportacaoVCard;
	}

	/**
	 * The <b>createVCardFile</b> method returns {@link StreamResource}
	 *
	 * @author anco62000465 2018-08-29
	 * @return
	 */
	private StreamResource createVCardFile()
	{
		return new StreamResource( new StreamSource()
		{
			/**
			 *
			 */
			private static final long serialVersionUID = 721545760831041530L;

			@Override
			public InputStream getStream()
			{
				try
				{
					return new FileInputStream( ContactVCardUtils.getVCardFile( elementosLayout.getElementosSelecionados().values() ) );
				}
				catch ( IOException e )
				{
					getLogger().error( e.getMessage(), e );
				}
				return null;
			};
		}, "contact.vcf" );
	}

	public void importProcess()
	{
		try
		{
			Map< String, Explorador > elementosParaImportar = elementosLayout.getElementosSelecionados();
			if ( elementosParaImportar.isEmpty() )
			{
				return;
			}
			if ( credential == null )
			{
				credential = googleAuthentication.getGoogleCredentials();
			}
			String applicationName = googleAuthentication.getApplicationName();
			ContactsService contactsService = new ContactsService( applicationName );
			contactsService.setOAuth2Credentials( credential );
			URL feedUrl = new URL( "https://www.google.com/m8/feeds/contacts/default/full" );
			ContactFeed resultFeed = contactsService.getFeed( feedUrl, ContactFeed.class );
			// Print the results
			// Add a normal progress bar
			// elementosLayout.addComponent( progressBar );
			// elementosLayout.setComponentAlignment( progressBar, Alignment.MIDDLE_CENTER
			// );
			// showDebugNotification( "Elementos para importar: " +
			// elementosParaImportar.size() );
			Map< String, ContactEntry > elementosExistentes = new HashMap<>();
			List< ContactEntry > listContc = new ArrayList<>( resultFeed.getEntries() );
			while ( resultFeed.getNextLink() != null )
			{
				resultFeed = contactsService.getFeed( new URL( resultFeed.getNextLink().getHref() ), ContactFeed.class );
				listContc.addAll( resultFeed.getEntries() );
			}
			// showDebugNotification( "Resultados recebidos: " +
			// resultFeed.getTotalResults() + " / " + listContc.size() );
			Set< String > listTelefonesExistentes = new TreeSet<>();
			for ( ContactEntry contactEntry : listContc )
			{
				for ( PhoneNumber phoneNumber : contactEntry.getPhoneNumbers() )
				{
					String strPhoneNumber = phoneNumber.getPhoneNumber();
					strPhoneNumber = strPhoneNumber.replace( " ", "" );
					if ( "NIN".equals( phoneNumber.getLabel() ) )
					{
						if ( elementosParaImportar.containsKey( strPhoneNumber.trim() ) )
						{
							elementosExistentes.put( strPhoneNumber, contactEntry );
						}
					}
					listTelefonesExistentes.add( strPhoneNumber );
				}
				for ( UserDefinedField userDefinedField : contactEntry.getUserDefinedFields() )
				{
					if ( StringUtils.equals( userDefinedField.getKey(), "NIN" ) )
					{
						String strNIN = StringUtils.trimToEmpty( userDefinedField.getValue() );
						if ( elementosParaImportar.containsKey( strNIN ) )
						{
							elementosExistentes.put( strNIN, contactEntry );
							getLogger().info(	"Actualizar :: {} - {}:{}",
												contactEntry.getName().getFullName().getValue(),
												userDefinedField.getKey(),
												userDefinedField.getValue() );
						}
					}
				}
			}
			Map< SECCAO, ContactGroupEntry > processarGrupo = processarGrupo( contactsService, elementosParaImportar );
			List< ContactFeed > listBatchFeeds = new ArrayList<>();
			ContactFeed batchRequestFeed = new ContactFeed();
			listBatchFeeds.add( batchRequestFeed );
			int i = 0;
			List< Explorador > values = new ArrayList<>( elementosParaImportar.values() );
			Collections.sort( values );
			for ( Explorador explorador : values )
			{
				if ( ++i > 98 )
				{
					batchRequestFeed = new ContactFeed();
					listBatchFeeds.add( batchRequestFeed );
					i = 0;
				}
				ContactEntry contEntry;
				ContactEntry elementoProcessar = elementosExistentes.get( explorador.getNin() );
				contEntry = ContactUtils.convertElementoToContactEntry( explorador, elementoProcessar, listTelefonesExistentes );
				if ( elementoProcessar != null )
				{
					// Actualizar
					System.out.println( "Actualizar: " + explorador.getNome() );
					BatchUtils.setBatchId( contEntry, "update" );
					BatchUtils.setBatchOperationType( contEntry, BatchOperationType.UPDATE );
				}
				else
				{
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
					if ( ( explorador.getCategoria() != seccao ) && ( groupEntry.getSystemGroup() == null ) )
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
		catch ( Exception e )
		{
			showError( e );
		}
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
				if ( ( entry.getSystemGroup() != null ) && ( entry.getSystemGroup().getId() != null ) &&
					"Contacts".equals( entry.getSystemGroup().getId() ) )
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

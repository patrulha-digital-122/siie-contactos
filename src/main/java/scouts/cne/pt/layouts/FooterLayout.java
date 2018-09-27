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
import java.util.Objects;
import java.util.Optional;
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
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

import j2html.TagCreator;
import j2html.tags.ContainerTag;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.component.EmailerWindow;
import scouts.cne.pt.component.ImportContactsConfigWindow;
import scouts.cne.pt.component.ImportContactsReportLayout;
import scouts.cne.pt.component.MailingListWindow;
import scouts.cne.pt.google.GoogleAuthenticationBean;
import scouts.cne.pt.model.Elemento;
import scouts.cne.pt.model.ImportContactReport;
import scouts.cne.pt.model.SECCAO;
import scouts.cne.pt.utils.ContactUtils;
import scouts.cne.pt.utils.ContactVCardUtils;
import scouts.cne.pt.utils.ElementoImport;

/**
 * @author anco62000465 2018-09-17
 *
 */
public class FooterLayout extends Panel implements HasLogger
{
	private static final long			serialVersionUID	= -6763770502811814642L;
	private final Button				btnAutorizacao;
	private Button						btImportacao;
	private Button						btImportacaoVCard;
	private final Button				btnCopyMailingList;
	private final Button				btnEmailer;
	private EscolherElementosLayout		elementosLayout;
	private GoogleAuthenticationBean	googleAuthentication;
	private final ImportContactsConfigWindow importContactsConfigWindow;

	/**
	 * constructor
	 *
	 * @author anco62000465 2018-09-17
	 * @param button
	 */
	public FooterLayout( EscolherElementosLayout elementosLayout, GoogleAuthenticationBean googleAuthentication )
	{
		super( "Funcionalidades disponiveis" );
		setSizeFull();
		this.elementosLayout = elementosLayout;
		this.googleAuthentication = googleAuthentication;
		this.importContactsConfigWindow = new ImportContactsConfigWindow();

		btnAutorizacao = new Button( "Autorizar", VaadinIcons.GOOGLE_PLUS_SQUARE );

		btImportacao = new Button( "Importação (0)", VaadinIcons.USER_STAR );
		btImportacao.setEnabled( false );
		btImportacao.setVisible( false );
		btImportacao.setDisableOnClick( true );
		btImportacao.addClickListener( event ->
		{
			if ( ( googleAuthentication.getRefreshToken() != null ) && ( elementosLayout.getSelecionados() > 0 ) )
			{
				getUI().addWindow( importContactsConfigWindow );
			}
		} );

		importContactsConfigWindow.addCloseListener( new CloseListener()
		{
			private static final long serialVersionUID = 5581538180777961098L;

			@Override
			public void windowClose( CloseEvent e )
			{
				if ( !importContactsConfigWindow.isCancel() )
				{
					importProcess();
				}
				btImportacao.setEnabled( true );
			}
		} );


		btImportacaoVCard = new Button( "Download como VCard (0)", VaadinIcons.DOWNLOAD_ALT );
		btImportacaoVCard.setEnabled( false );
		StreamResource myResource = createVCardFile();
		FileDownloader fileDownloader = new FileDownloader( myResource );
		fileDownloader.extend( btImportacaoVCard );
		Label labelFooter = new Label(
				"<p><strong>Powered by:</strong> Patrulha Digital 122 - <a href=\"mailto:patrulha.digital.122@escutismo.pt?Subject=SIIE%20Contactos%20\" target=\"_top\">patrulha.digital.122@escutismo.pt</a> </p>",
				ContentMode.HTML );
		Button btnFaq = new Button( "Ajuda - FAQ", VaadinIcons.QUESTION );
		btnFaq.addClickListener( new ClickListener()
		{
			private static final long serialVersionUID = -2312531713940582397L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				getUI().addWindow( new FAQWindow( btnFaq.getCaption() ) );
			}
		} );

		btnCopyMailingList = new Button( "Mailing list (0)", VaadinIcons.ENVELOPES );
		btnCopyMailingList.setEnabled( false );
		btnCopyMailingList.addClickListener( new ClickListener()
		{
			private static final long serialVersionUID = 8916961174824919931L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				getUI().addWindow( new MailingListWindow( elementosLayout.getElementosSelecionados().values() ) );
			}
		} );

		btnEmailer = new Button( "Email's", VaadinIcons.MAILBOX );
		btnEmailer.setEnabled( false );
		btnEmailer.setVisible( false );
		btnEmailer.addClickListener( new ClickListener()
		{
			private static final long serialVersionUID = 8916961174824919931L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				getUI().addWindow( new EmailerWindow( elementosLayout.getElementosSelecionados().values(), googleAuthentication ) );
			}
		} );
		HorizontalLayout horizontalLayoutBtn = new HorizontalLayout();
		horizontalLayoutBtn.setWidth( "100%" );
		horizontalLayoutBtn.setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );
		horizontalLayoutBtn.addComponents( btnAutorizacao, btImportacao, btImportacaoVCard, btnCopyMailingList, btnFaq );
		horizontalLayoutBtn.addComponent( btnEmailer );

		VerticalLayout verticalLayout = new VerticalLayout( horizontalLayoutBtn, labelFooter );
		verticalLayout.setComponentAlignment( labelFooter, Alignment.MIDDLE_CENTER );
		verticalLayout.setMargin( true );
		setContent( verticalLayout );
	}


	/**
	 * Getter for btnAutorizacao
	 * @author anco62000465 2018-09-26
	 * @return the btnAutorizacao  {@link Button}
	 */
	public Button getBtnAutorizacao()
	{
		return btnAutorizacao;
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
	 * Getter for btnCopyMailingList
	 *
	 * @author anco62000465 2018-09-21
	 * @return the btnCopyMailingList {@link Button}
	 */
	public Button getBtnCopyMailingList()
	{
		return btnCopyMailingList;
	}

	/**
	 * Getter for btnEmailer
	 *
	 * @author anco62000465 2018-09-25
	 * @return the btnEmailer {@link Button}
	 */
	public Button getBtnEmailer()
	{
		return btnEmailer;
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
			Map< String, Elemento > elementosParaImportar = elementosLayout.getElementosSelecionados();
			if ( elementosParaImportar.isEmpty() )
			{
				return;
			}
			GoogleCredential credential = googleAuthentication.getGoogleCredentials();
			if ( credential == null )
			{
				showWarning( "Aplicação sem autorização..." );
				return;
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
						}
					}
				}
			}
			Map< SECCAO, ContactGroupEntry > processarGrupo = processarGrupo( contactsService, elementosParaImportar );
			List< ContactFeed > listBatchFeeds = new ArrayList<>();
			ContactFeed batchRequestFeed = new ContactFeed();
			listBatchFeeds.add( batchRequestFeed );
			int i = 0;
			List< Elemento > values = new ArrayList<>( elementosParaImportar.values() );
			Map< String, ElementoImport > importReports = new HashMap<>();
			Collections.sort( values );
			for ( Elemento elemento : values )
			{
				if ( ++i > 98 )
				{
					batchRequestFeed = new ContactFeed();
					listBatchFeeds.add( batchRequestFeed );
					i = 0;
				}
				ElementoImport elementoImport;
				ContactEntry elementoProcessar = elementosExistentes.get( elemento.getNin() );
				elementoImport = ContactUtils.convertElementoToContactEntry(	elemento,
						elementoProcessar,
						listTelefonesExistentes,
						importContactsConfigWindow.getMapProperties() );
				importReports.put( elementoImport.getImportContactReport().getNin(), elementoImport );
				if ( elementoProcessar != null )
				{
					// Actualizar
					BatchUtils.setBatchId( elementoImport.getContactEntry(), "update" );
					BatchUtils.setBatchOperationType( elementoImport.getContactEntry(), BatchOperationType.UPDATE );
				}
				else
				{
					// Adicionar elemento
					BatchUtils.setBatchId( elementoImport.getContactEntry(), "create" );
					BatchUtils.setBatchOperationType( elementoImport.getContactEntry(), BatchOperationType.INSERT );
				}
				Map< String, ContactGroupEntry > groupsToDelete = new HashMap<>();
				ContactGroupEntry myContacts = processarGrupo.get( SECCAO.NONE );
				for ( Entry< SECCAO, ContactGroupEntry > entry : processarGrupo.entrySet() )
				{
					SECCAO seccao = entry.getKey();
					ContactGroupEntry groupEntry = entry.getValue();
					if ( ( elemento.getCategoria() != seccao ) && ( groupEntry.getSystemGroup() == null ) )
					{
						groupsToDelete.put( groupEntry.getId(), groupEntry );
					}
				}
				ContactGroupEntry contactGroupEntry = processarGrupo.get( elemento.getCategoria() );
				boolean associarGroup = true;
				for ( GroupMembershipInfo groupMembershipInfo : elementoImport.getContactEntry().getGroupMembershipInfos() )
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
					elementoImport.getContactEntry().getGroupMembershipInfos().add( groupMembershipInfo );
				}
				if ( myContacts != null )
				{
					GroupMembershipInfo groupMembershipInfo = new GroupMembershipInfo();
					groupMembershipInfo.setHref( myContacts.getId() );
					elementoImport.getContactEntry().getGroupMembershipInfos().add( groupMembershipInfo );
				}
				batchRequestFeed.getEntries().add( elementoImport.getContactEntry() );
			}
			List< ElementoImport > listOk = new ArrayList<>();
			List< ElementoImport > listCriados = new ArrayList<>();
			List< ElementoImport > listErro = new ArrayList<>();
			List< ElementoImport > listNaoModificado = new ArrayList<>();
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
					ElementoImport e = importReports.get( getNinFromContactEntry( entry.getUserDefinedFields() ) );

					String fullName = "?";
					if ((entry.getName() != null) && (entry.getName().getFullName() != null)) {
						fullName = entry.getName().getFullName().getValue();
					} else {
						fullName = Objects.toString(entry.getId(), "?");
					}
					switch ( status.getCode() )
					{
					case 200:
						getLogger().error( "Contacto actualizado: {}", fullName );
						listOk.add( e );
						break;
					case 201:
						getLogger().error( "Contacto criados: {}", fullName );
						e.getContactEntry().setId( entry.getId() );
						listCriados.add( e );
						break;
					case 304:
						getLogger().error( "Contacto não actualizados: {}", entry.getName().getFullName() );
						listNaoModificado.add( e );
						break;
					default:
						getLogger().error(	"Erro a processar : {} | {} :: {}",
								entry.getPlainTextContent(),
								status.getCode(),
								status.getReason() );
						ContainerTag join = TagCreator.p( TagCreator.join(	TagCreator.text( "Código do erro: " ),
								TagCreator.b( String.valueOf( status.getCode() ) ),
								TagCreator.text( " | Motivo: " ),
								TagCreator.b( status.getReason() ) ) );
						ElementoImport elementoImport = new ElementoImport( entry, null,
								new ImportContactReport( getNinFromContactEntry( entry.getUserDefinedFields() ) ) );
						elementoImport.getImportContactReport().getLstLabels().add( join.render() );
						listErro.add( elementoImport );
						break;
					}
				}
			}
			Window window = new Window( "Resultado" );
			window.setContent( new ImportContactsReportLayout( listOk, listCriados, listErro, listNaoModificado ) );
			// Center it in the browser window
			window.center();
			window.setResizable( true );
			window.setModal( true );
			window.setHeight( "500px" );
			window.setWidth( "1000px" );
			// Open it in the UI
			getUI().addWindow( window );
		}
		catch ( Exception e )
		{
			showError( e );
		}
	}

	private String getNinFromContactEntry( List< UserDefinedField > userDefinedFields )
	{
		Optional< UserDefinedField > findFirst = userDefinedFields.stream().filter( p -> StringUtils.equals( p.getKey(), "NIN" ) ).findFirst();
		if ( findFirst.isPresent() )
		{
			return findFirst.get().getValue();
		}
		else
		{
			return null;
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
	private Map< SECCAO, ContactGroupEntry > processarGrupo( ContactsService contactsService, Map< String, Elemento > elementosParaImportar )
	{
		Map< String, SECCAO > listSeccaoNecessaris = new HashMap<>();
		for ( Elemento elemento : elementosParaImportar.values() )
		{
			listSeccaoNecessaris.put( elemento.getCategoria().getNome(), elemento.getCategoria() );
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
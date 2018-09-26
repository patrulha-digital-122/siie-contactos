package scouts.cne.pt.component;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.lang3.StringUtils;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.google.GoogleAuthenticationBean;
import scouts.cne.pt.model.Elemento;
import scouts.cne.pt.model.ElementoTags;
import scouts.cne.pt.utils.HTMLUtils;

/**
 * @author anco62000465 2018-09-24
 *
 */
public class EmailerWindow extends Window implements Serializable, HasLogger
{
	private static final long				serialVersionUID	= 3816724843669785606L;
	private GoogleAuthenticationBean	googleAuthentication;
	private RichTextArea				richTextArea;
	private Panel						listTagsPanel;
	private Button							btnPreview;
	private Button							btnEnviarEmail;
	private Button						btnPrevious;
	private Button						btnNext;
	private Label							labelPopUp;
	private TextField					txtEmailSubject;
	private CheckBox					chbWithParents;
	private CheckBox					chbEmailSplitted;
	private final VerticalLayout			mainLayout;
	private final HorizontalLayout		bodyLayout;
	private final List< Elemento >		lstElementos;
	private int							iElementCount		= 1;

	/**
	 * constructor
	 * 
	 * @author anco62000465 2018-09-25
	 */
	public EmailerWindow( Collection< Elemento > lst, GoogleAuthenticationBean googleAuthentication )
	{
		super();
		this.googleAuthentication = googleAuthentication;
		lstElementos = new LinkedList<>( lst );
		setCaption( "Enviar email's" );
		setCaptionAsHtml( true );
		center();
		setResizable( true );
		setHeight( "600px" );
		setWidth( "1000px" );
		setModal( true );
		mainLayout = new VerticalLayout();
		mainLayout.setSpacing( false );
		mainLayout.setMargin( new MarginInfo( true, true, false, true ) );
		mainLayout.setSizeFull();
		mainLayout.setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );

		initRichText();
		initListTag();
		initBtnEnviarEmail();

		HorizontalSplitPanel editorLayout = getEditorLayout();
		HorizontalLayout configLayout = getConfigLayout();
		VerticalSplitPanel layoutPreviousNext = getLayoutPreviousNext();
		initBtnPreview( editorLayout, layoutPreviousNext );
		bodyLayout = new HorizontalLayout();
		bodyLayout.setSizeFull();
		bodyLayout.setMargin( false );
		bodyLayout.setSpacing( false );
		bodyLayout.setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );
		bodyLayout.addComponent( editorLayout );

		HorizontalLayout buttonsLayout = getButtonsLayout();
		
		mainLayout.addComponents( configLayout, bodyLayout, buttonsLayout );
		mainLayout.setExpandRatio( configLayout, 1 );
		mainLayout.setExpandRatio( bodyLayout, 6 );
		mainLayout.setExpandRatio( buttonsLayout, 1 );
		
		setContent( mainLayout );
	}

	/**
	 * The <b>initBtnEnviarEmail</b> method returns {@link void}
	 * @author anco62000465 2018-09-25 
	 */
	private void initBtnEnviarEmail()
	{
		btnEnviarEmail = new Button( "Enviar email", VaadinIcons.MAILBOX );
		btnEnviarEmail.setDisableOnClick( true );
		btnEnviarEmail.addClickListener( new ClickListener()
		{
			private static final long serialVersionUID = -1006672708345973490L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				try
				{
					String message = "?";
					if ( chbEmailSplitted.getValue() )
					{
						message = sendSplittedEmail();
					}
					else
					{
						message = sendOneEmail();
					}
					
					showInfo( message );
				}
				catch ( Exception e )
				{
					showError( e );
					return;
				}
				btnEnviarEmail.setEnabled( true );
			}

			/**
			 * The <b>sendSplittedEmail</b> method returns {@link void}
			 * 
			 * @author anco62000465 2018-09-26
			 * @return
			 * @throws GeneralSecurityException
			 * @throws IOException
			 * @throws UnsupportedEncodingException
			 * @throws MessagingException
			 */
			private String sendSplittedEmail() throws GeneralSecurityException, IOException, UnsupportedEncodingException, MessagingException
			{
				Gmail service = googleAuthentication.getGmailService();
				List< String > lstSentEmails = new ArrayList<>();
				int iSentMailsCount = 0;
				for ( Elemento elemento : lstElementos )
				{
					List< InternetAddress > lstEmails = new ArrayList<>();
					lstEmails.add( new InternetAddress( elemento.getEmailPrincipal(), elemento.getNome() ) );
					if ( chbWithParents.getValue() )
					{
						if ( StringUtils.isNotBlank( elemento.getEmailMae() ) )
						{
							lstEmails.add( new InternetAddress( elemento.getEmailMae(), elemento.getNomeMae() ) );
						}
						if ( StringUtils.isNotBlank( elemento.getEmailPai() ) )
						{
							lstEmails.add( new InternetAddress( elemento.getEmailPai(), elemento.getNomePai() ) );
						}
					}
					String strHTMLEmail = ElementoTags.convertHTML( richTextArea.getValue(), elemento );
					MimeMessage createEmail = HTMLUtils.createEmail(	null,
																		lstEmails,
																		null,
																		"patrulha.digital.122@escutismo.pt",
																		txtEmailSubject.getValue(),
																		strHTMLEmail );
					Message message = service.users().messages().send( "me", HTMLUtils.createMessageWithEmail( createEmail ) ).execute();
					getLogger().info( "Enviado email: {}", message.toPrettyString() );
					lstEmails.forEach( p -> lstSentEmails.add( p.getAddress() ) );
				}
				return "Enviado com sucesso " + iSentMailsCount + " emails para " + lstSentEmails.size() + " endereços.";
			}

			/**
			 * 
			 * The <b>sendOneEmail</b> method returns {@link void}
			 * 
			 * @author anco62000465 2018-09-26
			 * @return
			 * @throws GeneralSecurityException
			 * @throws IOException
			 * @throws UnsupportedEncodingException
			 * @throws MessagingException
			 */
			private String sendOneEmail() throws GeneralSecurityException, IOException, UnsupportedEncodingException, MessagingException
			{
				Gmail service = googleAuthentication.getGmailService();
				List< InternetAddress > lstEmails = new ArrayList<>();
				for ( Elemento elemento : lstElementos )
				{
					lstEmails.add( new InternetAddress( elemento.getEmailPrincipal(), elemento.getNome() ) );
					if ( chbWithParents.getValue() )
					{
						if ( StringUtils.isNotBlank( elemento.getEmailMae() ) )
						{
							lstEmails.add( new InternetAddress( elemento.getEmailMae(), elemento.getNomeMae() ) );
						}
						if ( StringUtils.isNotBlank( elemento.getEmailPai() ) )
						{
							lstEmails.add( new InternetAddress( elemento.getEmailPai(), elemento.getNomePai() ) );
						}
					}
				}

				if ( !lstEmails.isEmpty() )
				{
					MimeMessage createEmail = HTMLUtils.createEmail(	null,
																		null,
																		lstEmails,
																		"patrulha.digital.122@escutismo.pt",
																		txtEmailSubject.getValue(),
																		richTextArea.getValue() );
					Message message = service.users().messages().send( "me", HTMLUtils.createMessageWithEmail( createEmail ) ).execute();
					getLogger().info( "Enviado email: {}", message.toPrettyString() );
					return "Email enviado com sucesso para " + lstEmails.size() + " endereços.";
				}
				return "Lista de emails vazia";
			}
		} );
	}

	/**
	 * The <b>getButtonsLayout</b> method returns {@link Component}
	 * @author anco62000465 2018-09-25
	 * @return 
	 */
	private HorizontalLayout getButtonsLayout()
	{
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSizeFull();
		horizontalLayout.setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );
		horizontalLayout.addComponents( btnPreview, btnEnviarEmail );
		return horizontalLayout;
	}

	/**
	 * The <b>initBtnPreview</b> method returns {@link void}
	 * 
	 * @author anco62000465 2018-09-25
	 * @param layoutPreviousNext
	 * @param editorLayout
	 */
	private void initBtnPreview( HorizontalSplitPanel editorLayout, VerticalSplitPanel layoutPreviousNext )
	{
		String captionPreView = "Pré-visualizar mensagem";
		String captionEditView = "Editar mensagem";
		btnPreview = new Button( captionPreView, click ->
		{
			if ( btnPreview.getCaption().equals( captionPreView ) )
			{
				bodyLayout.removeAllComponents();
				iElementCount = 1;
				btnNext.click();
				bodyLayout.addComponent( layoutPreviousNext );
				btnPreview.setCaption( captionEditView );
			}
			else
			{
				bodyLayout.removeAllComponents();
				bodyLayout.addComponent( editorLayout );
				btnPreview.setCaption( captionPreView );
			}
		} );
	}

	/**
	 * The <b>getBtnPreviousNext</b> method returns {@link int}
	 * 
	 * @author anco62000465 2018-09-25
	 * @return
	 */
	private VerticalSplitPanel getLayoutPreviousNext()
	{

		HorizontalLayout footerLayout = new HorizontalLayout();
		footerLayout.setSizeFull();
		footerLayout.setMargin( false );
		footerLayout.setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );
		Label label = new Label( iElementCount + " de " + lstElementos.size() );
		btnPrevious = new Button( "Anterior", click ->
		{
			iElementCount -= 1;
			if ( iElementCount <= lstElementos.size() )
			{
				labelPopUp.setValue( ElementoTags.convertHTML( richTextArea.getValue(), lstElementos.get( iElementCount - 1 ) ) );
			}
			if ( iElementCount <= 1 )
			{
				iElementCount = 1;
				btnPrevious.setEnabled( false );
			}
			btnNext.setEnabled( lstElementos.size() > 1 );
			label.setValue( iElementCount + " de " + lstElementos.size() );
		} );
		btnPrevious.setEnabled( false );
		btnNext = new Button( "Próximo", click ->
		{
			iElementCount += 1;

			if ( iElementCount <= lstElementos.size() )
			{
				labelPopUp.setValue( ElementoTags.convertHTML( richTextArea.getValue(), lstElementos.get( iElementCount - 1 ) ) );
			}

			if ( iElementCount >= lstElementos.size() )
			{
				iElementCount = lstElementos.size();
				btnNext.setEnabled( false );
			}
			btnPrevious.setEnabled( lstElementos.size() > 1 );

			label.setValue( iElementCount + " de " + lstElementos.size() );
		} );
		btnNext.setEnabled( lstElementos.size() > 1 );

		footerLayout.addComponents( btnPrevious, label, btnNext );


		labelPopUp = new Label( ElementoTags.convertHTML( richTextArea.getValue(), lstElementos.get( 0 ) ), ContentMode.HTML );

		VerticalSplitPanel verticalSplitPanel = new VerticalSplitPanel( labelPopUp, footerLayout );
		verticalSplitPanel.setSplitPosition( 90 );
		verticalSplitPanel.setWidth( "100%" );
		return verticalSplitPanel;
	}

	/**
	 * The <b>initRichText</b> method returns {@link RichTextArea}
	 * 
	 * @author anco62000465 2018-09-25
	 * @return
	 * @return
	 */
	private void initRichText()
	{
		richTextArea = new RichTextArea();
		richTextArea.setSizeFull();
		richTextArea.setValue( HTMLUtils.getDefaultEmail() );
	}

	/**
	 * The <b>initListTag</b> method returns {@link ListSelect<ElementoTags>}
	 * 
	 * @author anco62000465 2018-09-25
	 * @return
	 */
	private void initListTag()
	{
		listTagsPanel = new Panel( "Lista de propriedades" );
		listTagsPanel.setDescription( "Para utilizar alguma destas propriedades, basta clicar." );
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setWidth( "100%" );
		verticalLayout.setDefaultComponentAlignment( Alignment.MIDDLE_LEFT );
		for ( ElementoTags elementoTags : ElementoTags.values() )
		{
			Button button = new Button( elementoTags.getTagDescription() );
			button.setDescription( elementoTags.getTagDescription() );
			button.addClickListener( new ClickListener()
			{
				private static final long serialVersionUID = 7823411359629919805L;

				@Override
				public void buttonClick( ClickEvent event )
				{
					// get possible transfer data
					StringBuilder sb = new StringBuilder( richTextArea.getValue() );
					sb.append( elementoTags.getTagReplace() );
					richTextArea.setValue( sb.toString() );
				}
			} );
			
			verticalLayout.addComponent( button );
		}
		listTagsPanel.setContent( verticalLayout );
	}

	/**
	 * The <b>getEditorLayout</b> method returns {@link void}
	 * 
	 * @author anco62000465 2018-09-25
	 * @param richTextLayout
	 */
	private HorizontalSplitPanel getEditorLayout()
	{
		HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel( richTextArea, listTagsPanel );
		horizontalSplitPanel.setSplitPosition( 75 );
		horizontalSplitPanel.setWidth( "100%" );
		return horizontalSplitPanel;
	}

	/**
	 * The <b>getConfigLayout</b> method returns {@link HorizontalSplitPanel}
	 * 
	 * @author anco62000465 2018-09-26
	 * @return
	 */
	private HorizontalLayout getConfigLayout()
	{
		txtEmailSubject = new TextField( "Assunto do email", "Actualização de contactos" );
		txtEmailSubject.setIcon( VaadinIcons.COMMENT_ELLIPSIS_O );
		txtEmailSubject.setWidth( "100%" );
		chbWithParents = new CheckBox( "Utilizar email dos pais", true );
		chbEmailSplitted = new CheckBox( "Enviar emails em separado", true );
		chbEmailSplitted.setDescription( "Se esta opção estiver activa envia um email para cada elemento, caso contrário envia o mesmo email para todos os conactos selecionados (em Bcc)" );
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSizeFull();
		horizontalLayout.setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );
		horizontalLayout.addComponents( txtEmailSubject, chbWithParents, chbEmailSplitted );
		return horizontalLayout;
	}
}

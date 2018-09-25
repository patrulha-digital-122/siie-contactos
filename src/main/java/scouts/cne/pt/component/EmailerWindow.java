package scouts.cne.pt.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;
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
	private final VerticalLayout			mainLayout;
	private final List< Elemento >		lstElementos;
	private int							iElementCount		= 0;

	/**
	 * constructor
	 * 
	 * @author anco62000465 2018-09-25
	 */
	public EmailerWindow( Collection< Elemento > lst, GoogleAuthenticationBean googleAuthentication )
	{
		super();
		lstElementos = new ArrayList<>( lst );
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
		initBtnPreview();
		initBtnEnviarEmail();

		HorizontalSplitPanel editorLayout = getEditorLayout();
		HorizontalLayout buttonsLayout = getButtonsLayout();
		mainLayout.addComponents( editorLayout, buttonsLayout );
		mainLayout.setExpandRatio( editorLayout, 5 );
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
		btnEnviarEmail.addClickListener( new ClickListener()
		{
			private static final long serialVersionUID = -1006672708345973490L;

			@Override
			public void buttonClick( ClickEvent event )
			{

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
	 */
	private void initBtnPreview()
	{
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setSizeFull();
		verticalLayout.setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );
		labelPopUp = new Label( ElementoTags.convertHTML( richTextArea.getValue(), lstElementos.get( 0 ) ), ContentMode.HTML );
		verticalLayout.addComponents( labelPopUp, getLayoutPreviousNext() );
		PopupView popupView = new PopupView( null, verticalLayout );
		btnPreview = new Button( "Pré-visualizar mensagem", click ->
		{
			popupView.setPopupVisible( true );
			iElementCount = 0;
		} );
		mainLayout.addComponent( popupView );
	}

	/**
	 * The <b>getBtnPreviousNext</b> method returns {@link int}
	 * 
	 * @author anco62000465 2018-09-25
	 * @return
	 */
	private HorizontalLayout getLayoutPreviousNext()
	{
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSizeFull();
		horizontalLayout.setDefaultComponentAlignment( Alignment.MIDDLE_CENTER );

		btnPrevious = new Button( "Anterior", click -> updateLabelPopUp( iElementCount-- ) );
		btnPrevious.setEnabled( false );
		btnNext = new Button( "Próximo", click -> updateLabelPopUp( iElementCount++ ) );
		btnNext.setEnabled( lstElementos.size() > 1 );

		horizontalLayout.addComponents( btnPrevious, btnNext );
		return horizontalLayout;
	}

	/**
	 * The <b>updateLabelPopUp</b> method returns {@link Object}
	 * 
	 * @author anco62000465 2018-09-25
	 * @param i
	 * @return
	 */
	private void updateLabelPopUp( int i )
	{
		getLogger().info( "iElementCount={}", iElementCount );
		if ( i >= 0 && i < lstElementos.size() )
		{
			getLogger().info( "iElementCount2={}", iElementCount );
			labelPopUp.setValue( ElementoTags.convertHTML( richTextArea.getValue(), lstElementos.get( i ) ) );
		}

		if ( iElementCount < 1 )
		{
			btnPrevious.setEnabled( false );
			btnNext.setEnabled( lstElementos.size() > 1 );
		}
		if ( iElementCount >= lstElementos.size() )
		{
			btnPrevious.setEnabled( lstElementos.size() > 1 );
			btnNext.setEnabled( false );
		}
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
}

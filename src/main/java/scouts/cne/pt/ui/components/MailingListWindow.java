package scouts.cne.pt.ui.components;

import java.util.Collection;
import javax.mail.internet.InternetAddress;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import scouts.cne.pt.model.Elemento;

/**
 * @author anco62000465 2018-09-21
 *
 */
public class MailingListWindow extends Window
{
	private final Collection< Elemento >	lstElementos;
	private CheckBox						chbWithParents;
	private CheckBox						chbWithNames;
	private TextArea						textArea;

	/**
	 * constructor
	 * 
	 * @author anco62000465 2018-09-21
	 * @param collection
	 */
	public MailingListWindow( Collection< Elemento > collection )
	{
		super();
		setCaption( "Mailing list" );
		this.lstElementos = collection;
		chbWithParents = new CheckBox( "Utilizar emails dos pais", true );
		ValueChangeListener< Boolean > listener = new ValueChangeListener< Boolean >()
		{
			@Override
			public void valueChange( ValueChangeEvent< Boolean > event )
			{
				if ( event.isUserOriginated() )
				{
					fillTextArea();
				}
			}
		};
		chbWithParents.addValueChangeListener( listener );
		chbWithNames = new CheckBox( "Utilizar nomes", true );
		chbWithNames.addValueChangeListener( listener );
		textArea = new TextArea( "", "" );
		textArea.setSizeFull();
		textArea.setEnabled( false );
		textArea.selectAll();
		center();
		setResizable( true );
		setHeight( "500px" );
		setWidth( "1000px" );
		setModal( true );
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setSizeFull();
		HorizontalLayout checkBoxLayout = getCheckBoxLayout();
		verticalLayout.addComponents( checkBoxLayout, textArea );
		verticalLayout.setExpandRatio( checkBoxLayout, 1 );
		verticalLayout.setExpandRatio( textArea, 20 );
		fillTextArea();
		setContent( verticalLayout );
	}

	private void fillTextArea()
	{
		StringBuilder sb = new StringBuilder();
		for ( Elemento elemento : lstElementos )
		{
			processEmail( sb, elemento.getInternetAddressEmail() );
			if ( chbWithParents.getValue() )
			{
				processEmail( sb, elemento.getInternetAddressEmailMae() );
				processEmail( sb, elemento.getInternetAddressEmailPai() );
			}
		}
		textArea.setValue( sb.toString() );
		textArea.selectAll();
	}

	/**
	 * The <b>processEmail</b> method returns {@link void}
	 * 
	 * @author anco62000465 2018-09-21
	 * @param sb
	 * @param internetAddressEmail
	 */
	private void processEmail( StringBuilder sb, InternetAddress internetAddressEmail )
	{
		if ( internetAddressEmail != null )
		{
			if ( sb.length() > 0 )
			{
				sb.append( ", " );
			}
			if ( chbWithNames.getValue() )
			{
				sb.append( "\"" );
				sb.append( internetAddressEmail.getPersonal() );
				sb.append( "\" " );
			}
			sb.append( "<" );
			sb.append( internetAddressEmail.getAddress() );
			sb.append( ">" );
		}
	}

	private HorizontalLayout getCheckBoxLayout()
	{
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.addComponents( chbWithNames, chbWithParents );
		horizontalLayout.setComponentAlignment( chbWithNames, Alignment.MIDDLE_CENTER );
		horizontalLayout.setComponentAlignment( chbWithParents, Alignment.MIDDLE_CENTER );
		horizontalLayout.setWidth( "100%" );

		return horizontalLayout;
	}
}

package scouts.cne.pt.ui.components;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.faq.Faq;
import scouts.cne.pt.model.faq.Faqs;

/**
 * @author anco62000465 2018-09-07
 *
 */
public class FAQWindow extends Window implements HasLogger
{
	private static final long serialVersionUID = -3395641231166593087L;

	/**
	 * constructor
	 * 
	 * @author anco62000465 2018-09-07
	 * @param strTitle
	 */
	public FAQWindow( String strTitle )
	{
		setCaption( strTitle );
		setCaptionAsHtml( true );
		center();
		setResizable( true );
		setHeight( "500px" );
		setWidth( "1000px" );
		setModal( true );
		Accordion accordion = new Accordion();
		accordion.setSizeFull();
		accordion.setTabCaptionsAsHtml( true );
		try
		{
			int iCount = 1;
			accordion.addTab( new Label(), "<p>Perguntas e <span style=\\\"color: #999999;\\\">talvez</span> algumas respostas...</p>" );
			for ( Faq faq : getFaqList() )
			{
				final Label label = new Label( faq.getResponse() );
				if ( faq.isHTML() )
				{
					label.setContentMode( ContentMode.HTML );
				}
				label.setWidth( 100.0f, Unit.PERCENTAGE );
				final VerticalLayout layout = new VerticalLayout( label );
				layout.setMargin( true );
				accordion.addTab( layout, iCount++ + ". " + faq.getQuestion() );
			}
		}
		catch ( Exception e )
		{
			printError( e );
			setComponentError( new UserError( e.getMessage() ) );
		}
		setContent( new VerticalLayout( accordion ) );
	}

	private List< Faq > getFaqList() throws IOException
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Faqs faqs = new Faqs();
		try ( InputStream inputStream = classLoader.getResourceAsStream( "faq.json" ) )
		{
			ObjectMapper mapper = new ObjectMapper();
			faqs = mapper.readValue( inputStream, Faqs.class );
		}
		catch ( Exception e )
		{
			printError( e );
		}
		return faqs.getFaqList();
	}
}

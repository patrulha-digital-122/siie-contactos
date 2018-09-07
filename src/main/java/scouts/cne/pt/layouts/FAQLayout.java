package scouts.cne.pt.layouts;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.faq.Faq;
import scouts.cne.pt.model.faq.Faqs;

/**
 * @author anco62000465 2018-09-07
 *
 */
public class FAQLayout extends VerticalLayout implements HasLogger
{
	private static final long serialVersionUID = -3395641231166593087L;

	/**
	 * constructor
	 * 
	 * @author anco62000465 2018-09-07
	 */
	public FAQLayout()
	{
		setSizeFull();
		setCaption( "Perguntas e algumas respotas..." );
		Accordion accordion = new Accordion();
		accordion.setSizeFull();
		try
		{
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
				accordion.addTab( layout, faq.getQuestion() );
			}
		}
		catch ( Exception e )
		{
			printError( e );
			accordion.setComponentError( new UserError( e.getMessage() ) );
		}
		addComponent( accordion );
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

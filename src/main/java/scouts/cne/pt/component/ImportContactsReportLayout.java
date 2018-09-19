package scouts.cne.pt.component;

import java.util.List;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import scouts.cne.pt.utils.ElementoImport;

/**
 * @author anco62000465 2018-09-19
 *
 */
public class ImportContactsReportLayout extends VerticalLayout
{
	private static final long serialVersionUID = 6350206714629945520L;

	public ImportContactsReportLayout(	List< ElementoImport > listOk,
										List< ElementoImport > listCriados,
										List< ElementoImport > listErro,
										List< ElementoImport > listNaoModificado )
	{
		setSizeFull();
		processList( listOk, "<center>Contactos actualizados com sucesso: <b>" + listOk.size() + "</b></center>" );
		processList( listCriados, "<center>Contactos não importados: <font color=\"red\"><b>" + listErro.size() + "</b></center></font>" );
		processList( listErro, "<center>Contactos não importados: <font color=\"red\"><b>" + listErro.size() + "</b></center></font>" );
	}

	/**
	 * The <b>processList</b> method returns {@link void}
	 * 
	 * @author anco62000465 2018-09-19
	 * @param listOk
	 * @param string
	 */
	private void processList( List< ElementoImport > list, String strLabelTitle )
	{
		if ( !list.isEmpty() )
		{
			Accordion accordionOk = new Accordion();
			accordionOk.setCaption( strLabelTitle );
			accordionOk.setCaptionAsHtml( true );
			accordionOk.setTabCaptionsAsHtml( true );
			for ( ElementoImport elementoImport : list )
			{
				final Label label = new Label( elementoImport.getHTMLImportContactReport(), ContentMode.HTML );
				final VerticalLayout layout = new VerticalLayout( label );
				layout.setMargin( true );
				accordionOk.addTab(	layout,
									String.format(	"<p><strong>%s</strong></p>",
													elementoImport.getElemento().getNome(),
													elementoImport.getContactEntry().getSelfLink().getHref() ) );
			}
			addComponent( accordionOk );
		}
	}
}

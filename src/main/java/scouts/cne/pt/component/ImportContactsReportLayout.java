package scouts.cne.pt.component;

import java.util.List;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.utils.ElementoImport;

/**
 * @author anco62000465 2018-09-19
 *
 */
public class ImportContactsReportLayout extends Panel implements HasLogger
{
	private static final long serialVersionUID = 6350206714629945520L;

	public ImportContactsReportLayout(	List< ElementoImport > listOk,
			List< ElementoImport > listCriados,
			List< ElementoImport > listErro,
			List< ElementoImport > listNaoModificado )
	{
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setSizeFull();
		verticalLayout.setSpacing(true);
		processList( listOk, "Contactos actualizados com sucesso: <b>" + listOk.size() + "</b>", verticalLayout );
		processList( listCriados, "Contactos criados com sucessos: <b>" + listCriados.size() + "</b>", verticalLayout );
		processList( listErro, "Contactos com erros: <font color=\"red\"><b>" + listErro.size() + "</b></font>", verticalLayout );
		processList( listNaoModificado, "Contactos sem alterações: <font color=\"red\"><b>" + listNaoModificado.size() + "</b></font>", verticalLayout );
		setContent(verticalLayout);
	}

	/**
	 * The <b>processList</b> method returns {@link void}
	 *
	 * @author anco62000465 2018-09-19
	 * @param verticalLayout
	 * @param listOk
	 * @param string
	 * @return
	 */
	private void processList( List< ElementoImport > list, String strLabelTitle, VerticalLayout verticalLayout )
	{
		if ( (list != null) && !list.isEmpty() )
		{
			Accordion accordion = new Accordion();
			accordion.setCaption( strLabelTitle );
			accordion.setCaptionAsHtml( true );
			accordion.setTabCaptionsAsHtml( true );
			for ( ElementoImport elementoImport : list )
			{
				if ( elementoImport != null )
				{
					final Label label = new Label( elementoImport.getHTMLImportContactReport(), ContentMode.HTML );
					final VerticalLayout layout = new VerticalLayout( label );
					layout.setMargin( true );
					accordion.addTab(	layout,
							String.format(	"<p><strong>%s</strong></p>",
									elementoImport.getElemento().getNome() ) );
				}
			}
			verticalLayout.addComponent(accordion);
		}

	}
}

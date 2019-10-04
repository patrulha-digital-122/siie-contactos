package scouts.cne.pt.ui.components;

import java.util.List;
import java.util.stream.Collectors;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import j2html.TagCreator;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.utils.ElementoImport;

/**
 * @author anco62000465 2018-09-19
 *
 */
public class ImportContactsReportLayout extends VerticalLayout implements HasLogger
{
	private static final long serialVersionUID = 6350206714629945520L;

	public ImportContactsReportLayout(	List< ElementoImport > listOk,
			List< ElementoImport > listCriados,
			List< ElementoImport > listErro,
			List< ElementoImport > listNaoModificado )
	{
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setSizeFull();
		verticalLayout.setSpacing( true );
		List< ElementoImport > listSemAlteracoes =
				listOk.stream().filter( p -> p.getImportContactReport().getLstLabels().isEmpty() ).collect( Collectors.toList() );
		listOk.removeAll( listSemAlteracoes );
		processList( listOk, "Contactos actualizados com sucesso: <b>" + listOk.size() + "</b>", verticalLayout );
		processList( listCriados, "Contactos criados com sucessos: <b>" + listCriados.size() + "</b>", verticalLayout );
		processErrorListWithoutChanges( listErro, "Contactos com erros: <font color=\"red\"><b>" + listErro.size() + "</b></font>", verticalLayout );
		processList(	listNaoModificado,
				"Contactos sem alterações: <font color=\"red\"><b>" + listNaoModificado.size() + "</b></font>",
				verticalLayout );
		processListWithoutChanges( listSemAlteracoes, "Contactos sem alterações: " + listSemAlteracoes.size(), verticalLayout );
		add( verticalLayout );
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
		if ( ( list != null ) && !list.isEmpty() )
		{
			// Accordion accordion = new Accordion();
			// accordion.setCaption( strLabelTitle );
			// accordion.setCaptionAsHtml( true );
			// accordion.setTabCaptionsAsHtml( true );
			// for ( ElementoImport elementoImport : list )
			// {
			// if ( (elementoImport != null) && !elementoImport.getImportContactReport().getLstLabels().isEmpty() )
			// {
			// final Label label = new Label( elementoImport.getHTMLImportContactReport(), ContentMode.HTML );
			// final VerticalLayout layout = new VerticalLayout( label );
			// layout.setMargin( true );
			// accordion.addTab( layout, String.format( "<p><strong>%s</strong></p>",
			// elementoImport.getElemento().getNome() ) );
			// }
			// }
			// verticalLayout.addComponent( accordion );
		}
	}

	private void processListWithoutChanges( List< ElementoImport > list, String strLabelTitle, VerticalLayout verticalLayout )
	{
		if ( ( list != null ) && !list.isEmpty() )
		{
			VerticalLayout panel = new VerticalLayout();
			VerticalLayout verticalLayoutWithoutChanges = new VerticalLayout();
			panel.add( verticalLayoutWithoutChanges );
			for ( ElementoImport elementoImport : list )
			{
				StringBuilder sb = new StringBuilder();
				sb.append( TagCreator.b(elementoImport.getElemento().getNome()).render() );
				sb.append( " - " );
				sb.append( elementoImport.getHTMLGoogleContactLink() );
				verticalLayoutWithoutChanges.add( new Label( sb.toString() ) );
			}
			verticalLayout.add( panel );
		}
	}

	/**
	 *
	 * The <b>processErrorListWithoutChanges</b> method returns {@link void}
	 *
	 * @author anco62000465 2018-09-20
	 * @param list
	 * @param strLabelTitle
	 * @param verticalLayout
	 */
	private void processErrorListWithoutChanges( List< ElementoImport > list, String strLabelTitle, VerticalLayout verticalLayout )
	{
		if ( ( list != null ) && !list.isEmpty() )
		{
			VerticalLayout panel = new VerticalLayout();
			VerticalLayout verticalLayoutWithoutChanges = new VerticalLayout();
			for ( ElementoImport elementoImport : list )
			{
				verticalLayoutWithoutChanges
								.add( new Label( elementoImport.getImportContactReport().getLstLabels().get( 0 ) ) );
			}
			verticalLayout.add( panel );
		}
	}
}

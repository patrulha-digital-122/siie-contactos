package scouts.cne.pt.ui.components.grids;

import com.vaadin.flow.component.combobox.ComboBox;
import scouts.cne.pt.model.siie.types.SIIESeccao;

/**
 * @author 62000465 2019-10-11
 *
 */
public class SeccaoComboBox extends ComboBox< SIIESeccao >
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3611809630961792678L;

	/**
	 * constructor
	 * 
	 * @author 62000465 2019-10-11
	 */
	public SeccaoComboBox()
	{
		super( "", SIIESeccao.values() );
		setItemLabelGenerator( SIIESeccao::getNome );
		setClearButtonVisible( true );
	}
}

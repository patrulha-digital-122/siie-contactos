package scouts.cne.pt.services;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.google.api.services.people.v1.model.ContactGroup;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.siie.types.SIIESeccao;

@Component
@VaadinSessionScope
public class GoogleContactGroupsService implements Serializable, HasLogger
{
	/**
	 *
	 */
	private static final long			serialVersionUID	= -4266591353450666223L;

	public final Map< SIIESeccao, ContactGroup >	listGroups;

	/**
	 * constructor
	 * 
	 * @author 62000465 2019-11-04
	 * @param listGroups
	 */
	public GoogleContactGroupsService()
	{
		super();
		this.listGroups = new EnumMap<>( SIIESeccao.class );
		for ( SIIESeccao siieSeccao : SIIESeccao.values() )
		{
			listGroups.put( siieSeccao, null );
		}
	}

	/**
	 * Getter for listGroups
	 * 
	 * @author 62000465 2019-11-04
	 * @return the listGroups {@link Map<SIIESeccao,ContactGroup>}
	 */
	public Map< SIIESeccao, ContactGroup > getListGroups()
	{
		return listGroups;
	}


}

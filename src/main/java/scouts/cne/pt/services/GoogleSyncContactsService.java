package scouts.cne.pt.services;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.ContactGroup;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.model.siie.types.SIIESeccao;
import scouts.cne.pt.ui.events.google.EventFinishGoogleUpdate;
import scouts.cne.pt.ui.events.google.EventStartGoogleUpdate;
import scouts.cne.pt.ui.events.google.EventUpdateSIIElemento;
import scouts.cne.pt.utils.Broadcaster;

@Component
@VaadinSessionScope
public class GoogleSyncContactsService implements Serializable, HasLogger
{
	/**
	 * 
	 */
	private static final long		serialVersionUID	= 5232615527037470159L;
	private SIIESeccao			siieSeccaoInUpdate;
	private EventFinishGoogleUpdate	eventFinishGoogleUpdate;
	private final UUID				uuid				= UUID.randomUUID();

	/**
	 * constructor
	 * 
	 * @author 62000465 2019-11-22
	 */
	public GoogleSyncContactsService()
	{
		super();
		getLogger().info( "GoogleSyncContactsService :: " + uuid.toString() );
	}

	/**
	 * Getter for siieSeccaoInUpdate
	 * 
	 * @author 62000465 2019-11-22
	 * @return the siieSeccaoInUpdate {@link SIIESeccao}
	 */
	public SIIESeccao getSiieSeccaoInUpdate()
	{
		return siieSeccaoInUpdate;
	}

	/**
	 * Setter for siieSeccaoInUpdate
	 * 
	 * @author 62000465 2019-11-22
	 * @param siieSeccaoInUpdate the siieSeccaoInUpdate to set
	 */
	public void setSiieSeccaoInUpdate( SIIESeccao siieSeccaoInUpdate )
	{
		this.siieSeccaoInUpdate = siieSeccaoInUpdate;
	}



	/**
	 * Getter for uuid
	 * 
	 * @author 62000465 2019-11-22
	 * @return the uuid {@link UUID}
	 */
	public UUID getUuid()
	{
		return uuid;
	}

	/**
	 * The <b>startAutoUpdate</b> method returns {@link void}
	 * 
	 * @author 62000465 2019-11-22
	 * @param siieSeccao
	 * @param googleAuthentication
	 * @param googleContactGroupsService
	 * @param googleContactsService
	 * @param list
	 */
	public void startAutoUpdate(	SIIESeccao siieSeccao,
									SIIEService siieService,
									GoogleAuthentication googleAuthentication,
									GoogleContactsService googleContactsService,
									GoogleContactGroupsService googleContactGroupsService )
	{
		eventFinishGoogleUpdate = new EventFinishGoogleUpdate();
		siieSeccaoInUpdate = siieSeccao;
		List< SIIEElemento > elementosActivosBySeccao = siieService.getElementosActivosBySeccao( siieSeccao );
		try
		{
			EventStartGoogleUpdate eventStartGoogleUpdate = new EventStartGoogleUpdate();
			eventStartGoogleUpdate.setBarVariant( ProgressBarVariant.LUMO_CONTRAST );
			eventStartGoogleUpdate.setMessage( "A validar grupo de contactos.." );
			eventStartGoogleUpdate.setTotal( elementosActivosBySeccao.size() );
			Broadcaster.broadcast( uuid, eventStartGoogleUpdate );
			
			ContactGroup contactGroup = googleContactGroupsService.getListGroups().get( siieSeccao );
			PeopleService peopleService = googleAuthentication.getPeopleService();
			if ( contactGroup == null )
			{
				contactGroup = googleContactGroupsService.createGroup( peopleService, siieSeccao );
			}
			int iCount = 0;
			for ( SIIEElemento siieElemento : elementosActivosBySeccao )
			{
				++iCount;
				EventUpdateSIIElemento updateSIIElemento = new EventUpdateSIIElemento();
				updateSIIElemento.setTotal( elementosActivosBySeccao.size() );
				updateSIIElemento.setActual( iCount );
				if ( siieElemento.getGooglePerson() == null )
				{
					googleContactsService.createElemento( peopleService, siieElemento );
				}
				else
				{
					updateSIIElemento.setCreate( false );
					googleContactsService.updateElemento( peopleService, siieElemento );
				}
				updateSIIElemento.setSiieElemento( siieElemento );
				Broadcaster.broadcast( uuid, updateSIIElemento );
			}
			
			if ( contactGroup != null )
			{
				googleContactGroupsService.updateGroup( peopleService, contactGroup, elementosActivosBySeccao );
			}
			
			eventFinishGoogleUpdate.setMessage( "Sincronização completa!" );
			eventFinishGoogleUpdate.setBarVariant( ProgressBarVariant.LUMO_SUCCESS );
		}
		catch ( Exception e1 )
		{
			eventFinishGoogleUpdate.setMessage( e1.getMessage() );
			eventFinishGoogleUpdate.setBarVariant( ProgressBarVariant.LUMO_ERROR );
		}
		finally
		{
			siieSeccaoInUpdate = null;

			eventFinishGoogleUpdate.finish();
			Broadcaster.broadcast( uuid, eventFinishGoogleUpdate );
		}
	}
	

}

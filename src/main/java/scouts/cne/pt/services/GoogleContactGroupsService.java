package scouts.cne.pt.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.ContactGroup;
import com.google.api.services.people.v1.model.CreateContactGroupRequest;
import com.google.api.services.people.v1.model.ListContactGroupsResponse;
import com.google.api.services.people.v1.model.ModifyContactGroupMembersRequest;
import com.google.api.services.people.v1.model.ModifyContactGroupMembersResponse;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.SIIIEImporterException;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.model.siie.types.SIIESeccao;

@Component
@VaadinSessionScope
public class GoogleContactGroupsService implements Serializable, HasLogger
{
	/**
	 *
	 */
	private static final long			serialVersionUID	= -4266591353450666223L;

	private final Map< SIIESeccao, ContactGroup >	listGroups;

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

	public ContactGroup createGroup( PeopleService peopleService, SIIESeccao siieSeccao ) throws SIIIEImporterException
	{
		try
		{
			ContactGroup newContactGroup = new ContactGroup();
			newContactGroup.setName( siieSeccao.getNome() );

			CreateContactGroupRequest contactGroupRequest = new CreateContactGroupRequest();
			contactGroupRequest.setContactGroup( newContactGroup );
			
			ContactGroup execute = peopleService.contactGroups().create( contactGroupRequest ).execute();
			listGroups.put( siieSeccao, execute );
			
			getLogger().info( "Grupo '" + siieSeccao.getNome() + "' criado com sucesso" );
			
			return execute;
		}
		catch ( Exception e1 )
		{
			getLogger().error( e1.getMessage(), e1 );
			throw new SIIIEImporterException( e1.getMessage() );
		}
	}

	public void deleteGroup( PeopleService peopleService, SIIESeccao siieSeccao ) throws SIIIEImporterException
	{
		try
		{
			peopleService.contactGroups().delete( listGroups.get( siieSeccao ).getResourceName() ).execute();
			listGroups.put( siieSeccao, null );
			getLogger().info( "Grupo '" + siieSeccao.getNome() + "' apagado com sucesso" );
		}
		catch ( Exception e1 )
		{
			getLogger().error( e1.getMessage(), e1 );
			throw new SIIIEImporterException( e1.getMessage() );
		}
	}

	/**
	 * The <b>updateAll</b> method returns {@link void}
	 * 
	 * @author 62000465 2019-11-05
	 * @param peopleService
	 * @throws SIIIEImporterException
	 */
	public void updateAll( PeopleService peopleService ) throws SIIIEImporterException
	{
		listGroups.clear();
		ListContactGroupsResponse executeContactGroups;
		try
		{
			executeContactGroups = peopleService.contactGroups().list().execute();
			for ( ContactGroup contactGroup : executeContactGroups.getContactGroups() )
			{
				for ( SIIESeccao siieSeccao : SIIESeccao.values() )
				{
					if ( siieSeccao.getNome().equals( contactGroup.getName() ) )
					{
						ContactGroup contactGroupComplete = peopleService.contactGroups().get( contactGroup.getResourceName() )
										.setMaxMembers( Optional.ofNullable( contactGroup.getMemberCount() ).orElse( 0 ) ).execute();
						listGroups.put( siieSeccao, contactGroupComplete );
					}
				}
			}
		}
		catch ( Exception e )
		{
			printError( e );
			throw new SIIIEImporterException( e.getMessage() );
		}
	}

	/**
	 * The <b>modifyGroup</b> method returns {@link void}
	 * 
	 * @author 62000465 2019-11-05
	 * @param peopleService
	 * @param contactGroup
	 * @param updateCreateElementos
	 * @throws SIIIEImporterException
	 */
	public void updateGroup( PeopleService peopleService, ContactGroup contactGroup, List< SIIEElemento > updateCreateElementos )
		throws SIIIEImporterException
	{
		try
		{
			List< String > lstElementoNoGrupo = new ArrayList<>();
			updateCreateElementos.forEach( p -> lstElementoNoGrupo.add( p.getGooglePerson().getResourceName() ) );
			ModifyContactGroupMembersRequest contactGroupMembersRequest = new ModifyContactGroupMembersRequest();
			List< String > namestoRemove = ListUtils.subtract( contactGroup.getMemberResourceNames(), lstElementoNoGrupo );
			List< String > namesToAdd = ListUtils.subtract( lstElementoNoGrupo, contactGroup.getMemberResourceNames() );
			if ( !namestoRemove.isEmpty() && !namesToAdd.isEmpty() )
			{
				contactGroupMembersRequest.setResourceNamesToRemove( namestoRemove );
				contactGroupMembersRequest.setResourceNamesToAdd( namesToAdd );
				ModifyContactGroupMembersResponse execute = peopleService.contactGroups().members()
								.modify( contactGroup.getResourceName(), contactGroupMembersRequest ).execute();
				if ( execute.getNotFoundResourceNames() != null && !execute.getNotFoundResourceNames().isEmpty() )
				{
					getLogger().error( "NotFoundResourceNames :: {}", StringUtils.join( execute.getNotFoundResourceNames() ) );
				}
			}
			else
			{
				getLogger().info( "Grupo nao precisa de ser actualizado!" );
			}
		}
		catch ( Exception e )
		{
			printError( e );
			throw new SIIIEImporterException( "Erro a actualizar os grupos. Por favor tente mais tarde :: 9008" );
		}
	}
}

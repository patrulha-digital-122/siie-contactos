package scouts.cne.pt.ui.views;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.QueryParameters;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.model.siie.SIIEElemento;
import scouts.cne.pt.model.siie.authentication.SIIEUserLoginError;
import scouts.cne.pt.services.SIIEService;
import scouts.cne.pt.ui.MainLayout;
import scouts.cne.pt.utils.UIUtils;

public abstract class HasSIIELoginUrl extends ViewFrame implements HasUrlParameter< String >, HasLogger
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -7986739356725426908L;
	private String				siieUser			= null;
	private String				siiePassword		= null;

	public boolean authenticate( SIIEService siieService, UI ui )
	{
		if ( StringUtils.isNoneEmpty( siieUser, siiePassword ) )
		{
			try
			{
				siieService.authenticateSIIE( siieUser, siiePassword );
				if ( ui != null )
				{
					ui.access( () ->
					{
						showInfo( getSiieUser() + " autenticado com sucesso" );
						Optional< SIIEElemento > elementoByNIN = siieService.getElementoByNIN( getSiieUser() );
						if ( elementoByNIN.isPresent() )
						{
							MainLayout.get().getAppBar().getAvatar()
											.setSrc( String.format( UIUtils.SIIE_IMG_PATH, elementoByNIN.get().getUploadgroup(), getSiieUser() ) );
						}
					} );
				}
				return true;
			}
			catch ( final HttpClientErrorException e )
			{
				if ( ui != null )
				{
					ui.access( () ->
					{
						final ObjectMapper mapper = new ObjectMapper();
						try
						{
							final SIIEUserLoginError userLoginError = mapper.readValue( e.getResponseBodyAsString(), SIIEUserLoginError.class );
							showError( userLoginError.getMessage() );
						}
						catch ( Exception e1 )
						{
							printError( e1 );
						}
					} );
				}
				else
				{
					printError( e );
				}
			}
			catch ( Exception e )
			{
				if ( ui != null )
				{
					ui.access( () ->
					{
						showError( e );
					} );
				}
				else
				{
					printError( e );
				}
			}
		}
		return false;
	}

	/**
	 * Getter for siieUser
	 * 
	 * @author 62000465 2019-10-16
	 * @return the siieUser {@link String}
	 */
	public String getSiieUser()
	{
		return siieUser;
	}

	@Override
	public void setParameter( BeforeEvent event, @OptionalParameter String parameter )
	{
		Location location = event.getLocation();
		QueryParameters queryParameters = location.getQueryParameters();
		Map< String, List< String > > parametersMap = queryParameters.getParameters();
		siieUser = parametersMap
						.getOrDefault( "siieUser", parametersMap.getOrDefault( "nin", parametersMap.getOrDefault( "user", Arrays.asList( "" ) ) ) )
						.get( 0 );
		siiePassword = parametersMap.getOrDefault( "siiePassword", parametersMap.getOrDefault( "password", Arrays.asList( "" ) ) ).get( 0 );
	}
}

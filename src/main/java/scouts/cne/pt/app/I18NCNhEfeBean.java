package scouts.cne.pt.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.VaadinSession;

/**
 * @author anco62000465 2018-09-11
 *
 */
@Service
public class I18NCNhEfeBean implements HasLogger, Serializable, I18NProvider
{
	private static final long	serialVersionUID	= -2854879008286919470L;

	private ResourceBundle		bundle;

	public I18NCNhEfeBean()
	{
		super();
	}

	@PostConstruct
	private void init()
	{
		refreshBundle();
	}

	public void refreshBundle()
	{
		if ( bundle == null )
		{
			try
			{
				final Locale locale = VaadinSession.getCurrent().getLocale();
				getLogger().info( "Current locale: {}", locale.toLanguageTag() );
				bundle = ResourceBundle.getBundle( "i18n/messages", locale, new UTF8Control() );
			}
			catch ( final Exception e )
			{
				getLogger().error( e.getMessage() );
				Locale localePT = new Locale( "pt", "PT" );
				bundle = ResourceBundle.getBundle( "i18n/messages", localePT, new UTF8Control() );
				getLogger().error( "Use default resource bundle " + bundle.getBaseBundleName() );
			}
		}
	}

	public String getI18NString( String key )
	{
		try
		{
			return new String( bundle.getString( key ).getBytes( "UTF-8" ), "UTF-8" );
		}
		catch ( final Exception e )
		{
			getLogger().error( e.getMessage() );
			return key;
		}
	}

	public String getI18NString( String key, Object... values )
	{
		return MessageFormat.format( getI18NString( key ), values );
	}

	public Icon getI18NIcon( String key )
	{
		try
		{
			final String i18nIconKey = getI18NString( key );
			if ( StringUtils.isNotBlank( i18nIconKey ) )
			{
				return VaadinIcon.valueOf( i18nIconKey.toUpperCase() ).create();
			}
		}
		catch ( final Exception e )
		{
			LoggerFactory.getLogger( getClass() ).error( e.getMessage() );
		}
		return null;
	}

	private class UTF8Control extends Control
	{
		@Override
		public ResourceBundle newBundle( String baseName, Locale locale, String format, ClassLoader loader, boolean reload )
			throws IllegalAccessException, InstantiationException, IOException
		{
			// The below is a copy of the default implementation.
			final String bundleName = toBundleName( baseName, locale );
			final String resourceName = toResourceName( bundleName, "properties" );
			ResourceBundle bundle = null;
			InputStream stream = null;
			if ( reload )
			{
				final URL url = loader.getResource( resourceName );
				if ( url != null )
				{
					final URLConnection connection = url.openConnection();
					if ( connection != null )
					{
						connection.setUseCaches( false );
						stream = connection.getInputStream();
					}
				}
			}
			else
			{
				stream = loader.getResourceAsStream( resourceName );
			}
			if ( stream != null )
			{
				try
				{
					// Only this line is changed to make it to read properties files as UTF-8.
					bundle = new PropertyResourceBundle( new InputStreamReader( stream, "UTF-8" ) );
				}
				finally
				{
					stream.close();
				}
			}
			return bundle;
		}
	}

	@Override
	public List< Locale > getProvidedLocales()
	{
		// TODO Auto-generated method stub
		return Arrays.asList( VaadinSession.getCurrent().getLocale() );
	}

	@Override
	public String getTranslation( String key, Locale locale, Object... params )
	{
		return getI18NString( key, params );
	}
}

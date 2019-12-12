package scouts.cne.pt.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import com.vaadin.flow.internal.JsonSerializer;
import elemental.json.Json;
import elemental.json.JsonObject;
import elemental.json.JsonValue;

/**
 * @author 62000465 2019-12-12
 *
 */
public class JsonUtils
{
	public static JsonValue convertObjectToJsonValue( Object bean )
	{
		try
		{
			JsonObject json = Json.createObject();
			BeanInfo info = Introspector.getBeanInfo( bean.getClass() );
			for ( PropertyDescriptor pd : info.getPropertyDescriptors() )
			{
				if ( "class".equals( pd.getName() ) )
				{
					continue;
				}
				Method reader = pd.getReadMethod();
				if ( reader != null )
				{
					Object invoke = reader.invoke( bean );
					if ( invoke != null )
					{
						json.put( pd.getName(), JsonSerializer.toJson( invoke ) );
					}
				}
			}
			return json;
		}
		catch ( Exception e )
		{
			throw new IllegalArgumentException( "Could not serialize object of type " + bean.getClass() + " to JsonValue", e );
		}
	}
}

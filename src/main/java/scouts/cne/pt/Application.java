package scouts.cne.pt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import scouts.cne.pt.app.security.SecurityConfiguration;

/**
 * @author anco62000465 2017-11-17
 *
 */
@SpringBootApplication( scanBasePackageClasses =
{ SecurityConfiguration.class, MainView.class, Application.class } )
public class Application extends SpringBootServletInitializer
{
	public static void main( String[] args )
	{
		SpringApplication.run( Application.class, args );
	}

	@Override
	protected SpringApplicationBuilder configure( SpringApplicationBuilder application )
	{
		return application.sources( Application.class );
	}
}

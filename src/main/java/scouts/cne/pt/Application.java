package scouts.cne.pt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author anco62000465 2017-11-17
 *
 */
@SpringBootApplication
public class Application
{

	/**
	 * The main method makes it possible to run the application as a plain Java application which starts embedded web
	 * server via Spring Boot.
	 *
	 * @param args command line parameters
	 */
	public static void main( String[] args )
	{
		SpringApplication.run( Application.class, args );
	}

}

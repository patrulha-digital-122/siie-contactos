package scouts.cne.pt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author anco62000465 2018-09-17
 *
 */
public class TestData
{
	/**
	 * The <b>main</b> method returns {@link void}
	 * @author anco62000465 2018-09-17
	 * @param args 
	 */
	public static void main( String[] args )
	{
		try
		{
			String dataNascimento = "17/09/1967";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
			Date parse = simpleDateFormat.parse( dataNascimento );
			System.out.println( parse.toString() );
		}
		catch ( ParseException e )
		{
			e.printStackTrace();
		}
	}
}

package scouts.cne.pt;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import scouts.cne.pt.google.GoogleServerAuthenticationBean;

/**
 * @author anco62000465 2018-09-21
 *
 */
public class TestSheet
{
	/**
	 * The <b>main</b> method returns {@link void}
	 * @author anco62000465 2018-09-21
	 * @param args 
	 */
	public static void main( String[] args )
	{
		final String spreadsheetId = "1vJ8o-7JkmgpYvtd-Xx3t3ycJGbtY2iv0e4eVUpRzrQ4";
		final String range = "A1:E";
		GoogleServerAuthenticationBean googleServerAuthentication = new GoogleServerAuthenticationBean();
		try
		{
			Sheets service = googleServerAuthentication.getSheetsService();
			ValueRange response = service.spreadsheets().values().get( spreadsheetId, range ).execute();
			List< List< Object > > values = response.getValues();
			if ( values == null || values.isEmpty() )
			{
				System.out.println( "No data found." );
			}
			else
			{
				System.out.println( "Name, Major" );
				for ( List row : values )
				{
					// Print columns A and E, which correspond to indices 0 and 4.
					System.out.printf( "%s, %s\n", row.get( 0 ), row.get( 4 ) );
				}
			}
		}
		catch ( GeneralSecurityException | IOException e )
		{
			e.printStackTrace();
		}
	}
}

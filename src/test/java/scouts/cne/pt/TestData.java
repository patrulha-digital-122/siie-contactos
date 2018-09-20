package scouts.cne.pt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import j2html.TagCreator;
import j2html.tags.ContainerTag;
import scouts.cne.pt.utils.ContactUtils;

/**
 * @author anco62000465 2018-09-17
 *
 */
public class TestData {
	/**
	 * The <b>main</b> method returns {@link void}
	 *
	 * @author anco62000465 2018-09-17
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String dataNascimento = "17/09/1967";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			Date parse = simpleDateFormat.parse(dataNascimento);
			System.out.println(parse.toString());

			System.out.println(ContactUtils.convertPhoneNumber("91 888 00 44"));
			String[] split = "yo/base/2a30d5f80dde0e8f".split( "/base/" );
			ContainerTag p = TagCreator
							.p( TagCreator.join(	TagCreator.text( "Link para Google Contacts: " ),
													TagCreator.a( "versão antiga" )
																	.withHref( String.format(	"https://www.google.com/contacts/?cplus=0#contact/%s",
																								split[ 1 ] ) )
																	.withTarget( "_blank" ),
													TagCreator.text( " | " ),
													TagCreator.a( "versão nova" )
																	.withHref( String.format( "https://contacts.google.com/contact/%s", split[ 1 ] ) )
																	.withTarget( "_blank" ) ) );
			System.out.println( p.render() );
			System.out.println( p.renderFormatted() );
			ContainerTag join = TagCreator.p( TagCreator.join(	TagCreator.text( "Código do erro: " ),
																TagCreator.b( "1" ),
																TagCreator.text( " | Motivo: " ),
																TagCreator.b( "Teste" ) ) );
			System.out.println( join.render() );
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}

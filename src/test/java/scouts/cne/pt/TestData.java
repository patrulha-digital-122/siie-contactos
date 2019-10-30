package scouts.cne.pt;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import j2html.TagCreator;
import j2html.tags.ContainerTag;
import scouts.cne.pt.model.google.GoogleAuthInfo;
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
			
			
			String json = "{\"El\":\"116262509726366688679\",\"Zi\":{\"token_type\":\"Bearer\",\"access_token\":\"ya29.ImqpB38VMAVO9z0ClZ1jfeo9sC1CNIGjt3n3SeKn9xZeaSt54CLmhv-XEN3FSTcit_fzZDPc61JxG8k_tEBQONmuqizDJljsOlBcg3GImWVYzvi_QHVv7d73Z9igQONMCZpNkl0ATjogOzAS\",\"scope\":\"email profile https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/gmail.send openid https://www.googleapis.com/auth/contacts\",\"login_hint\":\"AJDLj6JUa8yxXrhHdWRHIV0S13cA1v7KM5WessURKVUDG65Gf4t94VxTRBR5PwNeLKyOb7_wxWAkIoq3DkazMhACHklUHASByg\",\"expires_in\":3600,\"id_token\":\"eyJhbGciOiJSUzI1NiIsImtpZCI6IjhhNjNmZTcxZTUzMDY3NTI0Y2JiYzZhM2E1ODQ2M2IzODY0YzA3ODciLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXpwIjoiMTQ0MTYxMjc4MTAxLXA5bjJrM3JrMWk2Nmp0NmQ4MGM5dmUwY2tkZjI1YTUzLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiYXVkIjoiMTQ0MTYxMjc4MTAxLXA5bjJrM3JrMWk2Nmp0NmQ4MGM5dmUwY2tkZjI1YTUzLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMTE2MjYyNTA5NzI2MzY2Njg4Njc5IiwiZW1haWwiOiJhbmRyZS5jb25yYWRvLjBAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImF0X2hhc2giOiJVQnYwNWV0NnZMU2E0X0xYa3NIT2p3IiwibmFtZSI6IkFuZHLDqSBBbnTDs25pbyBTYW50b3MgQW50dW5lcyBDb25yYWRvIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hLS9BQXVFN21CLW1UbmVZVkNhSmFvaGpWb3JqeDgyQkZCUk1PaHFtOWpiMTV2LU9BPXM5Ni1jIiwiZ2l2ZW5fbmFtZSI6IkFuZHLDqSBBbnTDs25pbyBTYW50b3MiLCJmYW1pbHlfbmFtZSI6IkFudHVuZXMgQ29ucmFkbyIsImxvY2FsZSI6InB0LVBUIiwiaWF0IjoxNTcyMjg2NjY4LCJleHAiOjE1NzIyOTAyNjgsImp0aSI6IjYzZDdmZjdmMWE3ODJjYWE2NTg0OWQ4OWZkZDYwM2RjMmVhZTRjNTcifQ.YuYWYPGBGc1U-XFG_5PEeY6aVfTN1j80TYmulgFEsVBn8LUvk55z1MoBrmytqMtK3QNLs-mnaBXB7hOU-oYMtpWiDTqRrTCYuMGbOdM6dbwY2Vaurerz6FStjyPBRPmbrutwFXRbTm4UKGbLYAFOrt1VV1kbPedEUP4WciLljAora82xBzZEbrNtC1YFjN3Ua4SbJLvnTmzGTK6u6tSMY5dMHnA9L1d3KfxoTW4OTpBgmcoOTaZSNkEv9XxTwyDml4cqt6D6VJ_fYGOBZyeqm-AORPJOwdc3U_66z6EgSvQ2x7yTTsiSJmDFkkYNQDoHr31_mgncPivdvXicNcpL3A\",\"session_state\":{\"extraQueryParams\":{\"authuser\":\"0\"}},\"first_issued_at\":1.572286668354E12,\"expires_at\":1.572290268354E12,\"idpId\":\"google\"},\"w3\":{\"Eea\":\"116262509726366688679\",\"ig\":\"André António Santos Antunes Conrado\",\"ofa\":\"André António Santos\",\"wea\":\"Antunes Conrado\",\"Paa\":\"https://lh3.googleusercontent.com/a-/AAuE7mB-mTneYVCaJaohjVorjx82BFBRMOhqm9jb15v-OA=s96-c\",\"U3\":\"andre.conrado.0@gmail.com\"}}";
			
			final ObjectMapper mapper = new ObjectMapper();
			final GoogleAuthInfo googleAuthInfo = mapper.readValue( json, GoogleAuthInfo.class );
			
			System.out.println( googleAuthInfo.getGoogleAcessInfo().getExpires_at() );
			
			
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
		catch ( JsonParseException e )
		{
			e.printStackTrace();
		}
		catch ( JsonMappingException e )
		{
			e.printStackTrace();
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
	}
}

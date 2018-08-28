package scouts.cne.pt.listeners;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import com.vaadin.ui.Upload.Receiver;
import scouts.cne.pt.services.SIIEService;

public class FileUploader implements Receiver
{
	/**
	 *
	 */
	private static final long serialVersionUID = 3751542749493613243L;
	private File file;

	private SIIEService siieService;
	private OutputStream		outputStream		= null;

	public FileUploader(SIIEService siieService) {
		super();
		this.siieService = siieService;
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {

		System.out.println( "New file: " + filename );
		try {
			file = File.createTempFile("stuff", ".xlsx");

			outputStream = new FileOutputStream(file);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outputStream;
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		try
		{
			outputStream.close();
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		return file;
	}

}

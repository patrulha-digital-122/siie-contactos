package scouts.cne.pt.listeners;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import scouts.cne.pt.MyUI;
import scouts.cne.pt.services.SIIEService;

public class FileUploader implements Receiver, SucceededListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 3751542749493613243L;
	private File file;
	MyUI myUI;

	private SIIEService siieService;

	public FileUploader(MyUI myUI, SIIEService siieService) {
		super();
		this.myUI = myUI;
		this.siieService = siieService;
	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {
		this.siieService.setFile(file);
		this.myUI.showMenus();
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		OutputStream outputStream = null;
		try {
			file = File.createTempFile("stuff", ".xlsx");

			outputStream = new FileOutputStream(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outputStream;
	}
}

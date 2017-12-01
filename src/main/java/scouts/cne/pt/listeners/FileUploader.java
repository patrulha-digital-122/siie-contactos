package scouts.cne.pt.listeners;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

public class FileUploader implements Receiver, SucceededListener
{
	/**
	 *
	 */
	private static final long	serialVersionUID	= 3751542749493613243L;
	public File file;

	public FileUploader( File file )
	{
		super();
		this.file = file;
	}

	@Override
	public void uploadSucceeded( SucceededEvent event )
	{
		Notification.show( "Upload finalizado: " + event.getMIMEType(), Notification.Type.HUMANIZED_MESSAGE );
	}
	@Override
	public OutputStream receiveUpload( String filename, String mimeType )
	{
		OutputStream outputStream = null;
		try
		{

			outputStream = new FileOutputStream( file );
		}
		catch ( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outputStream;
	}
}

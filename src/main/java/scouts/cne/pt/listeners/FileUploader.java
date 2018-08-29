package scouts.cne.pt.listeners;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import scouts.cne.pt.app.HasLogger;
import scouts.cne.pt.services.SIIEService;

public class FileUploader implements Receiver, HasLogger, StartedListener, FailedListener
{
	/**
	 *
	 */
	private static final long	serialVersionUID	= 3751542749493613243L;
	private File				file;
	private SIIEService			siieService;
	private FileOutputStream	fileOutputStream;

	public FileUploader( SIIEService siieService )
	{
		super();
		this.siieService = siieService;
	}

	@Override
	public OutputStream receiveUpload( String filename, String mimeType )
	{
		getLogger().info( "New file: {} | {}", filename, mimeType );
		try
		{
			file = File.createTempFile( "stuff", ".xlsx" );
			fileOutputStream = new FileOutputStream( file );
		}
		catch ( Exception e )
		{
			getLogger().error( e.getMessage(), e );
		}
		return fileOutputStream;
	}

	/**
	 * @return the file
	 */
	public File getFile()
	{
		try
		{
			fileOutputStream.flush();
			fileOutputStream.close();
		}
		catch ( IOException e )
		{
			getLogger().error( e.getMessage(), e );
		}
		return file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.ui.Upload.StartedListener#uploadStarted(com.vaadin.ui.Upload.StartedEvent)
	 */
	@Override
	public void uploadStarted( StartedEvent event )
	{
		getLogger().info( "Start upload '{}' - {}", event.getFilename(), event.getContentLength() );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.ui.Upload.FailedListener#uploadFailed(com.vaadin.ui.Upload.FailedEvent)
	 */
	@Override
	public void uploadFailed( FailedEvent event )
	{
		getLogger().error( event.getReason().getMessage() );
	}
}

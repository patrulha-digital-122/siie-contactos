package scouts.cne.pt.ui.events;

import java.time.Duration;
import java.time.Instant;

/**
 * @author 62000465 2019-11-22
 *
 */
public abstract class InternalEvent
{
	final Instant	init		= Instant.now();
	Duration		duration	= Duration.ZERO;

	public void finish()
	{
		duration = Duration.between( init, Instant.now() );
	}

	public String getDescription()
	{
		return "InternalEvent";
	}

	/**
	 * Getter for duration
	 * 
	 * @author 62000465 2019-11-22
	 * @return the duration {@link Duration}
	 */
	public Duration getDuration()
	{
		return duration;
	}
}

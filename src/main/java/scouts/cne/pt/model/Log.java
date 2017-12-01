package scouts.cne.pt.model;

import java.time.Instant;

public class Log {

	public String message;
	private Instant instant;

	public Log(String message) {
		super();
		this.message = message;
		this.instant = Instant.now();
	}

	public String getMessage() {
		return message;
	}

	public Instant getInstant() {
		return instant;
	}

}

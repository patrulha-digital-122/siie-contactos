package scouts.cne.pt.google;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import scouts.cne.pt.model.Log;

public class FirebaseManager {

	protected static FirebaseManager instance = null;

	private DatabaseReference dbLogs;

	public static FirebaseManager getInstance() {

		if (instance == null) {
			instance = new FirebaseManager();
		}
		return instance;

	}

	private FirebaseManager() {
		super();
		if (FirebaseApp.getApps().isEmpty()) {

			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream in = classLoader.getResourceAsStream("firebase.json");

			FirebaseOptions options;
			try {
				options = new FirebaseOptions.Builder().setCredential(FirebaseCredentials.fromCertificate(in))
						.setDatabaseUrl("https://cnhefe-122.firebaseio.com").build();

				FirebaseApp.initializeApp(options);
				//FirebaseDatabase.getInstance().setLogLevel(Level.DEBUG);

				dbLogs = FirebaseDatabase.getInstance().getReference("logs");

				dbLogs.addValueEventListener(new ValueEventListener() {

					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						// TODO Auto-generated method stub
						System.out.println("Data Change: " + dataSnapshot.getValue());
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {
						// TODO Auto-generated method stub
						System.out.println("Listener was cancelled");
					}
				});

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void addLogMessage(String strMessage) {
		dbLogs.setValue(new Log(strMessage));
		System.out.println(Instant.now().toString() + " -> " + dbLogs.getKey());
	}

}

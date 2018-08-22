package scouts.cne.pt.google;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import scouts.cne.pt.model.GoogleCode;
import scouts.cne.pt.model.Log;

public class FirebaseManager {

	protected static FirebaseManager instance = null;

	private DatabaseReference dbLogs;
	private DatabaseReference dbCodes;
	private Map<Integer, GoogleCode> mapIdsCode = new HashMap<>();

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
			InputStream in = classLoader.getResourceAsStream("cnhefe-122-firebase.json");

			FirebaseOptions options;
			try {
				options = new FirebaseOptions.Builder().setCredential(FirebaseCredentials.fromCertificate(in))
						.setDatabaseUrl("https://cnhefe-122.firebaseio.com").build();

				FirebaseApp.initializeApp(options);
				// FirebaseDatabase.getInstance().setLogLevel(Level.DEBUG);

				dbLogs = FirebaseDatabase.getInstance().getReference("logs");
				dbCodes = FirebaseDatabase.getInstance().getReference("codes");
				dbCodes.addListenerForSingleValueEvent(new ValueEventListener() {

					@Override
					public void onDataChange(DataSnapshot snapshot) {

						GoogleCode googleCode = snapshot.getValue(GoogleCode.class);
						System.out.println("get google code:" + googleCode);
						mapIdsCode.put(googleCode.getId(), googleCode);
					}

					@Override
					public void onCancelled(DatabaseError error) {
						// TODO Auto-generated method stub

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

	public void addCode(int i, String googleId, String refreshToken) {
		GoogleCode googleCode = new GoogleCode();
		googleCode.setCode(googleId);
		googleCode.setId(i);
		googleCode.setRefreshToken(refreshToken);
		dbCodes.child(""+i).setValue(googleCode);
		mapIdsCode.put(i, googleCode);
		addLogMessage("New code from " + i);
	}

	public void deleteCode(String embedId) {

	}

	public GoogleCode getCode(int i) {

		return mapIdsCode.get(i);
	}

}

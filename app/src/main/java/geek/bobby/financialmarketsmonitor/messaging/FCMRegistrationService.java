package geek.bobby.financialmarketsmonitor.messaging;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import geek.bobby.financialmarketsmonitor.R;
import geek.bobby.financialmarketsmonitor.messaging.models.RegistrationResult;
import geek.bobby.financialmarketsmonitor.tools.AppStorage;
import geek.bobby.financialmarketsmonitor.tools.NotificationGenerator;

/**
 * It is a foreground service which will show an ongoing notification while performing
 * its operations
 */
public class FCMRegistrationService extends Service {

    public static final String ActionRetrieveAndSubmitRegistrationId = "messaging.ActionRetrieveAndSubmitRegistrationId";

    // An instance of RegistrationResult
    public static final String ExtraResult = "messaging.Result";

    private static final String TAG = "MessageHandlerService";

    private static final int ongoingNotificationID = 123456;

    private NotificationGenerator notificationGenerator;

    @Override
    public void onCreate() {
        final String notificationChannel = "FCM-Registration";
        super.onCreate();
        notificationGenerator = new NotificationGenerator(this,
                notificationChannel,
                getString(R.string.FCM_registration_channel_name),
                getString(R.string.FCM_registration_channel_description),
                NotificationManagerCompat.IMPORTANCE_LOW);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent!=null && ActionRetrieveAndSubmitRegistrationId.equals(intent.getAction()))
        {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser!=null) {
                startForeground(ongoingNotificationID, notificationGenerator.create(
                        getString(R.string.FCM_registration_channel_name),
                        getString(R.string.FCM_registration_operation),
                        System.currentTimeMillis(),
                        null, true));
                retrieveAndSubmitRegistrationID(currentUser.getUid(),
                        currentUser.getDisplayName()!=null ? currentUser.getDisplayName() : currentUser.getEmail());
                return START_STICKY;
            }
        }

        return START_NOT_STICKY;
    }

    private void retrieveAndSubmitRegistrationID(final @NonNull String UID,
                                                 final @Nullable String accountName)
    {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.d(TAG, "FCM token is "+token);
                        //stopMe(RegistrationResult.forSuccess());
                        if (token!=null)
                            submitRegistrationIDForUser(UID, accountName, token);
                    }
                })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    stopMe(RegistrationResult.forFailure(e));
                }
            });
    }

    private void submitRegistrationIDForUser(final @NonNull String UID,
                                             final @Nullable String accountName,
                                             @NonNull String token)
    {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tokenRef = database.getReference("/users/" +UID+"/token");
        tokenRef.setValue(token)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        DatabaseReference nameRef = database.getReference("/users/" +UID+"/account");
                        nameRef.setValue(accountName)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        new AppStorage(FCMRegistrationService.this).setIsUserRegistered(true);

                                        stopMe(RegistrationResult.forSuccess());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        e.printStackTrace();
                                        stopMe(RegistrationResult.forFailure(e));
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        stopMe(RegistrationResult.forFailure(e));
                    }
                });
    }

    private void stopMe(@NonNull RegistrationResult registrationResult)
    {
        stopForeground(true);
        NotificationManagerCompat.from(this).cancel(ongoingNotificationID);
        LocalBroadcastManager.getInstance(FCMRegistrationService.this)
                .sendBroadcast(new Intent(ActionRetrieveAndSubmitRegistrationId)
                    .putExtra(ExtraResult, registrationResult));
        stopSelf();
    }
}

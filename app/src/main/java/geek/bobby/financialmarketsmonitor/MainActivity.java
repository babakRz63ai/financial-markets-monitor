package geek.bobby.financialmarketsmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import geek.bobby.financialmarketsmonitor.login.LoginActivity;
import geek.bobby.financialmarketsmonitor.messaging.FCMRegistrationService;
import geek.bobby.financialmarketsmonitor.messaging.MessagingContract;
import geek.bobby.financialmarketsmonitor.messaging.models.RegistrationResult;
import geek.bobby.financialmarketsmonitor.messaging.models.SymbolRate;
import geek.bobby.financialmarketsmonitor.tools.AppStorage;


public class MainActivity extends AppCompatActivity {

    //private final static String TAG = "MainActivity";

    final static int ReqLogin = 100;

    private FirebaseAuth mAuth;

    private AppStorage appStorage;

    private MyMessagesAdapter messagesAdapter;

    private Snackbar progressSnackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icv_menu_dark);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        appStorage = new AppStorage(this);

        messagesAdapter = new MyMessagesAdapter(getLayoutInflater());

        progressSnackBar = Snackbar.make(findViewById(R.id.mainContainer),
                R.string.registration_status_in_progress,
                Snackbar.LENGTH_INDEFINITE);

        ListView listView = findViewById(R.id.listView);
        listView.setEmptyView(findViewById(R.id.txtEmptyView));
        listView.setAdapter(messagesAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(@Nullable FirebaseUser currentUser)
    {
        if (currentUser==null)
            startActivityForResult(new Intent(this, LoginActivity.class),
                    ReqLogin);
        else
        {
            NavigationView navigationView = findViewById(R.id.nav_view);
            TextView nameText = navigationView.getHeaderView(0).findViewById(R.id.userName);
            nameText.setText(currentUser.getDisplayName());
            TextView emailText = navigationView.getHeaderView(0).findViewById(R.id.userEmail);
            emailText.setText(currentUser.getEmail());

            if (appStorage.isUserRegistered())
                indicateSuccessfulRegistration();
            else {
                startServiceToRegisterUserAndDevice();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==ReqLogin) {
            if (resultCode!=RESULT_OK)
                finish();
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    private final BroadcastReceiver localBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent!=null && intent.getAction()!=null) {
                switch (intent.getAction()) {
                    case FCMRegistrationService.ActionRetrieveAndSubmitRegistrationId:
                        RegistrationResult registrationResult = (RegistrationResult) intent.getSerializableExtra(FCMRegistrationService.ExtraResult);
                        if (registrationResult != null) {
                            if (registrationResult.isSuccessful())
                                indicateSuccessfulRegistration();
                            else
                                indicateFailedRegistration(registrationResult.getErrorMessage());
                        }
                        break;

                    case MessagingContract.BroadcastActions.SymbolAndRate:
                        SymbolRate symbolRate = (SymbolRate)intent.getSerializableExtra(MessagingContract.ExtraSymbolRate);
                        if (symbolRate!=null) {
                            messagesAdapter.addItem(symbolRate);
                            android.net.Uri defNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            if (defNotification!=null) {
                                MediaPlayer mediaPlayer = MediaPlayer.create(context, defNotification);
                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        mediaPlayer.release();
                                    }
                                });
                                mediaPlayer.start();
                            }
                        }
                        break;
                }
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(MessagingContract.BroadcastActions.SymbolAndRate);

        if (appStorage.isUserRegistered())
            indicateSuccessfulRegistration();
        else
            intentFilter.addAction(FCMRegistrationService.ActionRetrieveAndSubmitRegistrationId);

        LocalBroadcastManager.getInstance(this).registerReceiver(localBroadcastReceiver,
                intentFilter);
    }

    private void startServiceToRegisterUserAndDevice()
    {
        indicateWaitingForRegistration();
        startService(new Intent(this, FCMRegistrationService.class)
                .setAction(FCMRegistrationService.ActionRetrieveAndSubmitRegistrationId));
    }

    private void indicateSuccessfulRegistration()
    {
        progressSnackBar.dismiss();
        Snackbar.make(findViewById(R.id.mainContainer),
                R.string.registration_status_registered,
                Snackbar.LENGTH_LONG).show();
    }

    private void indicateFailedRegistration(@Nullable String message)
    {
        progressSnackBar.dismiss();
        new AlertDialog.Builder(this)
                .setMessage(message!=null ? message : getString(R.string.registration_status_failure_default))
                .setPositiveButton(R.string.register_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startServiceToRegisterUserAndDevice();
                    }
                })
                .show();
    }

    private void indicateWaitingForRegistration()
    {
        progressSnackBar.show();
    }

    //==============================================
    static class MyMessagesAdapter extends BaseAdapter
    {
        @NonNull
        private final LayoutInflater layoutInflater;

        private final List<SymbolRate> items;

        private final SimpleDateFormat dateFormat;

        MyMessagesAdapter(@NonNull LayoutInflater layoutInflater) {
            this.layoutInflater = layoutInflater;
            this.dateFormat = new SimpleDateFormat("dd MMMMM yyyy - HH:mm",
                    Locale.US);
            this.items = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {

            SymbolRate model = items.get(i);

            if (convertView==null)
                convertView = layoutInflater.inflate(R.layout.symbol_rate_item,
                        viewGroup, false);

            TextView title = convertView.findViewById(R.id.txtTitle);
            title.setText(model.getSymbol());

            TextView rate = convertView.findViewById(R.id.txtSecondary);
            rate.setText(Float.toString(model.getRate()));

            TextView timeStamp = convertView.findViewById(R.id.txtTimeStamp);
            timeStamp.setText(dateFormat.format(model.getTimeStamp()));

            return convertView;
        }

        public void addItem(@NonNull SymbolRate symbolRate)
        {
            items.add(symbolRate);
            notifyDataSetChanged();
        }
    }
}
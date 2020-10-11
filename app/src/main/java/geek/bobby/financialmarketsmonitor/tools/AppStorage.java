package geek.bobby.financialmarketsmonitor.tools;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class AppStorage {
    @NonNull
    private final SharedPreferences preferences;

    // Boolean
    private static final String KeyIsUserRegistered = "isUserRegistered";

    public AppStorage(@NonNull Context context)
    {
        final String fileName = "AppStorage";
        preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    public boolean isUserRegistered()
    {
        return preferences.getBoolean(KeyIsUserRegistered, false);
    }

    public void setIsUserRegistered(boolean registered)
    {
        preferences.edit()
                .putBoolean(KeyIsUserRegistered, registered)
                .apply();
    }
}

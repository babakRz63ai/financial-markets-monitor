package geek.bobby.financialmarketsmonitor.messaging.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;

public class RegistrationResult implements Serializable
{
    @NonNull
    public static RegistrationResult forSuccess()
    {
        return new RegistrationResult(true, null);
    }

    @NonNull public static RegistrationResult forFailure(@NonNull Exception exception)
    {
        if (exception.getMessage()!=null)
            return new RegistrationResult(false, exception.getMessage());
        else
            return new RegistrationResult(false, exception.getClass().getName());
    }

    private final boolean isSuccessful;

    private final String errorMessage;

    private RegistrationResult(boolean isSuccessful,
                               @Nullable String errorMessage)
    {
        this.isSuccessful = isSuccessful;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }
}

package geek.bobby.financialmarketsmonitor.messaging.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;

public class PlainTextMessage implements Serializable {
    @Nullable
    private final String title;

    @NonNull
    private final String body;

    public PlainTextMessage(@Nullable String title, @NonNull String body) {
        this.title = title;
        this.body = body;
    }


    @Nullable
    public String getTitle() {
        return title;
    }

    @NonNull
    public String getBody() {
        return body;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        return super.equals(other) ||
                other instanceof PlainTextMessage &&
                        this.body.equals(((PlainTextMessage) other).body) &&
                        (this.title==null && ((PlainTextMessage) other).title==null ||
                                this.title!=null && this.title.equals(((PlainTextMessage) other).title));
    }
}

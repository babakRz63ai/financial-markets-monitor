package geek.bobby.financialmarketsmonitor.messaging.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

public class SymbolRate implements Serializable {
    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("rate")
    private float rate;

    @JsonProperty("timeStamp")
    private Date timeStamp;

    // We need this constructor for parsing JSON and ORMLite database
    SymbolRate() {}

    public SymbolRate(@NonNull String symbol,
                      float rate,
                      @NonNull Date date)
    {
        this.symbol = symbol;
        this.rate = rate;
        this.timeStamp = date;
    }

    public String getSymbol()
    {
        return symbol;
    }

    public float getRate() {
        return rate;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        return super.equals(other) ||
                other instanceof SymbolRate &&
                        rate == ((SymbolRate) other).rate &&
                        symbol!=null && symbol.equals(((SymbolRate) other).symbol) &&
                        timeStamp!=null && timeStamp.equals(((SymbolRate) other).timeStamp);
    }
}

package jawa.sinaukoding.sk.model.response;

import java.math.BigInteger;
import java.sql.Timestamp;

public class AuctionResponse {
    private String code;
    private String name;
    private String description;
    private BigInteger minimumPrice;
    private BigInteger maximumPrice;
    private Timestamp startedAt;
    private Timestamp endedAt;

  
    public AuctionResponse(String code, String name, String description, BigInteger minimumPrice, BigInteger maximumPrice, Timestamp startedAt, Timestamp endedAt) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.minimumPrice = minimumPrice;
        this.maximumPrice = maximumPrice;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigInteger getMinimumPrice() {
        return minimumPrice;
    }

    public void setMinimumPrice(BigInteger minimumPrice) {
        this.minimumPrice = minimumPrice;
    }

    public BigInteger getMaximumPrice() {
        return maximumPrice;
    }

    public void setMaximumPrice(BigInteger maximumPrice) {
        this.maximumPrice = maximumPrice;
    }

    public Timestamp getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Timestamp startedAt) {
        this.startedAt = startedAt;
    }

    public Timestamp getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Timestamp endedAt) {
        this.endedAt = endedAt;
    }
}

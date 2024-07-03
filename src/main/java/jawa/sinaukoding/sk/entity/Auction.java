package jawa.sinaukoding.sk.entity;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.OffsetDateTime;

public record Auction(Long id, //
                      String code, //
                      String name, //
                      String description, //
                      BigInteger offer, //
                      BigInteger highestBid, //
                      Long highestBidderId,
                      String highestBidderName, //
                      Status status, //
                      OffsetDateTime startedAt, //
                      OffsetDateTime endedAt, //
                      Long createdBy, //
                      Long updatedBy, //
                      Long deletedBy, //
                      OffsetDateTime createdAt, //
                      OffsetDateTime updatedAt, //
                      OffsetDateTime deletedAt //
) {

    public static final String TABLE_NAME = "auction";

    public PreparedStatement insert(final Connection connection) {
        try {
            // TODO: INSERT
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public enum Status {
        WAITING_FOR_APPROVAL, APPROVED, REJECTED, CLOSED
    }
}

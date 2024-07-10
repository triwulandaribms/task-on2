package jawa.sinaukoding.sk.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.OffsetDateTime;

public record AuctionBid(Long id, //
                         Long auctionId, //
                         Long bid, //
                         Long bidder, //
                         OffsetDateTime createdAt //
                          ) {

    public static final String TABLE_NAME = "sk_auction_bid";
    public PreparedStatement insert(final Connection connection) {
        try {
            final String sql = "INSERT INTO " + TABLE_NAME + " (name, email, password, role, created_by, created_at) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
            final PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, auctionId);
            ps.setLong(2, bid);
            ps.setString(3, String.valueOf(bidder));
            ps.setTimestamp(4, Timestamp.valueOf(createdAt.atZoneSameInstant(OffsetDateTime.now().getOffset()).toLocalDateTime()));
            return ps;
        } catch (Exception e) {
            return null;
        }
    }
}

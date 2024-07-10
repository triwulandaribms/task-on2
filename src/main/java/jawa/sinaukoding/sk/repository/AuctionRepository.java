package jawa.sinaukoding.sk.repository;

import jawa.sinaukoding.sk.entity.Auction;
import jawa.sinaukoding.sk.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;
import java.util.Optional;

@Repository
public class AuctionRepository {
    private static final Logger log = LoggerFactory.getLogger(AuctionRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public AuctionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Auction> findById(final Long id) {
        if (id == null || id < 0) {
            System.out.println("Invalid ID: " + id);
            return Optional.empty();
        }

        System.out.println("Finding auction with ID: " + id);

        try {
            return Optional.ofNullable(jdbcTemplate.query(con -> {
                final PreparedStatement ps = con.prepareStatement("SELECT * FROM " + "sk_auction"+ " WHERE id = ?");
                ps.setLong(1, id);

                return ps;


            }, rs -> {
                if (!rs.next()) {
                    System.out.println("No auction found with ID: " + id);
                    log.info("No auction found with ID: {}", id);
                    return null;
                }

                System.out.println("CEK ISI  : "+rs.getLong("id"));
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnsNumber = rsmd.getColumnCount();
                System.out.println("Columns in ResultSet:");
                for (int i = 1; i <= columnsNumber; i++) {
                    System.out.println(rsmd.getColumnName(i) + ": " + rs.getObject(i));
                }

                // Log all columns to see what the result set contains


                // Process the result set
                final String code = rs.getString("code");
                final String name = rs.getString("name");
                final String description = rs.getString("description");
                final BigInteger offer = rs.getBigDecimal("offer").toBigInteger();
                final BigInteger highestBid = rs.getBigDecimal("highest_bid").toBigInteger();
                final Long highestBidderId = rs.getLong("highest_bidder_id");
                final String highestBidderName = rs.getString("hignest_bidder_name");
                final Auction.Status status = Auction.Status.valueOf(rs.getString("status"));

//                final OffsetDateTime startedAt = rs.getTimestamp("started_at").toInstant().atOffset(ZoneOffset.UTC);
//                final OffsetDateTime endedAt = rs.getTimestamp("ended_at").toInstant().atOffset(ZoneOffset.UTC);
                final String startedAtStr = rs.getString("started_at");
                final OffsetDateTime startedAt = startedAtStr != null ?
                        LocalDateTime.parse(startedAtStr.replace("T", " ").replace("Z", ""),
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                                .atOffset(ZoneOffset.UTC) : null;
                final String endedAtStr = rs.getString("ended_at");
                final OffsetDateTime endedAt = endedAtStr != null ?
                        LocalDateTime.parse(endedAtStr.replace("T", " ").replace("Z", ""),
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                                .atOffset(ZoneOffset.UTC) : null;

                final Long createdBy = rs.getLong("created_by");
                final Long updatedBy = rs.getLong("updated_by");
                final Long deletedBy = rs.getLong("deleted_by");
                final OffsetDateTime createdAt = rs.getTimestamp("created_at") == null ? null : rs.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC);
                final OffsetDateTime updatedAt = rs.getTimestamp("updated_at") == null ? null : rs.getTimestamp("updated_at").toInstant().atOffset(ZoneOffset.UTC);
                final OffsetDateTime deletedAt = rs.getTimestamp("deleted_at") == null ? null : rs.getTimestamp("deleted_at").toInstant().atOffset(ZoneOffset.UTC);

                return new Auction(id, code, name, description, offer, highestBid, highestBidderId, highestBidderName, status, startedAt, endedAt, createdBy, updatedBy, deletedBy, createdAt, updatedAt, deletedAt);
            }));
        } catch (Exception e) {
            System.err.println("Error finding auction with ID: " + id);
            e.printStackTrace();
            return Optional.empty();
        }
    }


    public long autionRejected(Long auctionId) {
        if (jdbcTemplate.update(con -> {
            final PreparedStatement ps = con.prepareStatement("UPDATE " + Auction.TABLE_NAME + " SET status=? WHERE id=?");
            ps.setString(1, String.valueOf(Auction.Status.REJECTED));
            ps.setLong(2, auctionId);
            return ps;
        }) > 0) {
            return auctionId;
        } else {
            return 0L;
        }
    }

}

package jawa.sinaukoding.sk.repository;

import java.sql.PreparedStatement;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import jawa.sinaukoding.sk.entity.Auction;

@Repository
public class AuctionRepo {
    private static final Logger log = LoggerFactory.getLogger(AuctionRepo.class);

    private final JdbcTemplate jdbcTemplate;

    public AuctionRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long saveAuction(Auction auction) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            if (jdbcTemplate.update(con -> Objects.requireNonNull(auction.insert(con)), keyHolder) != 1) {
                return 0L;
            } else {
                return Objects.requireNonNull(keyHolder.getKey()).longValue();
            }
        } catch (Exception e) {
            log.error("{}", e);
            return 0L;
        }
    }

    public Optional<Auction> findById(Long id) {
        if (id == null || id < 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(jdbcTemplate.query(con -> {
            final PreparedStatement ps = con.prepareStatement("SELECT * FROM " + Auction.TABLE_NAME + " WHERE id = ?");
            ps.setLong(1, id);
            return ps;
        }, rs -> {
            if (!rs.next() || rs.getLong("id") <= 0) {
                return null;
            }
            final String code = rs.getString("code");
            final String name = rs.getString("name");
            final String description = rs.getString("description");
            final int offer = rs.getInt("offer");
            final int highestBid = rs.getInt("highest_bid");
            final Long highestBidderId = rs.getLong("highest_bidder_id");
            final String highestBidderName = rs.getString("highest_bidder_name");
            final Auction.Status status = Auction.Status.valueOf(rs.getString("status"));
            final OffsetDateTime startedAt = rs.getTimestamp("started_at").toInstant().atOffset(ZoneOffset.UTC);
            final OffsetDateTime endedAt = rs.getTimestamp("ended_at").toInstant().atOffset(ZoneOffset.UTC);
            final Long createdBy = rs.getLong("created_by");
            final Long updatedBy = rs.getLong("updated_by");
            final Long deletedBy = rs.getLong("deleted_by");
            final OffsetDateTime createdAt = rs.getTimestamp("created_at") == null ? null : rs.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC);
            final OffsetDateTime updatedAt = rs.getTimestamp("updated_at") == null ? null : rs.getTimestamp("updated_at").toInstant().atOffset(ZoneOffset.UTC);
            final OffsetDateTime deletedAt = rs.getTimestamp("deleted_at") == null ? null : rs.getTimestamp("deleted_at").toInstant().atOffset(ZoneOffset.UTC);
            return new Auction(id, code, name, description, offer, highestBid, highestBidderId, highestBidderName, status, startedAt, endedAt, createdBy, updatedBy, deletedBy, createdAt, updatedAt, deletedAt);
        }));
    }

    public Long updateAuctionStatus(Long id, Auction.Status status) {
        try {
            return (long) jdbcTemplate.update(con -> {
                final PreparedStatement ps = con.prepareStatement(
                        "UPDATE " + Auction.TABLE_NAME + " SET status = ?, updated_at = ? WHERE id = ?");
                ps.setString(1, status.toString());
                ps.setObject(2, OffsetDateTime.now(ZoneOffset.UTC));
                ps.setLong(3, id);
                return ps;
            });
        } catch (Exception e) {
            log.error("Failed to update auction status: {}", e.getMessage());
            return 0L;
        }
    }

    public void save(Auction auction) {
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }
}

package jawa.sinaukoding.sk.repository;

import jawa.sinaukoding.sk.entity.Auction;
import jawa.sinaukoding.sk.entity.AuctionBid;
import jawa.sinaukoding.sk.entity.User;
import jawa.sinaukoding.sk.exception.CustomeException;
import jawa.sinaukoding.sk.model.Authentication;
import jawa.sinaukoding.sk.model.Page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class AuctionRepository {
    private static final Logger log = LoggerFactory.getLogger(AuctionRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public AuctionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // public List<Auction> listAuction(int page, int size) {
    // final int offset = (page - 1) * size;
    // final String sql = "SELECT * FROM %s LIMIT ? OFFSET
    // ?".formatted(Auction.TABLE_NAME);
    // return jdbcTemplate.query(sql, new Object[]{size, offset}, (rs, rowNum) ->
    // new Auction(
    // rs.getLong("id"),
    // rs.getString("code"),
    // rs.getString("name"),
    // rs.getString("description"),
    // rs.getLong("offer"),
    // rs.getLong("highest_bid"),
    // rs.getLong("highest_bidder_id"),
    // rs.getString("hignest_bidder_name"),
    // Auction.Status.valueOf(rs.getString("status")),
    // rs.getTimestamp("started_at").toInstant().atOffset(ZoneOffset.UTC),
    // rs.getTimestamp("ended_at").toInstant().atOffset(ZoneOffset.UTC),
    // rs.getLong("created_by"),
    // rs.getLong("updated_by"),
    // rs.getLong("deleted_by"),
    // rs.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC),
    // rs.getTimestamp("updated_at") != null ?
    // rs.getTimestamp("updated_at").toInstant().atOffset(ZoneOffset.UTC) : null,
    // rs.getTimestamp("deleted_at") != null ?
    // rs.getTimestamp("deleted_at").toInstant().atOffset(ZoneOffset.UTC) : null
    // ));
    // }

    
    
    public Page<Auction> listAuctions(int page, int size, String status) {
        try {
            final int offset = (page - 1) * size;
            final String sql = "SELECT  * FROM %s WHERE status = ? AND deleted_at IS NULL ORDER BY id LIMIT ? OFFSET ?" .formatted(Auction.TABLE_NAME);
    
            final String countSql = "SELECT COUNT(id) AS total_data FROM %s WHERE status = ? AND deleted_at IS NULL".formatted(Auction.TABLE_NAME);
            
            final Long totalData = jdbcTemplate.queryForObject(countSql, Long.class,  new Object[]{status});
            final Long totalPage = (totalData / size) + ((totalData % size == 0) ? 0 : 1);
    
           
            final List<Auction> auctions = jdbcTemplate.query(sql, new RowMapper<Auction>() {
                @Override
                public Auction mapRow(ResultSet rs, int rowNum) throws SQLException {
                    final Auction.Status status = Auction.Status.valueOf(rs.getString("status"));
                    final String startedAt = rs.getString("started_at");
                    final String endedAt = rs.getString("ended_at");
                    final String createdAt = rs.getString("created_at");
                    final String updatedAt = rs.getString("updated_at");
                    final String deletedAt = rs.getString("deleted_at");
    
                    return new Auction(
                            rs.getLong("id"),
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getLong("offer"),
                            rs.getLong("highest_bid"),
                            rs.getLong("highest_bidder_id"),
                            rs.getString("hignest_bidder_name"),
                            status,
                            startedAt == null ? null : OffsetDateTime.parse(startedAt),
                            endedAt == null ? null : OffsetDateTime.parse(endedAt),
                            rs.getLong("created_by"),
                            rs.getLong("updated_by"),
                            rs.getLong("deleted_by"),
                            createdAt == null ? null : OffsetDateTime.parse(createdAt),
                            updatedAt == null ? null : OffsetDateTime.parse(updatedAt),
                            deletedAt == null ? null : OffsetDateTime.parse(deletedAt));
                }

            },new Object[]{status, size, offset});
    
            return new Page<>(totalData, totalPage, page, size, auctions);
        } catch (Exception e) {
            log.error("Terjadi kesalahan saat mengambil data lelang: {}", e.getMessage());
        }
        return null;
    }
    
    public Page<Auction> listAuctionsBuyer(int page, int size, String status){
        try {
            final int offset = (page - 1) * size;
            final String sql = "SELECT  * FROM %s WHERE status = ? AND deleted_at IS NULL ORDER BY id LIMIT ? OFFSET ?" .formatted(Auction.TABLE_NAME);
    
            final String countSql = "SELECT COUNT(id) AS total_data FROM %s WHERE status = ? AND deleted_at IS NULL".formatted(Auction.TABLE_NAME);
            
            final Long totalData = jdbcTemplate.queryForObject(countSql, Long.class,  new Object[]{status});
            final Long totalPage = (totalData / size) + ((totalData % size == 0) ? 0 : 1);
    
           
            final List<Auction> auctions = jdbcTemplate.query(sql, new RowMapper<Auction>() {
                @Override
                public Auction mapRow(ResultSet rs, int rowNum) throws SQLException {
                    final Auction.Status status = Auction.Status.valueOf(rs.getString("status"));
                    final String startedAt = rs.getString("started_at");
                    final String endedAt = rs.getString("ended_at");
                    final String createdAt = rs.getString("created_at");
                    final String updatedAt = rs.getString("updated_at");
                    final String deletedAt = rs.getString("deleted_at");
    
                    return new Auction(
                            rs.getLong("id"),
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getLong("offer"),
                            rs.getLong("highest_bid"),
                            rs.getLong("highest_bidder_id"),
                            rs.getString("hignest_bidder_name"),
                            status,
                            startedAt == null ? null : OffsetDateTime.parse(startedAt),
                            endedAt == null ? null : OffsetDateTime.parse(endedAt),
                            rs.getLong("created_by"),
                            rs.getLong("updated_by"),
                            rs.getLong("deleted_by"),
                            createdAt == null ? null : OffsetDateTime.parse(createdAt),
                            updatedAt == null ? null : OffsetDateTime.parse(updatedAt),
                            deletedAt == null ? null : OffsetDateTime.parse(deletedAt));
                }

            },new Object[]{status, size, offset});
    
            return new Page<>(totalData, totalPage, page, size, auctions);
        } catch (Exception e) {
            log.error("Terjadi kesalahan saat mengambil data lelang: {}", e.getMessage());
        }
        return null;
    }

    public Long saveAuction(final Auction auction) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            if (jdbcTemplate.update(con -> Objects.requireNonNull(auction.insert(con)),
                    keyHolder) != 1) {
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
            final PreparedStatement ps = con.prepareStatement("SELECT * FROM " +
                    Auction.TABLE_NAME + " WHERE id = ?");
            ps.setLong(1, id);
            return ps;
        }, rs -> {
            if (!rs.next() || rs.getLong("id") <= 0) {
                return null;
            }
            final String code = rs.getString("code");
            final String name = rs.getString("name");
            final String description = rs.getString("description");
            final Long offer = rs.getLong("offer");
            final Long highestBid = rs.getLong("highest_bid");
            final Long highestBidderId = rs.getLong("highest_bidder_id");
            final String highestBidderName = rs.getString("hignest_bidder_name");
            final Auction.Status status = Auction.Status.valueOf(rs.getString("status"));
            final OffsetDateTime startedAt = rs.getTimestamp("started_at").toInstant().atOffset(ZoneOffset.UTC);
            final OffsetDateTime endedAt = rs.getTimestamp("ended_at").toInstant().atOffset(ZoneOffset.UTC);
            final Long createdBy = rs.getLong("created_by");
            final Long updatedBy = rs.getLong("updated_by");
            final Long deletedBy = rs.getLong("deleted_by");
            final OffsetDateTime createdAt = rs.getTimestamp("created_at") == null ? null
                    : rs.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC);
            final OffsetDateTime updatedAt = rs.getTimestamp("updated_at") == null ? null
                    : rs.getTimestamp("updated_at").toInstant().atOffset(ZoneOffset.UTC);
            final OffsetDateTime deletedAt = rs.getTimestamp("deleted_at") == null ? null
                    : rs.getTimestamp("deleted_at").toInstant().atOffset(ZoneOffset.UTC);
            return new Auction(id, code, name, description, offer, highestBid,
                    highestBidderId, highestBidderName, status, startedAt, endedAt, createdBy,
                    updatedBy, deletedBy, createdAt, updatedAt, deletedAt);
        }));
    }

    public Long updateAuctionStatus(Long authId, Long id, Auction.Status status) {
        try {
            return (long) jdbcTemplate.update(con -> {
                final PreparedStatement ps = con.prepareStatement(
                        "UPDATE " + Auction.TABLE_NAME
                                + " SET status = ?,updated_by=?, updated_at = CURRENT_TIMESTAMP WHERE id = ?");
                ps.setString(1, status.toString());
                ps.setObject(2, authId);
                ps.setLong(3, id);
                return ps;
            });
        } catch (Exception e) {
            log.error("Gagal update status Auction: {}", e.getMessage());
            return 0L;
        }
    }

    public long saveAuctionBid(final AuctionBid auctionBid) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            if (jdbcTemplate.update(con -> Objects.requireNonNull(auctionBid.insert(con)), keyHolder) != 1) {
                // return 0L;
                throw new RuntimeException("Gagal");
            } else {
                return Objects.requireNonNull(keyHolder.getKey()).longValue();
            }
        } catch (Exception e) {
            log.error("{}", e);
            throw new RuntimeException(e);
        }
    }

    public void updateAuction(long auctionId, long highestBid, long highestBidderId, String highestBidderName) {
        String sql = "UPDATE " + Auction.TABLE_NAME
                + " SET highest_bid=?, highest_bidder_id=?, hignest_bidder_name=? WHERE id=?";
        try {
            int rowsUpdated = jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setLong(1, highestBid);
                ps.setLong(2, highestBidderId);
                ps.setString(3, highestBidderName);
                ps.setLong(4, auctionId);
                return ps;
            });

            if (rowsUpdated != 1) {
                throw new RuntimeException("Gagal update Auction dengan Id: " + auctionId);
            }
        } catch (DataAccessException e) {
            throw new RuntimeException("Error update Auction dengan Id: " + auctionId, e);
        }
    }

    public void save(Auction auction) {
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

   }

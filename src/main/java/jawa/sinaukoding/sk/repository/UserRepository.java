package jawa.sinaukoding.sk.repository;

import jawa.sinaukoding.sk.entity.User;
import jawa.sinaukoding.sk.model.Authentication;
import jawa.sinaukoding.sk.model.request.RegisterBuyerReq;
import jawa.sinaukoding.sk.model.request.RegisterSellerReq;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
    }

    public List<User> listUsers(final Authentication authentication, int page, int size) {
        final String sql = "SELECT * FROM %s".formatted(User.TABLE_NAME);
        final List<User> users = jdbcTemplate.query(sql, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                final User.Role role = User.Role.fromString(rs.getString("role"));
                final Timestamp createdAt = rs.getTimestamp("created_at");
                final Timestamp updatedAt = rs.getTimestamp("updated_at");
                final Timestamp deletedAt = rs.getTimestamp("deleted_at");
                return new User(rs.getLong("id"), //
                        rs.getString("name"), //
                        rs.getString("email"), //
                        rs.getString("password"), //
                        role, //
                        rs.getLong("created_by"), //
                        rs.getLong("updated_by"), //
                        rs.getLong("deleted_by"), //
                        createdAt == null ? null : createdAt.toInstant().atOffset(ZoneOffset.UTC), //
                        updatedAt == null ? null : updatedAt.toInstant().atOffset(ZoneOffset.UTC), //
                        deletedAt == null ? null : deletedAt.toInstant().atOffset(ZoneOffset.UTC)); //
            }
        });
        return users;
    }

    public Long saveSeller(final Authentication authentication, final RegisterSellerReq req, String encodedPassword) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final User user = new User( //
                null, //
                req.name(), //
                req.email(), //
                encodedPassword, //
                User.Role.SELLER, //
                authentication.id(), //
                null, //
                null, //
                OffsetDateTime.now(), //
                null, //
                null //
        );
        try {
            if (jdbcTemplate.update(con -> Objects.requireNonNull(user.insert(con)), keyHolder) != 1) {
                return 0L;
            } else {
                return Objects.requireNonNull(keyHolder.getKey()).longValue();
            }
        } catch (Exception e) {
            return 0L;
        }
    }

    public Long saveBuyer(final Authentication authentication, final RegisterBuyerReq req, String encodedPassword) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final User user = new User( //
                null, //
                req.name(), //
                req.email(), //
                encodedPassword, //
                User.Role.BUYER, //
                authentication.id(), //
                null, //
                null, //
                OffsetDateTime.now(), //
                null, //
                null //
        );
        try {
            if (jdbcTemplate.update(con -> Objects.requireNonNull(user.insert(con)), keyHolder) != 1) {
                return 0L;
            } else {
                return Objects.requireNonNull(keyHolder.getKey()).longValue();
            }
        } catch (Exception e) {
            return 0L;
        }
    }

    public Long updatePassword(final Authentication authentication, String newPassword) {
        if (jdbcTemplate.update(con -> {
            final PreparedStatement ps = con.prepareStatement("UPDATE sys_user SET password=?");
            ps.setString(1, newPassword);
            return ps;
        }) > 0) {
            return authentication.id();
        } else {
            return 0L;
        }
    }

    public Optional<User> findById(final Long id) {
        if (id == null || id < 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(jdbcTemplate.query(con -> {
            final PreparedStatement ps = con.prepareStatement("SELECT * FROM sys_user WHERE id=?");
            ps.setLong(1, id);
            return ps;
        }, rs -> {
            final String name = rs.getString("name");
            final String email = rs.getString("email");
            final String password = rs.getString("password");
            final User.Role role = User.Role.valueOf(rs.getString("role"));
            final Long createdBy = rs.getLong("created_by");
            final Long updatedBy = rs.getLong("updated_by");
            final Long deletedBy = rs.getLong("deleted_by");
            final OffsetDateTime createdAt = rs.getTimestamp("created_at") == null ? null : rs.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC);
            final OffsetDateTime updatedAt = rs.getTimestamp("updated_at") == null ? null : rs.getTimestamp("updated_at").toInstant().atOffset(ZoneOffset.UTC);
            final OffsetDateTime deletedAt = rs.getTimestamp("deleted_at") == null ? null : rs.getTimestamp("deleted_at").toInstant().atOffset(ZoneOffset.UTC);
            return new User(id, name, email, password, role, createdBy, updatedBy, deletedBy, createdAt, updatedAt, deletedAt);
        }));
    }

    public Optional<User> findByEmail(final String email) {
        if (email == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(jdbcTemplate.query(con -> {
            final PreparedStatement ps = con.prepareStatement("SELECT * FROM sys_user WHERE email=?");
            ps.setString(1, email);
            return ps;
        }, rs -> {
            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final String password = rs.getString("password");
            final User.Role role = User.Role.valueOf(rs.getString("role"));
            final Long createdBy = rs.getLong("created_by");
            final Long updatedBy = rs.getLong("updated_by");
            final Long deletedBy = rs.getLong("deleted_by");
            final OffsetDateTime createdAt = rs.getTimestamp("created_at") == null ? null : rs.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.UTC);
            final OffsetDateTime updatedAt = rs.getTimestamp("updated_at") == null ? null : rs.getTimestamp("updated_at").toInstant().atOffset(ZoneOffset.UTC);
            final OffsetDateTime deletedAt = rs.getTimestamp("deleted_at") == null ? null : rs.getTimestamp("deleted_at").toInstant().atOffset(ZoneOffset.UTC);
            return new User(id, name, email, password, role, createdBy, updatedBy, deletedBy, createdAt, updatedAt, deletedAt);
        }));
    }
}

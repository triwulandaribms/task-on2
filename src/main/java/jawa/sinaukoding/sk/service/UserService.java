package jawa.sinaukoding.sk.service;


import jawa.sinaukoding.sk.entity.User;
import jawa.sinaukoding.sk.model.Authentication;
import jawa.sinaukoding.sk.model.request.LoginReq;
import jawa.sinaukoding.sk.model.request.RegisterBuyerReq;
import jawa.sinaukoding.sk.model.request.RegisterSellerReq;
import jawa.sinaukoding.sk.model.Response;
import jawa.sinaukoding.sk.model.request.ResetPasswordReq;
import jawa.sinaukoding.sk.model.response.UserDto;
import jawa.sinaukoding.sk.repository.UserRepository;
import jawa.sinaukoding.sk.util.HexUtils;
import jawa.sinaukoding.sk.util.JwtUtils;
import jawa.sinaukoding.sk.util.SecurityContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public final class UserService extends AbstractService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final byte[] jwtKey;

    public UserService(final Environment env, final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        final String skJwtKey = env.getProperty("sk.jwt.key");
        assert skJwtKey != null;
        this.jwtKey = HexUtils.hexToBytes(skJwtKey);
    }

    public Response<Object> listUsers(final Authentication authentication, final int page, final int size) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            if (page < 0 || size <= 0) {
                return Response.badRequest();
            }
            final List<UserDto> users = userRepository.listUsers(authentication, page, size) //
                    .stream().map(user -> new UserDto(user.name())).toList();
            return Response.create("09", "00", "Sukses", users);
        });
    }

    public Response<Object> registerSeller(final Authentication authentication, final RegisterSellerReq req) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            if (req == null) {
                return Response.badRequest();
            }
            final String encoded = passwordEncoder.encode(req.password());
            final Long saved = userRepository.saveSeller(authentication, req, encoded);
            if (saved == null || 0L == saved) {
                return Response.create("05", "01", "Gagal mendaftarkan seller", null);
            }
            return Response.create("05", "00", "Sukses", saved);
        });
    }

    public Response<Object> registerBuyer(final Authentication authentication, final RegisterBuyerReq req) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            if (req == null) {
                return Response.badRequest();
            }
            final String encoded = passwordEncoder.encode(req.password());
            final Long saved = userRepository.saveBuyer(authentication, req, encoded);
            if (saved == null || 0L == saved) {
                return Response.create("06", "01", "Gagal mendaftarkan buyer", null);
            }
            return Response.create("06", "00", "Sukses", saved);
        });
    }

    public Response<Object> resetPassword(final Authentication authentication, final ResetPasswordReq req) {
        return precondition(authentication).orElseGet(() -> {
            if (req == null) {
                return Response.badRequest();
            }
            final Long userId = authentication.id();
            final Optional<User> userOp = userRepository.findById(userId);
            if (userOp.isEmpty()) {
                return Response.create("07", "01", "Partisipan tidak ditemukan", null);
            }
            if (authentication.role() != User.Role.ADMIN) {
                final User u = userOp.get();
                if (!Objects.equals(u.password(), req.currentPassword())) {
                    return Response.create("07", "02", "Password tidak sama", null);
                }
            }
            final Long saved = userRepository.updatePassword(authentication, req.newPassword());
            if (saved == null || 0L == saved) {
                return Response.create("07", "03", "Gagal menubah kata sandi", null);
            }
            return Response.create("07", "00", "Sukses", saved);
        });
    }

    public Response<Object> login(final LoginReq req) {
        if (req == null) {
            return Response.badRequest();
        }
        final Optional<User> userOpt = userRepository.findByEmail(req.email());
        if (userOpt.isEmpty()) {
            return Response.create("08", "01", "Email atau password salah", null);
        }
        final User user = userOpt.get();
        if (!passwordEncoder.matches(req.password(), user.password())) {
            return Response.create("08", "01", "Email atau password salah", null);
        }
        final Authentication authentication = new Authentication(user.id(), user.role(), true);
        final long iat = System.currentTimeMillis();
        final long exp = 1000 * 60 * 60 * 24; // 24 hour
        final JwtUtils.Header header = new JwtUtils.Header() //
                .add("typ", "JWT") //
                .add("alg", "HS256"); //
        final JwtUtils.Payload payload = new JwtUtils.Payload() //
                .add("sub", authentication.id()) //
                .add("role", user.role().name()) //
                .add("iat", iat) //
                .add("exp", exp); //
        final String token = JwtUtils.hs256Tokenize(header, payload, jwtKey);
        return Response.create("08", "00", "Sukses", token);
    }
}

package jawa.sinaukoding.sk.service;

import ch.qos.logback.core.net.SyslogOutputStream;
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
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
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
        this.jwtKey = HexUtils.hexToBytes(skJwtKey);
    }

    public Response<Object> listUsers(final Authentication authentication, final int page, final int size) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            if (page <= 0 || size <= 0) {
                return Response.badRequest();
            }
            final List<UserDto> users = userRepository.listUsers(page, size) //
                    .stream().map(user -> new UserDto(user.name(),user.role())).toList();
            return Response.create("09", "00", "Sukses", users);
        });
    }

    public Response<Object> registerSeller(final Authentication authentication, final RegisterSellerReq req) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            if (req == null) {
                return Response.badRequest();
            }
            final String encoded = passwordEncoder.encode(req.password());
            final User user = new User( //
                    null, //
                    req.name(), //
                    req.email(), //
                    encoded, //
                    User.Role.SELLER, //
                    authentication.id(), //
                    null, //
                    null, //
                    OffsetDateTime.now(), //
                    null, //
                    null //
            );
            final Long saved = userRepository.saveSeller(user);
            if (0L == saved) {
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
            final User user = new User( //
                    null, //
                    req.name(), //
                    req.email(), //
                    encoded, //
                    User.Role.BUYER, //
                    authentication.id(), //
                    null, //
                    null, //
                    OffsetDateTime.now(), //
                    null, //
                    null //
            );
            final Long saved = userRepository.saveBuyer(user);
            if (0L == saved) {
                return Response.create("06", "01", "Gagal mendaftarkan buyer", null);
            }
            return Response.create("06", "00", "Sukses", saved);
        });
    }

    public Response<Object> login(final LoginReq req) {
        if (req == null) {
            return Response.badRequest();
        }
        final Optional<User> userOpt = userRepository.findByEmail(req.email());
        if (userOpt.isEmpty()) {
            return Response.create("08", "01", "Email  salah", null);
        }
        final User user = userOpt.get();
        if (!passwordEncoder.matches(req.password(), user.password())) {
            return Response.create("08", "02", "password salah", null);
        }
        final Authentication authentication = new Authentication(user.id(), user.role(), true);
        System.out.println("auth : "+authentication);
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


    public Response<Object> resetPassword(final Authentication authentication, final ResetPasswordReq req){
        System.out.println("Authh : "+authentication);

        if (req.newPassword() == null || req.newPassword().isEmpty()) {
            return Response.create("07", "03", "New password must not be empty", null);
        }
        if (req.newPassword().length() < 8) {
            return Response.create("07", "03", "New password must be at least 8 characters long", null);
        }

        try {
            Optional<User> userOpt = userRepository.findById(authentication.id());
            if (userOpt.isEmpty()) {
                return Response.create("07", "01", "User not found", null);
            }

            User user = userOpt.get();
            if (passwordEncoder.matches(req.newPassword(),user.password())){
                return Response.create("07", "03", "Old password and new password are same. Please use a new password", null);
            }

            if (passwordEncoder.matches(req.newPassword(),user.password())){
                return Response.create("07", "03", "was Deleted", null);
            }
            String newEncodedPassword = passwordEncoder.encode(req.newPassword());
            long updatedUserId = userRepository.resetPassword(user.id(), newEncodedPassword);
            if (updatedUserId == 0L) {
                return Response.create("07", "02", "Failed to update password", null);
            }
            if (user.deletedBy() != null || user.deletedAt()!=null) {
                return Response.create("07", "04", "User account has been deleted", null);
            }
            UserDto userDto = new UserDto(user.name(),user.role());
            return Response.create("07", "00", "Success to update password", userDto);
        } catch (Exception e) {
            return Response.create("07", "02", "Invalid Token", null);
        }
    }

}

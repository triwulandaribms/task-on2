package jawa.sinaukoding.sk.service;

import jawa.sinaukoding.sk.entity.User;
import jawa.sinaukoding.sk.model.Authentication;
import jawa.sinaukoding.sk.model.Page;
import jawa.sinaukoding.sk.model.request.LoginReq;
import jawa.sinaukoding.sk.model.request.RegisterBuyerReq;
import jawa.sinaukoding.sk.model.request.RegisterSellerReq;
import jawa.sinaukoding.sk.model.request.UpdateProfileReq;
import jawa.sinaukoding.sk.model.Response;
import jawa.sinaukoding.sk.model.request.ResetPasswordReq;
import jawa.sinaukoding.sk.model.response.UserDto;
import jawa.sinaukoding.sk.repository.UserRepository;
import jawa.sinaukoding.sk.util.HexUtils;
import jawa.sinaukoding.sk.util.JwtUtils;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public final class UserService extends AbstractService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final byte[] jwtKey;

    public UserService(final Environment env, final UserRepository userRepository,
            final PasswordEncoder passwordEncoder) {
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
            Pageable pageable = PageRequest.of(page - 1, size);
            org.springframework.data.domain.Page<User> userPage = userRepository.findAll(pageable);

            List<UserDto> users = userPage.getContent()
                    .stream()
                    .map(user -> new UserDto(user.getId(), user.getName()))
                    .collect(Collectors.toList());

            Page<UserDto> p = new Page<>(userPage.getTotalElements(), userPage.getTotalPages(), page, size, users);
            return Response.create("09", "00", "Sukses", p);
        });
    }

    public Response<Object> registerSeller(final Authentication authentication, final RegisterSellerReq req) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            if (req == null) {
                return Response.badRequest();
            }
            final String encoded = passwordEncoder.encode(req.password());
            final User user = new User();
            user.setName(req.name());
            user.setEmail(req.email());
            user.setPassword(encoded);
            user.setRole(User.Role.SELLER);
            user.setCreatedBy(authentication.id());
            user.setCreatedAt(OffsetDateTime.now());

            User saved = userRepository.save(user);
            if (saved == null || saved.getId() == null) {
                return Response.create("05", "01", "Gagal mendaftarkan seller", null);
            }
            return Response.create("05", "00", "Sukses", saved.getId());
        });
    }

    public Response<Object> registerBuyer(final Authentication authentication, final RegisterBuyerReq req) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            if (req == null) {
                return Response.badRequest();
            }
            final String encoded = passwordEncoder.encode(req.password());
            final User user = new User();
            user.setName(req.name());
            user.setEmail(req.email());
            user.setPassword(encoded);
            user.setRole(User.Role.BUYER);
            user.setCreatedBy(authentication.id());
            user.setCreatedAt(OffsetDateTime.now());

            User saved = userRepository.save(user);
            if (saved == null || saved.getId() == null) {
                return Response.create("06", "01", "Gagal mendaftarkan buyer", null);
            }
            return Response.create("06", "00", "Sukses", saved.getId());
        });
    }

    public Response<Object> login(final LoginReq req) {
        if (req == null) {
            return Response.badRequest();
        }
        final Optional<User> userOpt = userRepository.findByEmail(req.email());
        if (userOpt.isEmpty()) {
            return Response.create("08", "01", "Email salah", null);
        }
        final User user = userOpt.get();
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            return Response.create("08", "02", "Password salah", null);
        }
        final Authentication authentication = new Authentication(user.getId(), user.getRole(), true);
        final long iat = System.currentTimeMillis();
        final long exp = 1000 * 60 * 60 * 24; // 24 hour
        final JwtUtils.Header header = new JwtUtils.Header()
                .add("typ", "JWT")
                .add("alg", "HS256");
        final JwtUtils.Payload payload = new JwtUtils.Payload()
                .add("sub", authentication.id())
                .add("role", user.getRole().name())
                .add("iat", iat)
                .add("exp", exp);
        final String token = JwtUtils.hs256Tokenize(header, payload, jwtKey);
        return Response.create("08", "00", "Sukses", token);
    }

    public Response<Object> resetPassword(final Authentication authentication, final ResetPasswordReq req) {
        if (req.newPassword() == null || req.newPassword().isEmpty()) {
            return Response.create("07", "01", "Password baru tidak boleh kosong", null);
        }
        if (req.newPassword().length() < 8) {
            return Response.create("07", "02", "Password baru harus memiliki minimal 8 karakter", null);
        }

        try {
            Optional<User> userOpt = userRepository.findById(authentication.id());
            if (userOpt.isEmpty()) {
                return Response.create("07", "03", "Pengguna tidak ditemukan", null);
            }

            User user = userOpt.get();
            if (passwordEncoder.matches(req.newPassword(), user.getPassword())) {
                return Response.create("07", "04", "Password lama dan password baru sama. Silakan gunakan password yang berbeda",
                        null);
            }

            if ((user.getDeletedBy() != null && user.getDeletedBy() != 0) || user.getDeletedAt() != null) {
                return Response.create("07", "05", "Akun pengguna telah dihapus", null);
            }

            user.setPassword(passwordEncoder.encode(req.newPassword()));
            userRepository.save(user);

            UserDto userDto = new UserDto(user.getId(), user.getName());
            return Response.create("07", "00", "Password berhasil diperbarui", userDto);
        } catch (Exception e) {
            return Response.create("07", "07", "Token tidak valid", null);
        }
    }

    public Response<Object> deletedUser(Authentication authentication, Long userId) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            Optional<User> userOpt = userRepository.findById(userId);

            if (userOpt.isEmpty()) {
                return Response.create("10", "02", "ID tidak ditemukan", null);
            }

            User dataUser = userOpt.get();

            if (dataUser.getDeletedAt() != null) {
                return Response.create("10", "03", "Data sudah dihapus", null);
            }

            dataUser.setDeletedBy(authentication.id());
            dataUser.setDeletedAt(OffsetDateTime.now());

            User saved = userRepository.save(dataUser);
            if (saved != null && saved.getDeletedAt() != null) {
                return Response.create("10", "00", "Berhasil hapus data", null);
            } else {
                return Response.create("10", "01", "Gagal hapus data", null);
            }
        });
    }

    public Response<Object> updateProfile(final Authentication authentication, final UpdateProfileReq req) {
        return precondition(authentication, User.Role.ADMIN, User.Role.BUYER, User.Role.SELLER).orElseGet(() -> {
            Optional<User> userOpt = userRepository.findById(authentication.id());
            if (userOpt.isEmpty()) {
                return Response.create("07", "01", "User tidak ditemukan", null);
            }
            User user = userOpt.get();

            user.setName(req.name());
            user.setUpdatedAt(OffsetDateTime.now());

            User saved = userRepository.save(user);
            if (saved != null) {
                return Response.create("07", "00", "Profil berhasil diupdate", null);
            } else {
                return Response.create("07", "02", "Gagal mengupdate profil", null);
            }
        });
    }


}

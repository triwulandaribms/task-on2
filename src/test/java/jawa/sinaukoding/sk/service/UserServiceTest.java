package jawa.sinaukoding.sk.service;

import jawa.sinaukoding.sk.entity.User;
import jawa.sinaukoding.sk.model.Authentication;
import jawa.sinaukoding.sk.model.request.RegisterSellerReq;
import jawa.sinaukoding.sk.model.Response;
import jawa.sinaukoding.sk.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.OffsetDateTime;
import java.util.Optional;

@SpringBootTest
class UserServiceTest {

    private static final User ADMIN = new User(1L, //
            "ADMIN", //
            "ADMIN@EXAMPLE.com", //
            "PASSWORD", //
            User.Role.ADMIN, //
            0L, //
            null, //
            null, //
            OffsetDateTime.now(), //
            null, //
            null); //

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    void findAdmin() {
        Mockito.when(userRepository.findById(ArgumentMatchers.eq(1L))).thenReturn(Optional.of(ADMIN));
        Mockito.when(userRepository.findById(ArgumentMatchers.eq(2L))).thenReturn(Optional.of(new User( //
                2L, //
                "Charlie", //
                "charlie", //
                "alice", //
                User.Role.SELLER, //
                ADMIN.id(), //
                null, //
                null, //
                OffsetDateTime.now(), //
                null, //
                null //
        )));
    }

    @Test
    void registerSeller() {
        final RegisterSellerReq req = new RegisterSellerReq("Charlie", "charlie", "alice");
        Mockito.when(userRepository.saveSeller(ArgumentMatchers.any(), ArgumentMatchers.eq(req), ArgumentMatchers.anyString())).thenReturn(2L);
        final User admin = userRepository.findById(1L).orElseThrow();
        final Authentication authentication = new Authentication(admin.id(), admin.role(), true);
        final Response<Object> response = userService.registerSeller(authentication, req);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("0500", response.code());
        Assertions.assertEquals("Sukses", response.message());
        Assertions.assertEquals(2L, response.data());
    }

    @Test
    void registerSellerUnauthenticated() {
        final User admin = userRepository.findById(1L).orElseThrow();
        final Authentication authentication = new Authentication(admin.id(), admin.role(), false);
        final Response<Object> response1 = userService.registerSeller(authentication, new RegisterSellerReq("Charlie", "charlie", "alice"));
        Assertions.assertNotNull(response1);
        Assertions.assertEquals("0101", response1.code());
        Assertions.assertEquals("unauthenticated", response1.message());
        Assertions.assertNull(response1.data());
        //
        final Response<Object> response2 = userService.registerSeller(null, new RegisterSellerReq("Charlie", "charlie", "alice"));
        Assertions.assertNotNull(response2);
        Assertions.assertEquals("0101", response2.code());
        Assertions.assertEquals("unauthenticated", response2.message());
        Assertions.assertNull(response2.data());
    }

    @Test
    void registerSellerUnauthorized() {
        final User seller = userRepository.findById(2L).orElseThrow();
        final Authentication authentication = new Authentication(seller.id(), seller.role(), true);
        final Response<Object> response1 = userService.registerSeller(authentication, new RegisterSellerReq("Charlie", "charlie", "alice"));
        Assertions.assertNotNull(response1);
        Assertions.assertEquals("0201", response1.code());
        Assertions.assertEquals("unauthorized", response1.message());
        Assertions.assertNull(response1.data());
        //
        final Response<Object> response2 = userService.registerSeller(new Authentication(null, null, true), new RegisterSellerReq("Charlie", "charlie", "alice"));
        Assertions.assertNotNull(response2);
        Assertions.assertEquals("0201", response2.code());
        Assertions.assertEquals("unauthorized", response2.message());
        Assertions.assertNull(response2.data());
    }

    @Test
    void registerSellerBadRequest() {
        final User admin = userRepository.findById(1L).orElseThrow();
        final Authentication authentication = new Authentication(admin.id(), admin.role(), true);
        final Response<Object> response1 = userService.registerSeller(authentication, null);
        Assertions.assertNotNull(response1);
        Assertions.assertEquals("0301", response1.code());
        Assertions.assertEquals("bad request", response1.message());
        Assertions.assertNull(response1.data());
    }

    @Test
    void registerSellerFailed() {
        final RegisterSellerReq req = new RegisterSellerReq("Charlie", "charlie", "alice");
        Mockito.when(userRepository.saveSeller(ArgumentMatchers.any(), ArgumentMatchers.eq(req), ArgumentMatchers.anyString())).thenReturn(0L);
        final User admin = userRepository.findById(1L).orElseThrow();
        final Authentication authentication = new Authentication(admin.id(), admin.role(), true);
        final Response<Object> response = userService.registerSeller(authentication, req);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("0501", response.code());
        Assertions.assertEquals("Gagal mendaftarkan seller", response.message());
        Assertions.assertNull(response.data());
    }
}

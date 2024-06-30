package jawa.sinaukoding.sk.repository;

import jawa.sinaukoding.sk.entity.User;
import jawa.sinaukoding.sk.model.Authentication;
import jawa.sinaukoding.sk.model.request.RegisterBuyerReq;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Test
    void saveUser() {
        final User user = userRepository.findById(1L).get();
        final Authentication authentication = new Authentication(user.id(), user.role(), true);
        final RegisterBuyerReq req = new RegisterBuyerReq("MM", "mm@example.com", "12345");
        String encoded = passwordEncoder.encode(req.password());
        final Long saved = userRepository.saveBuyer(authentication, req, encoded);
        Assertions.assertTrue(saved > 0);
    }
}

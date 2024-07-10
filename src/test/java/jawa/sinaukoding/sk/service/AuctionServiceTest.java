package jawa.sinaukoding.sk.service;


import jawa.sinaukoding.sk.SkApplication;
import jawa.sinaukoding.sk.entity.Auction;
import jawa.sinaukoding.sk.model.Response;
import jawa.sinaukoding.sk.repository.AuctionRepository;
import org.junit.jupiter.api.Assertions;
import jawa.sinaukoding.sk.entity.User;
import jawa.sinaukoding.sk.model.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SkApplication.class)
@ComponentScan(basePackages = "jawa.sinaukoding.sk")
class AuctionServiceTest {
    @MockBean
    private AuctionRepository auctionRepository;

    @Autowired
    private AuctionService auctionService;

    @Test
    void testAuctionRejected_AuctionNotFound() {
        Mockito.when(auctionRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        Authentication adminAuth = new Authentication(1L, User.Role.ADMIN, true);
        Response<Object> response = auctionService.auctionRejected(adminAuth, 1L);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("0201", response.code());
        Assertions.assertEquals("Auction not found", response.message());
    }
    @Test
    void testAuctionRejected_AuctionAlreadyRejected() {
        Auction auction = new Auction(1L, "CODE123", "Sample Auction", "This is a sample auction",
                BigInteger.valueOf(1000), BigInteger.valueOf(3000), 1L, "nesya",
                Auction.Status.REJECTED, OffsetDateTime.now(), OffsetDateTime.now().plusDays(1),
                1L, null, null, OffsetDateTime.now(), null, null);

        Mockito.when(auctionRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(auction));

        Authentication adminAuth = new Authentication(1L, User.Role.ADMIN, true);
        Response<Object> response = auctionService.auctionRejected(adminAuth, 1L);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("0202", response.code());
        Assertions.assertEquals("Auction was Rejected", response.message());
    }

    @Test
    void testAuctionRejected_WaitingForApproval_Success() {
        OffsetDateTime startTime = OffsetDateTime.of(2024, 7, 10, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endTime = startTime.plusDays(1);

        Auction auction = new Auction(1L, "CODE123", "Sample Auction", "This is a sample auction",
                BigInteger.valueOf(1000), BigInteger.valueOf(3000), 1L, "nesya",
                Auction.Status.WAITING_FOR_APPROVAL,startTime, endTime,
                1L, null, null, OffsetDateTime.now(), null, null);

        Mockito.when(auctionRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(auction));
        Mockito.when(auctionRepository.autionRejected(ArgumentMatchers.anyLong())).thenReturn(1L);

        Authentication adminAuth = new Authentication(1L, User.Role.ADMIN, true);
        Response<Object> response = auctionService.auctionRejected(adminAuth, 1L);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("0200", response.code());
        Assertions.assertEquals("Succes for Rejected", response.message());
    }


//    @Test
//    void testAuctionRejected_InvalidToken() {
//        // Simulate authentication with an invalid token
//        Authentication invalidAuth = new Authentication(1L, User.Role.SELLER, true);
//        Long auctionId = 1L;
//
//        // Mock precondition method to return empty Optional (indicating invalid token)
//        Mockito.when(auctionService.precondition(invalidAuth, User.Role.ADMIN))
//                .thenReturn(Optional.empty());
//
//        // Call the auctionRejected method with invalid authentication
//        Response<Object> response = auctionService.auctionRejected(invalidAuth, auctionId);
//
//        // Assert that the response matches the expected invalid token response
//        Assertions.assertNotNull(response);
//        Assertions.assertEquals("0204", response.code());
//        Assertions.assertEquals("Invalid Token", response.message());
//    }


    @Test
    void testAuctionRejected_WaitingForApproval_Failed() {
        Auction auction = new Auction(1L, "CODE123", "Sample Auction", "This is a sample auction",
                BigInteger.valueOf(1000), BigInteger.valueOf(3000), 1L, "nesya",
                Auction.Status.WAITING_FOR_APPROVAL, OffsetDateTime.now(), OffsetDateTime.now().plusDays(1),
                1L, null, null, OffsetDateTime.now(), null, null);

        Mockito.when(auctionRepository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.of(auction));
        Mockito.when(auctionRepository.autionRejected(ArgumentMatchers.anyLong())).thenReturn(0L);

        Authentication adminAuth = new Authentication(1L, User.Role.ADMIN, true);
        Response<Object> response = auctionService.auctionRejected(adminAuth, 1L);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("0203", response.code());
        Assertions.assertEquals("Failed to rejected an Auction", response.message());
    }


}


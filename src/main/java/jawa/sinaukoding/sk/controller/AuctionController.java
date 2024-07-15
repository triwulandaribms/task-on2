package jawa.sinaukoding.sk.controller;

import jawa.sinaukoding.sk.exception.CustomeException;
import jawa.sinaukoding.sk.model.Authentication;
import jawa.sinaukoding.sk.model.Response;
import jawa.sinaukoding.sk.model.request.AuctionBidReq;
import jawa.sinaukoding.sk.model.request.SellerCreateAuctionReq;
import jawa.sinaukoding.sk.service.AuctionService;
import jawa.sinaukoding.sk.util.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/secured/auction")
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    // list auction
    @GetMapping("/list-auction")
    public Response<Object> listAuctions(@RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "status", defaultValue = "APPROVED") String status) {

        Authentication authentication = SecurityContextHolder.getAuthentication();
        return auctionService.listAuctions(authentication, page, size, status);
    }

    // list auction buyer
    @GetMapping("/list-auction-buyer")
    public Response<Object> listAuctionsBuyer(@RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Authentication authentication = SecurityContextHolder.getAuthentication();
        return auctionService.listAuctionsBuyer(authentication, page, size);
    }

    // seller bisa createAuction
    @PostMapping("/create-auction")
    public Response<Object> createAuction(@RequestBody SellerCreateAuctionReq req) {
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return auctionService.createAuction(authentication, req);
    }

    // admin, bisa approve
    @PostMapping("/{id}/approve")
    public Response<Object> approveAuction(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return auctionService.approveAuction(authentication, id);
    }

    // admin, bisa reject
    @PostMapping("/reject/{id}")
    public Response<Object> rejectAuction(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getAuthentication();
        return auctionService.auctionRejected(auth, id);
    }

    // buyer, create AuctionBid
    @PostMapping("/create-bid")
    public Response<Object> createAuctionBid(@RequestBody AuctionBidReq req) {
        Authentication authentication = SecurityContextHolder.getAuthentication();
        return auctionService.createAuctionBid(authentication, req);
    }

}

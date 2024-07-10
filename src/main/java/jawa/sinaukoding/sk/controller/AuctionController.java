package jawa.sinaukoding.sk.controller;

import jawa.sinaukoding.sk.model.Authentication;
import jawa.sinaukoding.sk.model.Response;
import jawa.sinaukoding.sk.model.request.SellerCreateAuctionReq;
import jawa.sinaukoding.sk.service.AuctionService;
import jawa.sinaukoding.sk.util.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/secured/auction")
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService){
        this.auctionService = auctionService;
    }

    // seller bisa createAuction
    @PostMapping("/create")
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
        return auctionService.auctionRejected(auth,id);
    }
}

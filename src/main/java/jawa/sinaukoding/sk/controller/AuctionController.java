package jawa.sinaukoding.sk.controller;

import jawa.sinaukoding.sk.model.Authentication;
import jawa.sinaukoding.sk.model.Response;
import jawa.sinaukoding.sk.model.request.SellerCreateAuctionReq;
import jawa.sinaukoding.sk.service.AuctionService;
import jawa.sinaukoding.sk.util.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/secured/auction")
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService){
        this.auctionService = auctionService;
    }

    // seller bisa createAuction
    @PostMapping("/create-auction")
    public Response<Object> createAuction() {
        return Response.badRequest();
    }

    // admin, bisa approve
    @PostMapping("/approve")
    public Response<Object> approveAuction() {
        return Response.badRequest();
    }

    // admin, bisa reject
    @PostMapping("/reject/{id}")
    public Response<Object> rejectAuction(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getAuthentication();
        return auctionService.auctionRejected(auth,id);
    }
}

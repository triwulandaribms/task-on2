package jawa.sinaukoding.sk.controller;

import jawa.sinaukoding.sk.model.Authentication;
import jawa.sinaukoding.sk.model.Response;
import jawa.sinaukoding.sk.model.request.SellerCreateAuctionReq;
import jawa.sinaukoding.sk.service.AuctionService;
import jawa.sinaukoding.sk.util.SecurityContextHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/secured/auction")
public class AuctionController {

    @Autowired
    private AuctionService auctionService;

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
    @PostMapping("")
    public Response<Object> rejectAuction() {
        return Response.badRequest();
    }
}

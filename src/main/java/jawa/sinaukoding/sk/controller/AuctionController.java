package jawa.sinaukoding.sk.controller;

import jawa.sinaukoding.sk.entity.Auction;
import jawa.sinaukoding.sk.model.Response;
import jawa.sinaukoding.sk.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/secured/auction")
public class AuctionController {

    @Autowired
    private AuctionService auctionService;

    // seller bisa createAuction
    @PostMapping("")
    public Response<Object> createAuction() {
        return Response.badRequest();
    }

    // admin, bisa approve
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/approve")
    public Response<Auction> approveAuction(@PathVariable Long id) {
        Optional<Auction> approvedAuction = auctionService.approveAuction(id);
        return approvedAuction.map(Response::ok).orElseGet(Response::badRequest);
    }

    // admin, bisa reject
    @PostMapping("")
    public Response<Object> rejectAuction() {
        return Response.badRequest();
    }
}

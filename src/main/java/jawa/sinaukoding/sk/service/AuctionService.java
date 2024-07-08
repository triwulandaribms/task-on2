package jawa.sinaukoding.sk.service;

import jawa.sinaukoding.sk.entity.Auction;
import jawa.sinaukoding.sk.repository.AuctionRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class AuctionService {

    @Autowired
    private AuctionRepository auctionRepository;

    public Auction createAuction(Auction auction) {
        return auctionRepository.save(auction);
    }

    public Optional<Auction> approveAuction(Long id) {
        Optional<Auction> auction = auctionRepository.findById(id);
        auction.ifPresent(a -> {
            a = new Auction(a.id(), a.code(), a.name(), a.description(), a.offer(), a.highestBid(), a.highestBidderId(), a.highestBidderName(), Auction.Status.APPROVED, a.startedAt(), a.endedAt(), a.createdBy(), a.updatedBy(), a.deletedBy(), a.createdAt(), a.updatedAt(), a.deletedAt());
            auctionRepository.save(a);
        });
        return auction;
    }
}

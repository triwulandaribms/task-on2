package jawa.sinaukoding.sk.service;


import jawa.sinaukoding.sk.entity.Auction;
import jawa.sinaukoding.sk.entity.User;
import jawa.sinaukoding.sk.model.Authentication;
import jawa.sinaukoding.sk.model.Response;
import jawa.sinaukoding.sk.model.request.SellerCreateAuctionReq;
import jawa.sinaukoding.sk.repository.AuctionRepo;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

// import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;


@Service
public class AuctionService extends AbstractService{
    private final AuctionRepo auctionRepository;

    public AuctionService(final AuctionRepo userRepository) {
        this.auctionRepository = userRepository;
    }

    public Response<Object> createAuction(Authentication authentication, SellerCreateAuctionReq req){
        return precondition(authentication, User.Role.SELLER).orElseGet(()->{
            if(req.maximumPrice().compareTo(req.minimumPrice()) <= 0){
                return Response.create("40", "03", "Harga maksimum harus lebih besar dari harga minimum.", null);
            }

          
            OffsetDateTime startAt = OffsetDateTime.parse(req.startedAt());
            OffsetDateTime endAt = OffsetDateTime.parse(req.endedAt());
            BigInteger offerPrice = req.maximumPrice().subtract(req.minimumPrice()).divide(BigInteger.TWO);

            Auction auction = new Auction(
                null, 
                UUID.randomUUID().toString().substring(0,8).toUpperCase(),
                req.name(), 
                req.description(),
                offerPrice, 
                offerPrice, 
                0L, 
                "", 
                Auction.Status.WAITING_FOR_APPROVAL, 
                startAt, 
                endAt, 
                authentication.id(), 
                null, 
                null, 
                null, 
                null, 
                null);
          
           Long auctionRepo = auctionRepository.saveAuction(auction);

           if(auctionRepo == null){
                return Response.create("40", "00", "gagal save auction", null);
           }

                return Response.create("20", "01", "sukses membuat pengajuan lelang", auctionRepo);
        });
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


 

package jawa.sinaukoding.sk.service;

import jawa.sinaukoding.sk.entity.Auction;
import jawa.sinaukoding.sk.entity.User;
import jawa.sinaukoding.sk.model.Authentication;
import jawa.sinaukoding.sk.model.Response;
import jawa.sinaukoding.sk.model.request.SellerCreateAuctionReq;
import jawa.sinaukoding.sk.repository.AuctionRepo;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class AuctionService extends AbstractService {
    private final AuctionRepo auctionRepository;

    public AuctionService(final Environment env, final AuctionRepo auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    public Response<Object> createAuction(Authentication authentication, SellerCreateAuctionReq req) {
        return precondition(authentication, User.Role.SELLER).orElseGet(() -> {
            if (req.maximumPrice().compareTo(req.minimumPrice()) <= 0) {
                return Response.create("40", "03", "Harga maksimum harus lebih besar dari harga minimum.", null);
            }

            OffsetDateTime startAt = OffsetDateTime.parse(req.startedAt());
            OffsetDateTime endAt = OffsetDateTime.parse(req.endedAt());
            BigInteger offerPrice = req.maximumPrice().subtract(req.minimumPrice()).divide(BigInteger.TWO);

            Auction auction = new Auction(
                    null,
                    UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                    req.name(),
                    req.description(),
                    offerPrice.intValue(),
                    offerPrice.intValue(),
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
                    null
            );

            Long auctionRepo = auctionRepository.saveAuction(auction);

            if (auctionRepo == null) {
                return Response.create("40", "00", "gagal save auction", null);
            }

            return Response.create("20", "01", "sukses membuat pengajuan lelang", auctionRepo);
        });
    }

    public Response<Object> approveAuction(Authentication authentication, Long id) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            Optional<Auction> auctionOpt = auctionRepository.findById(id);
            if (auctionOpt.isEmpty()) {
                return Response.create("07", "02", "Auction tidak ditemukan", null);
            }

            Long updated = auctionRepository.updateAuctionStatus(id, Auction.Status.APPROVED);

            if (updated == 1L) {
                return Response.create("07", "00", "Sukses", updated);
            } else {
                return Response.create("07", "01", "Gagal menyetujui lelang", null);
            }
        });
    }
}

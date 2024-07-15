package jawa.sinaukoding.sk.service;

import jawa.sinaukoding.sk.entity.Auction;
import jawa.sinaukoding.sk.entity.AuctionBid;
import jawa.sinaukoding.sk.entity.User;
import jawa.sinaukoding.sk.model.Authentication;
import jawa.sinaukoding.sk.model.Page;
import jawa.sinaukoding.sk.model.Response;
import jawa.sinaukoding.sk.model.request.AuctionBidReq;
import jawa.sinaukoding.sk.model.request.SellerCreateAuctionReq;
import jawa.sinaukoding.sk.model.response.AuctionDto;
import jawa.sinaukoding.sk.model.response.UserDto;
import jawa.sinaukoding.sk.repository.AuctionRepository;
import jawa.sinaukoding.sk.repository.UserRepository;
import jawa.sinaukoding.sk.util.HexUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuctionService extends AbstractService {
    private final AuctionRepository auctionRepository;

    private final UserRepository userRepository;

    private final byte[] jwtKey;

    public AuctionService(final Environment env, final AuctionRepository auctionRepository,
            UserRepository userRepository) {
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
        final String skJwtKey = env.getProperty("sk.jwt.key");
        this.jwtKey = HexUtils.hexToBytes(skJwtKey);
    }

    // public Response<Object> listAuction(final Authentication authentication,
    // final int page, final int size) {
    // return precondition(authentication, User.Role.ADMIN, User.Role.SELLER,
    // User.Role.BUYER).orElseGet(() -> {
    // if (page <= 0 || size <= 0) {
    // return Response.badRequest();
    // }

    // final List<Auction> auctions = auctionRepository.listAuction(page, size);
    // final List<AuctionDto> dto = auctions.stream()
    // .map(auction -> new AuctionDto(
    // auction.id(),
    // auction.code(),
    // auction.name(),
    // auction.description(),
    // auction.offer(),
    // auction.highestBid(),
    // Timestamp.valueOf(auction.startedAt().toLocalDateTime()),
    // Timestamp.valueOf(auction.endedAt().toLocalDateTime()),
    // auction.status().toString())).toList();

    // return Response.create("09", "00", "Sukses", dto);
    // });
    // }

    public Response<Object> listAuctions(final Authentication authentication, final int page, final int size, final String status) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            if (page <= 0 || size <= 0) {
                return Response.badRequest();
            }
            Page<Auction> auctionPage = auctionRepository.listAuctions(page, size, null);
            List<AuctionDto> auctions = auctionPage.data().stream().map(
                    auction -> new AuctionDto(auction.id(), auction.name(), null, null, null, null, null, null, null))
                    .toList();
            Page<AuctionDto> p = new Page<>(auctionPage.totalData(), auctionPage.totalPage(), auctionPage.page(),
                    auctionPage.saze(), auctions);
            return Response.create("09", "00", "Sukses", p);
        });
    }

    public Response<Object> auctionRejected(final Authentication authentication, Long id) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            Optional<Auction> auctOpt = auctionRepository.findById(id);
            if (auctOpt.isEmpty()) {
                return Response.create("02", "01", "Lelang tidak ditemukan", null);
            }
            Auction auction = auctOpt.get();
            if (auction.status() == Auction.Status.REJECTED) {
                return Response.create("02", "02", "Lelang sudah di tolak ", null);
            }

            if (auction.status() != Auction.Status.WAITING_FOR_APPROVAL) {
                return Response.create("02", "03", "Gagal menolak lelang", null);
            } else {
                long rejected = auctionRepository.updateAuctionStatus(authentication.id(), id, Auction.Status.REJECTED);
                if (rejected == 0L) {
                    return Response.create("02", "03", "Gagal menolak lelang", null);
                }
                return Response.create("02", "00", "Sukses", null);

            }
        });
    }

    // public Response<Object> createAuction(final Authentication
    // authentication,final SellerCreateAuctionReq req) {
    // return precondition(authentication, User.Role.SELLER).orElseGet(() -> {
    // if (req == null) {
    // return Response.badRequest();
    // }

    // OffsetDateTime startedAt = OffsetDateTime.parse(req.startedAt());
    // OffsetDateTime endedAt = OffsetDateTime.parse(req.endedAt());

    // Auction newAuction = new Auction(
    // UUID.randomUUID().toString().substring(0, 8),
    // req.name(),
    // req.description(),
    // req.minimumPrice(),
    // startedAt,
    // endedAt,
    // authentication.id()
    // );

    // final Long saved = auctionRepository.saveAuction(newAuction);

    // if (saved == 0L) {
    // return Response.create("05", "01", "Gagal membuat lelang.", null);
    // } else {
    // return Response.create("05", "00", "Sukses membuat lelang.", saved);
    // }
    // });
    // }

    public Response<Object> createAuction(final Authentication authentication, final SellerCreateAuctionReq req) {
        return precondition(authentication, User.Role.SELLER).orElseGet(() -> {
            if (req == null) {
                return Response.badRequest();
            }

            OffsetDateTime startedAt;
            OffsetDateTime endedAt;

            // pengecekan format waktu
            try {
                startedAt = OffsetDateTime.parse(req.startedAt());
                endedAt = OffsetDateTime.parse(req.endedAt());
            } catch (DateTimeParseException e) {
                return Response.create("05", "02", "Format waktu tidak valid.", null);
            }

            // pengecekan startedAt tidak boleh kurang dari endedAt
            if (startedAt.isAfter(endedAt)) {
                return Response.create("05", "03", "Waktu mulai harus sebelum waktu selesai.", null);
            }

            // pengecekan untuk waktu sekarang
            // if (startedAt.isBefore(OffsetDateTime.now())) {
            //     return Response.create("05", "04", "Waktu mulai harus sekarang.", null);
            // }

            Auction newAuction = new Auction(
                    UUID.randomUUID().toString().substring(0, 8),
                    req.name(),
                    req.description(),
                    req.minimumPrice(),
                    startedAt,
                    endedAt,
                    authentication.id());

            final Long saved = auctionRepository.saveAuction(newAuction);

            if (saved == 0L) {
                return Response.create("05", "01", "Gagal membuat lelang.", null);
            } else {
                return Response.create("05", "00", "Sukses membuat lelang.", saved);
            }
        });
    }

    public Response<Object> approveAuction(Authentication authentication, Long id) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            Optional<Auction> auctionOpt = auctionRepository.findById(id);
            if (auctionOpt.isEmpty()) {
                return Response.create("07", "02", "Auction tidak ditemukan", null);
            }

            Long updated = auctionRepository.updateAuctionStatus(authentication.id(), id, Auction.Status.APPROVED);

            if (updated == 1L) {
                return Response.create("07", "00", "Sukses", updated);
            } else {
                return Response.create("07", "01", "Gagal menyetujui lelang", null);
            }
        });
    }

    @Transactional
    public Response<Object> createAuctionBid(final Authentication authentication, final AuctionBidReq req) {
        return precondition(authentication, User.Role.BUYER).orElseGet(() -> {
            if (req == null) {
                return Response.badRequest();
            }

            Optional<Auction> findId = auctionRepository.findById(req.auctionId());
            if (findId.isEmpty()) {
                return Response.create("06", "01", "Lelang tidak ditemukan", null);
            }

            Auction auction = findId.get();

            Long highestBid = auction.highestBid();
            Long minimumPrice = auction.offer();
            if (req.bid() <= minimumPrice) {
                return Response.create("06", "01", "Gagal membuat bidding", null);
            }
            if (highestBid > 0 && req.bid() < highestBid) {
                return Response.create("06", "01", "Gagal membuat bidding", null);
            }

            final AuctionBid auctionBid = new AuctionBid(
                    null, //
                    req.auctionId(), //
                    req.bid(), //
                    authentication.id(), //
                    OffsetDateTime.now()//
            );

            Optional<User> buyerOp = userRepository.findById(authentication.id());
            if (findId.isEmpty()) {
                return Response.create("06", "01", "Gagal membuat bidding, karena buyer tidak ditemukan", null);
            }

            User buyer = buyerOp.get();

            auctionRepository.updateAuction(req.auctionId(), req.bid(), authentication.id(), buyer.name());
            final Long saved = auctionRepository.saveAuctionBid(auctionBid);

            if (saved == 0L) {
                return Response.create("06", "01", "Gagal membuat bidding", null);
            }
            return Response.create("06", "00", "Sukses", saved);
        });
    }
}

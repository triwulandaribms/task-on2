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
import jawa.sinaukoding.sk.repository.AuctionBidRepository;
import jawa.sinaukoding.sk.repository.AuctionRepository;
import jawa.sinaukoding.sk.repository.UserRepository;
import jawa.sinaukoding.sk.util.HexUtils;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuctionService extends AbstractService {

    private final AuctionRepository auctionRepository;
    private final AuctionBidRepository auctionBidRepository;
    private final UserRepository userRepository;
    private final byte[] jwtKey;

    public AuctionService(Environment env,
                          AuctionRepository auctionRepository,
                          AuctionBidRepository auctionBidRepository,
                          UserRepository userRepository) {
        this.auctionRepository = auctionRepository;
        this.auctionBidRepository = auctionBidRepository;
        this.userRepository = userRepository;

        final String skJwtKey = env.getProperty("sk.jwt.key");
        this.jwtKey = HexUtils.hexToBytes(skJwtKey);
    }

    public Response<Object> listAuctions(Authentication authentication, int page, int size, String status) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            if (page <= 0 || size <= 0) return Response.badRequest();

            Pageable pageable = PageRequest.of(page - 1, size);
            org.springframework.data.domain.Page<Auction> auctionPage =
                    auctionRepository.findByStatusAndDeletedAtIsNull(Auction.Status.valueOf(status), pageable);

            List<AuctionDto> auctions = auctionPage.getContent().stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());

            Page<AuctionDto> responsePage = new Page<>(auctionPage.getTotalElements(), auctionPage.getTotalPages(), page, size, auctions);
            return Response.create("09", "00", "Sukses", responsePage);
        });
    }

    public Response<Object> listAuctionsBuyer(Authentication authentication, int page, int size) {
        return precondition(authentication, User.Role.BUYER).orElseGet(() -> {
            if (page <= 0 || size <= 0) return Response.badRequest();

            Pageable pageable = PageRequest.of(page - 1, size);
            org.springframework.data.domain.Page<Auction> auctionPage = auctionRepository.findAllActive(pageable);

            List<AuctionDto> auctions = auctionPage.getContent().stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());

            Page<AuctionDto> responsePage = new Page<>(auctionPage.getTotalElements(), auctionPage.getTotalPages(), page, size, auctions);
            return Response.create("09", "00", "Sukses", responsePage);
        });
    }

    public Response<Object> auctionRejected(Authentication authentication, Long id) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            Optional<Auction> auctionOpt = auctionRepository.findById(id);
            if (auctionOpt.isEmpty()) {
                return Response.create("02", "01", "Lelang tidak ditemukan", null);
            }

            Auction auction = auctionOpt.get();
            if (auction.getStatus() == Auction.Status.REJECTED) {
                return Response.create("02", "02", "Lelang sudah ditolak", null);
            }
            if (auction.getStatus() != Auction.Status.WAITING_FOR_APPROVAL) {
                return Response.create("02", "03", "Gagal menolak lelang", null);
            }

            auction.setStatus(Auction.Status.REJECTED);
            auction.setUpdatedBy(authentication.id());
            auction.setUpdatedAt(OffsetDateTime.now());
            auctionRepository.save(auction);

            return Response.create("02", "00", "Sukses", null);
        });
    }

    public Response<Object> createAuction(Authentication authentication, SellerCreateAuctionReq req) {
        return precondition(authentication, User.Role.SELLER).orElseGet(() -> {
            if (req == null) return Response.badRequest();

            OffsetDateTime startedAt, endedAt;
            try {
                startedAt = OffsetDateTime.parse(req.startedAt());
                endedAt = OffsetDateTime.parse(req.endedAt());
            } catch (DateTimeParseException e) {
                return Response.create("05", "02", "Format waktu tidak valid.", null);
            }

            if (startedAt.isAfter(endedAt)) {
                return Response.create("05", "03", "Waktu mulai harus sebelum waktu selesai.", null);
            }

            if (endedAt.isBefore(OffsetDateTime.now())) {
                return Response.create("05", "04", "Waktu lelang tidak boleh di masa lalu", null);
            }

            Auction auction = new Auction();
            auction.setCode(UUID.randomUUID().toString().substring(0, 8));
            auction.setName(req.name());
            auction.setDescription(req.description());
            auction.setOffer(req.minimumPrice());
            auction.setStartedAt(startedAt);
            auction.setEndedAt(endedAt);
            auction.setCreatedBy(authentication.id());
            auction.setCreatedAt(OffsetDateTime.now());
            auction.setStatus(Auction.Status.WAITING_FOR_APPROVAL);

            Auction saved = auctionRepository.save(auction);
            if (saved.getId() == null) {
                return Response.create("05", "01", "Gagal membuat lelang.", null);
            }

            return Response.create("05", "00", "Sukses membuat lelang.", saved.getId());
        });
    }

    public Response<Object> approveAuction(Authentication authentication, Long id) {
        return precondition(authentication, User.Role.ADMIN).orElseGet(() -> {
            Optional<Auction> auctionOpt = auctionRepository.findById(id);
            if (auctionOpt.isEmpty()) {
                return Response.create("07", "02", "Auction tidak ditemukan", null);
            }

            Auction auction = auctionOpt.get();
            if (auction.getStatus() != Auction.Status.WAITING_FOR_APPROVAL) {
                return Response.create("07", "01", "Lelang tidak bisa di-approve", null);
            }

            auction.setStatus(Auction.Status.APPROVED);
            auction.setUpdatedBy(authentication.id());
            auction.setUpdatedAt(OffsetDateTime.now());
            auctionRepository.save(auction);

            return Response.create("07", "00", "Sukses", auction.getId());
        });
    }

    @Transactional
    public Response<Object> createAuctionBid(Authentication authentication, AuctionBidReq req) {
        return precondition(authentication, User.Role.BUYER).orElseGet(() -> {
            if (req == null) return Response.badRequest();

            Optional<Auction> auctionOpt = auctionRepository.findById(req.auctionId());
            if (auctionOpt.isEmpty()) {
                return Response.create("06", "01", "Lelang tidak ditemukan", null);
            }

            Auction auction = auctionOpt.get();
            if (auction.getStatus() != Auction.Status.APPROVED) {
                return Response.create("06", "01", "Lelang belum disetujui atau sudah ditutup", null);
            }

            if (auction.getEndedAt().isBefore(OffsetDateTime.now())) {
                return Response.create("06", "01", "Lelang telah berakhir", null);
            }

            Long highestBid = auction.getHighestBid() != null ? auction.getHighestBid() : 0L;
            Long minimumPrice = auction.getOffer() != null ? auction.getOffer() : 0L;

            if (req.bid() <= minimumPrice) {
                return Response.create("06", "01", "Bid harus lebih besar dari harga minimum", null);
            }
            if (highestBid > 0 && req.bid() <= highestBid) {
                return Response.create("06", "01", "Bid harus lebih besar dari bid tertinggi saat ini", null);
            }

            Optional<User> buyerOpt = userRepository.findById(authentication.id());
            if (buyerOpt.isEmpty()) {
                return Response.create("06", "01", "Buyer tidak ditemukan", null);
            }

            User buyer = buyerOpt.get();

            auction.setHighestBid(req.bid());
            auction.setHighestBidderId(authentication.id());
            auction.setHighestBidderName(buyer.getName());
            auction.setUpdatedBy(authentication.id());
            auction.setUpdatedAt(OffsetDateTime.now());
            auctionRepository.save(auction);

            AuctionBid auctionBid = new AuctionBid();
            auctionBid.setAuction(auction);
            auctionBid.setBid(req.bid());
            auctionBid.setBuyerId(authentication.id());
            auctionBid.setCreatedAt(OffsetDateTime.now());

            AuctionBid savedBid = auctionBidRepository.save(auctionBid);
            if (savedBid.getId() == null) {
                return Response.create("06", "01", "Gagal membuat bidding", null);
            }

            return Response.create("06", "00", "Sukses", savedBid.getId());
        });
    }

    private AuctionDto toDto(Auction auction) {
        return new AuctionDto(
                auction.getId(),
                auction.getCode(),
                auction.getName(),
                auction.getDescription(),
                auction.getOffer(),
                auction.getHighestBid(),
                auction.getStartedAt(),
                auction.getEndedAt(),
                auction.getStatus()
        );
    }
}

package jawa.sinaukoding.sk.service;

import jawa.sinaukoding.sk.entity.Auction;
import jawa.sinaukoding.sk.entity.User;
import jawa.sinaukoding.sk.model.Authentication;
import jawa.sinaukoding.sk.model.Response;
import jawa.sinaukoding.sk.repository.AuctionRepository;
import jawa.sinaukoding.sk.repository.UserRepository;
import jawa.sinaukoding.sk.util.HexUtils;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuctionService extends AbstractService {
    private final AuctionRepository auctionRepository;

    private final byte[] jwtKey;

    public AuctionService(final Environment env, final AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
        final String skJwtKey = env.getProperty("sk.jwt.key");
        this.jwtKey = HexUtils.hexToBytes(skJwtKey);
    }

    public Response<Object> auctionRejected(final Authentication authentication, Long id){
        return precondition(authentication, User.Role.ADMIN).orElseGet(()-> {
            Optional<Auction> auctOpt = auctionRepository.findById(id);
            if (auctOpt.isEmpty()) {
                return Response.create("02", "01", "Auction not found", null);
            }
            Auction auction = auctOpt.get();
            if (auction.status() == Auction.Status.REJECTED) {
                return Response.create("02", "02", "Auction was Rejected", null);
            }

            if (auction.status() == Auction.Status.WAITING_FOR_APPROVAL) {
                long rejected = auctionRepository.autionRejected(id);
                if (rejected == 0L) {
                    return Response.create("02", "03", "Failed to rejected an Auction", null);
                } else {
                    return Response.create("02", "00", "Succes for Rejected", null);
                }
            }
            return Response.create("02", "04", "Invalid Token", null);
        });
    }
}

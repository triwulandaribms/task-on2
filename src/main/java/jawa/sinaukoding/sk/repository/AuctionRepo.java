// package jawa.sinaukoding.sk.repository;

// import jawa.sinaukoding.sk.entity.Auction;
// import org.springframework.data.jpa.repository.JpaRepository;

// public interface AuctionRepository extends JpaRepository<Auction, Long> {
// }


package jawa.sinaukoding.sk.repository;

import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import jawa.sinaukoding.sk.entity.Auction;


@Repository
public class AuctionRepo {
    private static final Logger log = LoggerFactory.getLogger(UserRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public AuctionRepo(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long saveAuction(Auction auction){
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try{
            if(jdbcTemplate.update(con->Objects.requireNonNull(auction.insert(con)),keyHolder) != 1){
                return 0L;
            }else{
                return Objects.requireNonNull(keyHolder.getKey()).longValue();
            }
        } catch(Exception e){
            log.error("{}", e);
            return 0L;
        }      
    }

    public Optional<Auction> findById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    public void save(Auction a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }
}


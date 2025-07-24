package jawa.sinaukoding.sk.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jawa.sinaukoding.sk.entity.Auction;


@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {

    Page<Auction> findByStatusAndDeletedAtIsNull(Auction.Status status, Pageable pageable);

    // Untuk list buyer
    @Query("SELECT a FROM Auction a WHERE a.status = 'APPROVED' AND a.endedAt > CURRENT_TIMESTAMP AND a.deletedAt IS NULL")
    Page<Auction> findAllActive(Pageable pageable);
}

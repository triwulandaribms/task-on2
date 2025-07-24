package jawa.sinaukoding.sk.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jawa.sinaukoding.sk.entity.AuctionBid;

@Repository
public interface AuctionBidRepository extends JpaRepository<AuctionBid, Long> {
    // Jika butuh tambahan query, bisa dibuat di sini
}

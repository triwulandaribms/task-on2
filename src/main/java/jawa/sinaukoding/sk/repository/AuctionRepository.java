package jawa.sinaukoding.sk.repository;

import jawa.sinaukoding.sk.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
}

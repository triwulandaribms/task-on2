package jawa.sinaukoding.sk.model.response;

import java.time.OffsetDateTime;

import jawa.sinaukoding.sk.entity.Auction;

public record AuctionDto(
     Long id,
     String code,
     String name,
     String description,
     Long offer,
     Long highestBid,
     OffsetDateTime startedAt,
     OffsetDateTime endedAt,
     Auction.Status status){

  }

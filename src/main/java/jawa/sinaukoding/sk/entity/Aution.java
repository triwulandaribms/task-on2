package jawa.sinaukoding.sk.entity;

import java.math.BigInteger;
import java.time.OffsetDateTime;

public record Aution(Long id, //
                     String code, //
                     String name, //
                     String description, //
                     BigInteger openingPrice, //
                     BigInteger maximumPrice, //
                     OffsetDateTime closedAt, //
                     BigInteger highestBid, //
                     String buyerName, //
                     Long createdBy, //
                     Long updatedBy, //
                     Long deletedBy, //
                     OffsetDateTime createdAt, //
                     OffsetDateTime updatedAt, //
                     OffsetDateTime deletedAt //
) {
}

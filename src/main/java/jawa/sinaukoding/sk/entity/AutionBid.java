package jawa.sinaukoding.sk.entity;

import java.math.BigInteger;
import java.time.OffsetDateTime;

public record AutionBid(Long id, //
                        Long actionId, //
                        BigInteger currentBid, //
                        Long bidder, //
                        OffsetDateTime createdAt //
) {
}

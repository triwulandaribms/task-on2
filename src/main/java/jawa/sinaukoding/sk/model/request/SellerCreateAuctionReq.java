package jawa.sinaukoding.sk.model.request;

import java.math.BigInteger;

public record SellerCreateAuctionReq(String name,  //
                                     String description, //
                                     Long minimumPrice, //
                                     //BigInteger maximumPrice, //
                                     String startedAt, //
                                     String endedAt //
) {
}

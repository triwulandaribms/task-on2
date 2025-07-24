package jawa.sinaukoding.sk.model.request;


public record SellerCreateAuctionReq(String name,  
                                     String description, 
                                     Long minimumPrice, 
                                     String startedAt, 
                                     String endedAt 
) {
}

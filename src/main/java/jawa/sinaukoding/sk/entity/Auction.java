package jawa.sinaukoding.sk.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "sk_auction")
public class Auction {

    public enum Status {
        WAITING_FOR_APPROVAL, APPROVED, REJECTED, CLOSED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    private String name;

    private String description;

    private Long offer;

    @Column(name = "highest_bid")
    private Long highestBid = 0L;

    @Column(name = "highest_bidder_id")
    private Long highestBidderId;

    @Column(name = "highest_bidder_name") 
    private String highestBidderName;

    @Enumerated(EnumType.STRING)
    private Status status = Status.WAITING_FOR_APPROVAL;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "ended_at")
    private OffsetDateTime endedAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "deleted_by")
    private Long deletedBy;

    @Column(name = "created_at")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuctionBid> bids;

    public Auction() {}

    public Auction(String code, String name, String description, Long offer, OffsetDateTime startedAt, OffsetDateTime endedAt, Long createdBy) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.offer = offer;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.createdBy = createdBy;
        this.highestBid = 0L;
        this.highestBidderId = 0L;
        this.highestBidderName = "";
        this.status = Status.WAITING_FOR_APPROVAL;
        this.createdAt = OffsetDateTime.now();
    }

    public Long getId() { return id; }

    public String getCode() { return code; }

    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public Long getOffer() { return offer; }

    public void setOffer(Long offer) { this.offer = offer; }

    public Long getHighestBid() { return highestBid; }

    public void setHighestBid(Long highestBid) { this.highestBid = highestBid; }

    public Long getHighestBidderId() { return highestBidderId; }

    public void setHighestBidderId(Long highestBidderId) { this.highestBidderId = highestBidderId; }

    public String getHighestBidderName() { return highestBidderName; }

    public void setHighestBidderName(String highestBidderName) { this.highestBidderName = highestBidderName; }

    public Status getStatus() { return status; }

    public void setStatus(Status status) { this.status = status; }

    public OffsetDateTime getStartedAt() { return startedAt; }

    public void setStartedAt(OffsetDateTime startedAt) { this.startedAt = startedAt; }

    public OffsetDateTime getEndedAt() { return endedAt; }

    public void setEndedAt(OffsetDateTime endedAt) { this.endedAt = endedAt; }

    public Long getCreatedBy() { return createdBy; }

    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public Long getUpdatedBy() { return updatedBy; }

    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }

    public Long getDeletedBy() { return deletedBy; }

    public void setDeletedBy(Long deletedBy) { this.deletedBy = deletedBy; }

    public OffsetDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    public OffsetDateTime getDeletedAt() { return deletedAt; }

    public void setDeletedAt(OffsetDateTime deletedAt) { this.deletedAt = deletedAt; }

    public List<AuctionBid> getBids() { return bids; }

    public void setBids(List<AuctionBid> bids) { this.bids = bids; }
}

package org.tbk.bitcoin.example.payreq.donation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jmolecules.ddd.types.AggregateRoot;
import org.jmolecules.ddd.types.Association;
import org.jmolecules.ddd.types.Identifier;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.tbk.bitcoin.example.payreq.order.Order;
import org.tbk.bitcoin.example.payreq.payment.BitcoinOnchainPaymentRequest;
import org.tbk.bitcoin.example.payreq.payment.PaymentRequest;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Getter
@Setter(AccessLevel.PACKAGE)
@Table(name = "donation")
@ToString
public class Donation extends AbstractAggregateRoot<Donation> implements AggregateRoot<Donation, Donation.DonationId> {

    private final DonationId id;

    private final long createdAt;

    @Column(name = "order_id")
    private final Association<Order, Order.OrderIdentifier> order;

    @Column(name = "payment_request_id")
    private final Association<BitcoinOnchainPaymentRequest, PaymentRequest.PaymentRequestIdentifier> paymentRequest;

    private String description;

    private String paymentUrl;

    private String displayPrice;

    private String comment;

    @JsonIgnore
    @Version
    private Long version;

    Donation(Order order, BitcoinOnchainPaymentRequest paymentRequest) {
        this.id = DonationId.of(UUID.randomUUID().toString());
        this.createdAt = Instant.now().toEpochMilli();
        this.order = Association.forAggregate(order);
        this.paymentRequest = Association.forAggregate(paymentRequest);
        this.paymentUrl = paymentRequest.getPaymentUrl();
        this.displayPrice = paymentRequest.getDisplayPrice();

        registerEvent(new DonationCreatedEvent(this));
    }

    @AfterDomainEventPublication
    void afterDomainEventPublication() {
        log.trace("AfterDomainEventPublication");
    }

    @Value(staticConstructor = "of")
    public static class DonationId implements Identifier {
        public static DonationId create() {
            return DonationId.of(UUID.randomUUID().toString());
        }

        String id;
    }

    @Value(staticConstructor = "of")
    public static class DonationCreatedEvent {

        Donation domain;

        public String toString() {
            return "DonationCreatedEvent";
        }
    }
}
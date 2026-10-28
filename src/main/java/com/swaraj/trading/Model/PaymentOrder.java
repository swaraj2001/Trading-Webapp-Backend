package com.swaraj.trading.Model;

import com.swaraj.trading.Domain.PaymentMethod;
import com.swaraj.trading.Domain.PaymentOrderStatus;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long amount;

    private PaymentOrderStatus paymentOrderStatus;

    private PaymentMethod paymentMethod;

    @ManyToOne
    private User user;
}

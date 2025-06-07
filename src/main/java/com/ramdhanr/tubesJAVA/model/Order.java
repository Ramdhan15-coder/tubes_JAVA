package com.ramdhanr.tubesJAVA.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders") // Nama tabel sesuai DDL kita
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "order_date", nullable = false, updatable = false) // Biasanya diisi saat pembuatan
    @Temporal(TemporalType.TIMESTAMP)
    // Bisa pakai @CreationTimestamp jika mau otomatis, atau diset manual di service
    private Timestamp orderDate;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // Misal: PENDING_PAYMENT, PROCESSING, SHIPPED, DELIVERED, CANCELED

    @Lob
    @Column(name = "shipping_address", nullable = false, columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(name = "payment_method_details", length = 255)
    private String paymentMethodDetails; // Misal: "Bank BCA via Manual Transfer"

    @Column(name = "payment_proof_url", length = 255)
    private String paymentProofUrl;

    @Lob
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes; // Catatan dari pembeli

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp updatedAt;

    // Helper method untuk sinkronisasi dua arah dengan OrderItem
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }

    public void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
    }
}
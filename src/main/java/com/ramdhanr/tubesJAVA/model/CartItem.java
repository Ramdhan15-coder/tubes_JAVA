package com.ramdhanr.tubesJAVA.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp; // Untuk addedAt (atau bisa juga CreationTimestamp jika hanya saat tambah)

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cart_items",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"cart_id", "product_id"}, name = "uk_cart_product")
       }
)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Relasi Many-to-One dengan ShoppingCart: banyak item milik satu keranjang
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private ShoppingCart shoppingCart;

    // Relasi Many-to-One dengan Product: banyak item bisa merujuk ke satu produk (tapi satu baris item hanya satu produk)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @UpdateTimestamp // Otomatis diisi/diupdate oleh Hibernate saat record disimpan/diupdate
    @Column(name = "added_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp addedAt;

}
package com.ramdhanr.tubesJAVA.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp; // Pastikan import ini ada

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reviews") // Pastikan nama tabel sesuai dengan yang ada di database kamu
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id") // Sesuai nama kolom di DDL gambar dan SQL yang kita buat
    private Integer reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "rating", nullable = false)
    private Byte rating; // TINYINT (1-5)

    @Lob
    @Column(name = "comment", columnDefinition = "TEXT") // Boleh null
    private String comment;

    @CreationTimestamp // Otomatis diisi Hibernate saat entitas pertama kali disimpan
    @Column(name = "review_date", nullable = false, updatable = false) // Tanggal review diset saat dibuat & tidak diupdate
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp reviewDate;

    @CreationTimestamp // Otomatis diisi Hibernate saat record pertama kali dibuat
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp // Otomatis diisi Hibernate saat record diupdate
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;
}
package com.ramdhanr.tubesJAVA.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp; 
import org.hibernate.annotations.UpdateTimestamp;   

import java.math.BigDecimal;
import java.sql.Timestamp; 

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    //  untuk createdAt dan updatedAt
    @CreationTimestamp // Akan diisi otomatis oleh Hibernate saat entitas pertama kali disimpan
    @Column(name = "created_at", nullable = false, updatable = false) 
    @Temporal(TemporalType.TIMESTAMP) 
    private Timestamp createdAt;

    @UpdateTimestamp // Akan diisi otomatis oleh Hibernate saat entitas disimpan atau diupdate
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp updatedAt;
    
}
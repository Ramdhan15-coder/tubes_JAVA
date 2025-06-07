package com.ramdhanr.tubesJAVA.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Entity 
@Table(name = "roles") 
public class Role {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Integer id;

    @Column(name = "name", length = 20, nullable = false, unique = true) 
    
    private String name;

}
package com.whocares.musicalapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "shops")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="address")
    private String address;

    @Column(name="category")
    private Integer category;

    @Column(name="image")
    private String image;

    @ManyToMany
    @JoinTable(
            name = "theater_shop",
            joinColumns = @JoinColumn(name = "shop_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "theater_id", nullable = false))
    @JsonIgnore
    private List<Theater> theaters;

    @Column(name="gaodeId",unique = true)
    private String gaodeId;

    /*@ManyToMany(mappedBy = "shops")
    @JsonIgnore
    private List<Theater> theaters;*/

}

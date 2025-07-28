package com.whocares.musicalapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "theaters")
@Data
public class Theater {

    @Getter
    @Id
    private Long id;

    @Column(name="name",nullable = false)
    private String name;

    @Column(name = "location_name", nullable = false)
    private String locationName;

    @Column(name = "latitude",nullable = false)
    private Double latitude;

    @Column(name = "longitude",nullable = false)
    private Double longitude;

    @Column(name = "`imageUrl`")
    private String imageUrl;

    /*@ManyToMany
    @JoinTable(
            name = "theater_shop",
            joinColumns = @JoinColumn(name = "theater_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "shop_id", nullable = false))
    private List<Shop> shops;*/

    @ManyToMany(mappedBy = "theaters")
    //@JsonIgnore
    private List<Shop> shops;

    public void setId(Long id) {

    }
}

package com.whocares.musicalapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

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

    public void setId(Long id) {

    }
}

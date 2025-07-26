package com.whocares.musicalapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;


@Entity
@Table(name = "musicals")
@Data
public class Musical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="name",nullable = false)
    private String name;

    @Column(name = "info")
    private String info;

    @Column(name = "is_original")
    private boolean isOriginal;

    @Column(name = "`imageUrl`")
    private String imageUrl;

    @OneToMany(mappedBy = "musicalId", fetch = FetchType.LAZY)
    private List<Show> shows;  // 演出排期集合
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "venue")
    private String venue;

//    @OneToMany(mappedBy = "musicalId", fetch = FetchType.LAZY)
//    private List<Show> shows;  // 演出排期集合

}

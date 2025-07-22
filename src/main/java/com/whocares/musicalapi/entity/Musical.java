package com.whocares.musicalapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;


@Entity
@Table(name = "musicals")
@Data
public class Musical {

    @Id
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

//    @OneToMany(mappedBy = "musicalId", fetch = FetchType.LAZY)
//    private List<Show> shows;  // 演出排期集合

}

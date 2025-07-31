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

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 让数据库处理 ID 的生成
    private Long id;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name = "location_name", nullable = false)
    private String locationName;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "`imageUrl`")
    private String imageUrl;

    @ManyToMany(mappedBy = "theaters")
    private List<Shop> shops;
}



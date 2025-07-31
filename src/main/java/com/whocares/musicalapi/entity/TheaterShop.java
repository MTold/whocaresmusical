package com.whocares.musicalapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "theater_shop")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TheaterShop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="theater_id")
    private Long theaterId;

    @Column(name="shop_id")
    private Long shopId;
}

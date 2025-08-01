package com.whocares.musicalapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "shows")
@Data
public class Show {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "theater_id")
    private Long theaterId;

    @Column(name = "musical_id")
    private Long musicalId;

//    @ManyToOne
//    @JoinColumn(name = "theater_id", referencedColumnName = "id")
//    private Theater theater;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "time")
    private LocalTime time;

    @Column(name = "cast")
    private String cast;
}

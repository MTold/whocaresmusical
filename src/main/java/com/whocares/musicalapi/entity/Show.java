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
    private Long id;

    @Column(name = "musical_id")
    private Long musicalId;

    @Column(name = "theater_id")
    private Long theaterId;

//    @ManyToOne
//    @JoinColumn(name = "musical_id", referencedColumnName = "id")
//    private Musical musical;
//
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

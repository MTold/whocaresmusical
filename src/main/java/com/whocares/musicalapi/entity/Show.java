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

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "musical_id", referencedColumnName = "id", insertable = false, updatable = false)
//    private Musical musical;

    @Column(name = "musical_id")
    private Long musicalId;

    @Column(name = "theatre_id")
    private Long theatreId;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "time")
    private LocalTime time;

    @Column(name = "cast")
    private String cast;

}

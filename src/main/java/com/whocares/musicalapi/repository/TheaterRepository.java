package com.whocares.musicalapi.repository;

import com.whocares.musicalapi.entity.Shop;
import com.whocares.musicalapi.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, Long> {



}

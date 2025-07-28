package com.whocares.musicalapi.repository;

import com.whocares.musicalapi.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {



}

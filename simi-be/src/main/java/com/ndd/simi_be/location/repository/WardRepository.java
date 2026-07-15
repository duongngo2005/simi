package com.ndd.simi_be.location.repository;

import com.ndd.simi_be.location.entity.Ward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WardRepository extends JpaRepository<Ward, String> {
    List<Ward> findByProvinceCode(String provinceCode);
}

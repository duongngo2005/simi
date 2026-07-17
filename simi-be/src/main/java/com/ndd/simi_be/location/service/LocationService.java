package com.ndd.simi_be.location.service;

import com.ndd.simi_be.location.dto.ProvinceResponse;
import com.ndd.simi_be.location.dto.WardResponse;
import com.ndd.simi_be.location.mapper.LocationMapper;
import com.ndd.simi_be.location.repository.ProvinceRepository;
import com.ndd.simi_be.location.repository.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final ProvinceRepository provinceRepository;
    private final WardRepository wardRepository;

    public List<ProvinceResponse> getAllProvinces(){
        return provinceRepository.findAll().stream().map(LocationMapper::toProvinceResponse).toList();
    }

    public List<WardResponse> getAllWardsByProvince(String provinceCode){
        return wardRepository.findByProvinceCode(provinceCode).stream().map(LocationMapper::toWardResponse).toList();
    }
}

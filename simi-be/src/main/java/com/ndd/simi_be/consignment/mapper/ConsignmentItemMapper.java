package com.ndd.simi_be.consignment.mapper;

import com.ndd.simi_be.consignment.dto.response.ConsignmentItemResponse;
import com.ndd.simi_be.consignment.entity.ConsignmentItem;
import com.ndd.simi_be.product.mapper.ProductMapper;

public class ConsignmentItemMapper {
    public static ConsignmentItemResponse toConsignmentItemResponse(ConsignmentItem item){
        return ConsignmentItemResponse.builder()
                .id(item.getId())
                .commissionRate(item.getCommissionRate())
                .status(item.getConsignmentItemStatus().name())
                .productResponse(ProductMapper.toProductResponse(item.getProduct()))
                .priceScheduleResponses(
                        item.getPriceSchedules().stream().map(PriceScheduleMapper::toPriceScheduleResponse).toList()
                )
                .itemDispositionResponse(
                        item.getItemDisposition() == null
                        ? null
                        : ItemDispositionMapper.toItemDispositionResponse(item.getItemDisposition())
                )
                .build();
    }
}

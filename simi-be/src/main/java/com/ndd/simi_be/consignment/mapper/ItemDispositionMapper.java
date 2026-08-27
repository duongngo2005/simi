package com.ndd.simi_be.consignment.mapper;

import com.ndd.simi_be.consignment.dto.response.ItemDispositionResponse;
import com.ndd.simi_be.consignment.entity.Consignment;
import com.ndd.simi_be.consignment.entity.ConsignmentItem;
import com.ndd.simi_be.consignment.entity.ItemDisposition;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.user.entity.User;

public class ItemDispositionMapper {
    public static ItemDispositionResponse toItemDispositionResponse(ItemDisposition itemDisposition) {
        ConsignmentItem item = itemDisposition.getConsignmentItem();
        Product product = item != null ? item.getProduct() : null;
        Consignment consignment = item != null ? item.getConsignment() : null;
        User consignor = consignment != null ? consignment.getConsignor() : null;

        return ItemDispositionResponse.builder()
                .id(itemDisposition.getId())
                .type(itemDisposition.getItemDispositionType() != null ? itemDisposition.getItemDispositionType().name() : null)
                .status(itemDisposition.getItemDispositionStatus() != null ? itemDisposition.getItemDispositionStatus().name() : null)
                .productName(product != null ? product.getName() : null)
                .consignmentId(consignment != null ? consignment.getId() : null)
                .consignorName(consignor != null ? consignor.getFullName() : null)
                .consignorPhone(consignor != null ? consignor.getPhoneNumber() : null)
                .pickupDeadline(itemDisposition.getPickupDeadline())
                .build();
    }
}
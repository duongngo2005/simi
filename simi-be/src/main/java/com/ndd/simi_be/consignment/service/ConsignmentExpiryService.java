package com.ndd.simi_be.consignment.service;

import com.ndd.simi_be.consignment.entity.Consignment;
import com.ndd.simi_be.consignment.entity.ConsignmentItem;
import com.ndd.simi_be.consignment.enums.ConsignmentItemStatus;
import com.ndd.simi_be.consignment.enums.ConsignmentStatus;
import com.ndd.simi_be.consignment.repository.ConsignmentRepository;
import com.ndd.simi_be.product.enums.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsignmentExpiryService {
    private final ConsignmentRepository consignmentRepository;

    @Transactional
    public void processExpiredConsignments(){
        List<Consignment> expiredConsignments = consignmentRepository.findByConsignmentStatusAndExpiryDateBefore(
                ConsignmentStatus.ACTIVE, LocalDateTime.now()
        );

        for (Consignment expiredConsignment: expiredConsignments){
            expiredConsignment.setConsignmentStatus(ConsignmentStatus.PENDING_SETTLEMENT);
            List<ConsignmentItem> consignmentItems = expiredConsignment.getConsignmentItems();
            for (ConsignmentItem item: consignmentItems){
                if (item.getConsignmentItemStatus() == ConsignmentItemStatus.ACTIVE){
                    item.setConsignmentItemStatus(ConsignmentItemStatus.EXPIRED);
                    item.getProduct().setProductStatus(ProductStatus.EXPIRED);
                }
            }
        }
    }
}

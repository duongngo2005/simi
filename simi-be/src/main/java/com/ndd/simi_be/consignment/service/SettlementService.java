package com.ndd.simi_be.consignment.service;

import com.ndd.simi_be.cloudinary.CloudinaryResponse;
import com.ndd.simi_be.cloudinary.CloudinaryService;
import com.ndd.simi_be.common.exception.BadRequestException;
import com.ndd.simi_be.common.exception.ResourceNotFoundException;
import com.ndd.simi_be.consignment.dto.response.ConsignmentItemResponse;
import com.ndd.simi_be.consignment.dto.response.SettlementPreviewResponse;
import com.ndd.simi_be.consignment.dto.response.SettlementResponse;
import com.ndd.simi_be.consignment.entity.Consignment;
import com.ndd.simi_be.consignment.entity.ConsignmentItem;
import com.ndd.simi_be.consignment.entity.ItemDisposition;
import com.ndd.simi_be.consignment.entity.Settlement;
import com.ndd.simi_be.consignment.enums.ConsignmentItemStatus;
import com.ndd.simi_be.consignment.enums.ConsignmentStatus;
import com.ndd.simi_be.consignment.enums.ItemDispositionStatus;
import com.ndd.simi_be.consignment.enums.ItemDispositionType;
import com.ndd.simi_be.consignment.mapper.ConsignmentItemMapper;
import com.ndd.simi_be.consignment.mapper.SettlementMapper;
import com.ndd.simi_be.consignment.repository.ConsignmentRepository;
import com.ndd.simi_be.consignment.repository.ItemDispositionRepository;
import com.ndd.simi_be.consignment.repository.SettlementRepository;
import com.ndd.simi_be.order.entity.OrderItem;
import com.ndd.simi_be.order.enums.OrderStatus;
import com.ndd.simi_be.order.repository.OrderItemRepository;
import com.ndd.simi_be.payment.enums.PaymentMethod;
import com.ndd.simi_be.product.entity.Product;
import com.ndd.simi_be.user.entity.User;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SettlementService {
    private final OrderItemRepository orderItemRepository;
    private final ConsignmentRepository consignmentRepository;
    private final CloudinaryService cloudinaryService;
    private final SettlementRepository settlementRepository;
    private final ItemDispositionRepository itemDispositionRepository;

    @Transactional(readOnly = true)
    public SettlementPreviewResponse previewResponse(Long consignmentId){
        Consignment consignment = consignmentRepository.findById(consignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lô hàng"));

        if (consignment.getConsignmentStatus() != ConsignmentStatus.PENDING_SETTLEMENT){
            throw new BadRequestException("Trạng thái lô hàng không hợp lệ");
        }

        SettlementCalc settlementCalc = calcSettlement(consignment);

        return SettlementPreviewResponse.builder()
                .consignorId(settlementCalc.getConsignorId())
                .consignorName(settlementCalc.getConsignorName())
                .bankName(settlementCalc.getBankName())
                .accountNumber(settlementCalc.getAccountNumber())
                .accountHolder(settlementCalc.getAccountHolder())
                .totalSoldAmount(settlementCalc.getTotalSoldAmount())
                .totalCommissionAmount(settlementCalc.getTotalCommissionAmount())
                .netAmount(settlementCalc.getNetAmount())
                .canSettle(settlementCalc.isCanSettle())
                .soldItemCount(settlementCalc.getSoldItems().size())
                .soldItems(settlementCalc.getSoldItemResponses())
                .reserveItems(settlementCalc.getReserveItemResponses())
                .returnItems(settlementCalc.getReturnItemResponses())
                .build();
    }

    @Transactional
    public SettlementResponse createSettlement(
            Long consignmentId, User processedBy, MultipartFile proofImage
    ){
        Consignment consignment = consignmentRepository.findById(consignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lô hàng"));

        if (consignment.getConsignmentStatus() != ConsignmentStatus.PENDING_SETTLEMENT){
            throw new BadRequestException("Trạng thái lô hàng không hợp lệ");
        }

        SettlementCalc settlementCalc = calcSettlement(consignment);
        if (!settlementCalc.isCanSettle()){
            throw new BadRequestException("Chưa đủ điều kiện để kết toán");
        }

        CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadImage(proofImage);

        Settlement settlement = Settlement.builder()
                .consignment(consignment)
                .processedBy(processedBy)
                .proofImageUrl(cloudinaryResponse.getUrl())
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .settlementItems(settlementCalc.getSoldItems())
                .netAmount(settlementCalc.getNetAmount())
                .totalSoldAmount(settlementCalc.getTotalSoldAmount())
                .totalCommissionAmount(settlementCalc.getTotalCommissionAmount())
                .accountHolder(settlementCalc.getAccountHolder())
                .accountNumber(settlementCalc.getAccountNumber())
                .bankName(settlementCalc.getBankName())
                .settledAt(LocalDateTime.now())
                .build();
        settlementRepository.save(settlement);

        for (ConsignmentItem item : settlementCalc.getSoldItems()){
            item.setSettlement(settlement);
        }

        for (ConsignmentItem item : settlementCalc.getReturnItems()){
            ItemDisposition itemDisposition = ItemDisposition.builder()
                    .itemDispositionStatus(ItemDispositionStatus.PENDING)
                    .consignmentItem(item)
                    .itemDispositionType(ItemDispositionType.RETURN)
                    .pickupDeadline(LocalDateTime.now().plusDays(7))
                    .processedBy(processedBy)
                    .processedAt(null)
                    .build();
            itemDispositionRepository.save(itemDisposition);
        }

        consignment.setConsignmentStatus(ConsignmentStatus.SETTLED);
        consignment.setSettledAt(LocalDateTime.now());
        return SettlementMapper.toSettlementResponse(settlementRepository.save(settlement));
    }

    @Builder
    @Getter
    private static class SettlementCalc{
        private BigDecimal totalSoldAmount;
        private BigDecimal totalCommissionAmount;
        private BigDecimal netAmount;
        private List<ConsignmentItem> soldItems;
        private List<ConsignmentItem> returnItems;
        private List<ConsignmentItem> reserveItems;
        private List<ConsignmentItemResponse> soldItemResponses;
        private List<ConsignmentItemResponse> returnItemResponses;
        private List<ConsignmentItemResponse> reserveItemResponses;
        private Long consignorId;
        private String consignorName;
        private String bankName;
        private String accountHolder;
        private String accountNumber;

        private boolean canSettle;
    }

    private SettlementCalc calcSettlement(Consignment consignment){
        boolean canSettle = true;

        BigDecimal totalSoldAmount = BigDecimal.ZERO;
        BigDecimal totalCommissionAmount = BigDecimal.ZERO;
        BigDecimal netAmount = BigDecimal.ZERO;

        List<ConsignmentItem> soldItems = new ArrayList<>();
        List<ConsignmentItem> returnItems = new ArrayList<>();

        for (ConsignmentItem item : consignment.getConsignmentItems()){
            if (item.getConsignmentItemStatus() == ConsignmentItemStatus.SOLD){
                Product product = item.getProduct();
                OrderItem orderItem = orderItemRepository.findFirstByProductAndOrder_OrderStatus(product, OrderStatus.COMPLETED)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chi tiết đơn hàng"));

                BigDecimal unitPrice = orderItem.getUnitPrice();
                totalSoldAmount = totalSoldAmount.add(unitPrice);
                totalCommissionAmount = totalCommissionAmount.add(unitPrice.multiply(item.getCommissionRate()));
                netAmount = netAmount.add(unitPrice.multiply(BigDecimal.ONE.subtract(item.getCommissionRate())));
                soldItems.add(item);

            }else if (item.getConsignmentItemStatus() == ConsignmentItemStatus.EXPIRED){
                returnItems.add(item);
            }
        }

        List<ConsignmentItem> reserveItems = consignment.getConsignmentItems().stream()
                .filter(item -> item.getConsignmentItemStatus() == ConsignmentItemStatus.RESERVED)
                .toList();

        if (!reserveItems.isEmpty()){
            canSettle = false;
        }

        User consignor = consignment.getConsignor();

        if ( !StringUtils.hasText(consignor.getAccountNumber())
                || !StringUtils.hasText(consignor.getBankName())
                || !StringUtils.hasText(consignor.getAccountHolder())){
            canSettle = false;
        }

        return SettlementCalc.builder()
                .totalSoldAmount(totalSoldAmount)
                .totalCommissionAmount(totalCommissionAmount)
                .netAmount(netAmount)
                .soldItems(soldItems)
                .reserveItems(reserveItems)
                .returnItems(returnItems)
                .canSettle(canSettle)
                .returnItemResponses(returnItems.stream().map(ConsignmentItemMapper::toConsignmentItemResponse).toList())
                .reserveItemResponses(reserveItems.stream().map(ConsignmentItemMapper::toConsignmentItemResponse).toList())
                .soldItemResponses(soldItems.stream().map(ConsignmentItemMapper::toConsignmentItemResponse).toList())
                .accountHolder(consignor.getAccountHolder())
                .accountNumber(consignor.getAccountNumber())
                .consignorName(consignor.getFullName())
                .consignorId(consignor.getId())
                .bankName(consignor.getBankName())
                .build();
    }

    @Transactional(readOnly = true)
    public SettlementResponse getSettlement(Long consignmentId){
        Settlement settlement = settlementRepository.findByConsignmentId(consignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu quyết toán"));
        return SettlementMapper.toSettlementResponse(settlement);
    }

    @Transactional(readOnly = true)
    public List<SettlementResponse> getMySettlements(User consignor){
        List<Settlement> settlements = settlementRepository.findByConsignment_ConsignorOrderBySettledAtDesc(consignor);
        return settlements.stream().map(SettlementMapper::toSettlementResponse).toList();
    }
}
package com.polinakulyk.cashregister.manager.mapper;

import com.polinakulyk.cashregister.db.entity.Receipt;
import com.polinakulyk.cashregister.db.entity.ReceiptItem;
import com.polinakulyk.cashregister.service.api.dto.ReceiptDto;
import com.polinakulyk.cashregister.service.api.dto.ReceiptItemDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * The purpose of this class is to map {@link Receipt} to {@link ReceiptDto} and vice versa.
 * With MapStruct we only need to create the interface, and the library will
 * automatically create a concrete implementation during compile time.
 */
@Mapper(componentModel = "spring")
public interface ReceiptMapper {
    @Mappings({
            @Mapping(source = "receipt.id", target = "id"),
            @Mapping(source = "receipt.createdTime", target = "createdTime"),
            @Mapping(source = "receipt.checkoutTime", target = "checkoutTime"),
            @Mapping(source = "receipt.status", target = "status"),
            @Mapping(source = "receipt.sumTotal", target = "sumTotal"),
            @Mapping(source = "receipt.receiptItems", target = "receiptItems"),
            @Mapping(source = "receipt.user", target = "user"),
    })
    ReceiptDto receiptToReceiptDto(Receipt receipt);

    @Mappings({
            @Mapping(source = "receiptDto.id", target = "id"),
            @Mapping(source = "receiptDto.createdTime", target = "createdTime"),
            @Mapping(source = "receiptDto.checkoutTime", target = "checkoutTime"),
            @Mapping(source = "receiptDto.status", target = "status"),
            @Mapping(source = "receiptDto.sumTotal", target = "sumTotal"),
            @Mapping(source = "receiptDto.receiptItems", target = "receiptItems"),
            @Mapping(source = "receiptDto.user", target = "user"),
    })
    Receipt receiptDtoToReceipt(ReceiptDto receiptDto);
}

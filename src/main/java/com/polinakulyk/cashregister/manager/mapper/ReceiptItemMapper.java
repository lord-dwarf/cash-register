package com.polinakulyk.cashregister.manager.mapper;

import com.polinakulyk.cashregister.db.entity.ReceiptItem;
import com.polinakulyk.cashregister.service.api.dto.ReceiptItemDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * The purpose of this class is to map {@link ReceiptItem} to {@link ReceiptItemDto}.
 * With MapStruct we only need to create the interface, and the library will
 * automatically create a concrete implementation during compile time.
 */
@Mapper(componentModel = "spring")
public interface ReceiptItemMapper {
    @Mappings({
            @Mapping(source = "receiptItem.id", target = "id"),
            @Mapping(source = "receiptItem.product", target = "product"),
            @Mapping(source = "receiptItem.name", target = "name"),
            @Mapping(source = "receiptItem.amount", target = "amount"),
            @Mapping(source = "receiptItem.amountUnit", target = "amountUnit"),
            @Mapping(source = "receiptItem.price", target = "price"),
    })
    ReceiptItemDto receiptItemToReceiptItemDto(ReceiptItem receiptItem);
}

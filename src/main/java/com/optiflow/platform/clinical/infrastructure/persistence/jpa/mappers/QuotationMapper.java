package com.optiflow.platform.clinical.infrastructure.persistence.jpa.mappers;

import com.optiflow.platform.clinical.domain.entities.Quotation;
import com.optiflow.platform.clinical.domain.entities.QuotationItem;
import com.optiflow.platform.clinical.domain.valueobjects.ClinicalRecordId;
import com.optiflow.platform.clinical.domain.valueobjects.Discount;
import com.optiflow.platform.clinical.domain.valueobjects.DiscountType;
import com.optiflow.platform.clinical.domain.valueobjects.Money;
import com.optiflow.platform.clinical.domain.valueobjects.QuotationId;
import com.optiflow.platform.clinical.domain.valueobjects.QuotationItemId;
import com.optiflow.platform.clinical.domain.valueobjects.QuotationItemType;
import com.optiflow.platform.clinical.domain.valueobjects.QuotationStatus;
import com.optiflow.platform.clinical.infrastructure.persistence.jpa.entities.QuotationEntity;
import com.optiflow.platform.clinical.infrastructure.persistence.jpa.entities.QuotationItemEntity;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.stereotype.Component;

@Component
public class QuotationMapper {

  public Quotation toDomain(QuotationEntity entity, List<QuotationItemEntity> itemEntities) {
    Discount discount = entity.getDiscountType() == null
        ? null
        : new Discount(
            DiscountType.valueOf(entity.getDiscountType()),
            entity.getDiscountValue(),
            entity.getDiscountReason());
    return Quotation.reconstitute(
        QuotationId.of(entity.getId()),
        ClinicalRecordId.of(entity.getClinicalRecordId()),
        itemEntities.stream().map(this::toItem).toList(),
        discount,
        Money.of(entity.getTotal()),
        QuotationStatus.valueOf(entity.getStatus()),
        entity.getRejectionReason());
  }

  public QuotationEntity toEntity(Quotation quotation) {
    QuotationEntity entity = new QuotationEntity();
    entity.setId(quotation.id().value());
    entity.setClinicalRecordId(quotation.clinicalRecordId().value());
    quotation.discount().ifPresent(discount -> {
      entity.setDiscountType(discount.type().name());
      entity.setDiscountValue(discount.value());
      entity.setDiscountReason(discount.reason());
    });
    entity.setTotal(quotation.total().amount());
    entity.setStatus(quotation.status().name());
    entity.setRejectionReason(quotation.rejectionReason());
    return entity;
  }

  public List<QuotationItemEntity> toItemEntities(Quotation quotation) {
    List<QuotationItem> items = quotation.items();
    return IntStream.range(0, items.size())
        .mapToObj(index -> toItemEntity(quotation.id(), items.get(index), index + 1))
        .toList();
  }

  private QuotationItemEntity toItemEntity(
      QuotationId quotationId, QuotationItem item, int lineNumber) {
    QuotationItemEntity entity = new QuotationItemEntity();
    entity.setId(item.id().value());
    entity.setQuotationId(quotationId.value());
    entity.setLineNumber(lineNumber);
    entity.setItemType(item.itemType().name());
    entity.setProductSku(item.productSku());
    entity.setDescription(item.description());
    entity.setUnitPrice(item.unitPrice().amount());
    entity.setQuantity(item.quantity());
    return entity;
  }

  private QuotationItem toItem(QuotationItemEntity entity) {
    return QuotationItem.reconstitute(
        QuotationItemId.of(entity.getId()),
        QuotationItemType.valueOf(entity.getItemType()),
        entity.getProductSku(),
        entity.getDescription(),
        Money.of(entity.getUnitPrice()),
        entity.getQuantity());
  }
}

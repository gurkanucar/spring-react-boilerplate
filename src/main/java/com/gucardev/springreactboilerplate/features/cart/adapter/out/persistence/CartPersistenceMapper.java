package com.gucardev.springreactboilerplate.features.cart.adapter.out.persistence;

import com.gucardev.springreactboilerplate.features.cart.domain.model.Cart;
import com.gucardev.springreactboilerplate.features.cart.domain.model.CartLine;
import com.gucardev.springreactboilerplate.features.cart.domain.model.Coupon;
import com.gucardev.springreactboilerplate.features.cart.domain.model.CouponType;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Translates between the {@link Cart} domain model and the {@link CartJpaEntity} (plus its JSON
 * documents). Hand-written because it spans the audit fields on {@code BaseEntity} via the super-builder
 * and unwraps the aggregate's value objects into flat JSON records.
 *
 * <p>On the way <b>out</b> the value objects are unwrapped to primitives; on the way <b>in</b> the
 * domain factories ({@code CartLine.fromPersistence}, {@code Coupon.of}, {@code Cart.fromPersistence})
 * rebuild the validated value objects, so a cart loaded from the DB is just as well-formed as one
 * created through the API.
 */
@Component
public class CartPersistenceMapper {

    Cart toDomain(CartJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        List<CartLine> lines = entity.getLines().stream()
                .map(d -> CartLine.fromPersistence(d.sku(), d.productName(), d.unitPrice(), d.quantity()))
                .toList();
        Coupon coupon = entity.getCoupon() == null ? null : toCoupon(entity.getCoupon());
        return Cart.fromPersistence(
                entity.getId(),
                entity.getCustomerName(),
                entity.getStatus(),
                lines,
                coupon,
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedBy());
    }

    CartJpaEntity toEntity(Cart cart) {
        if (cart == null) {
            return null;
        }
        List<CartLineDocument> lines = cart.getLines().stream()
                .map(l -> new CartLineDocument(
                        l.getSku().value(),
                        l.getProductName().value(),
                        l.getUnitPrice().value(),
                        l.getQuantity().value()))
                .toList();
        CartCouponDocument coupon = cart.getCoupon() == null ? null : toCouponDocument(cart.getCoupon());
        return CartJpaEntity.builder()
                .id(cart.getId())
                .customerName(cart.getCustomerName())
                .status(cart.getStatus())
                .lines(lines)
                .coupon(coupon)
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .createdBy(cart.getCreatedBy())
                .updatedBy(cart.getUpdatedBy())
                .build();
    }

    private Coupon toCoupon(CartCouponDocument d) {
        return Coupon.of(d.code(), CouponType.valueOf(d.type()), d.value(), d.minSubtotal(), d.maxDiscount());
    }

    private CartCouponDocument toCouponDocument(Coupon coupon) {
        return new CartCouponDocument(
                coupon.getCode(),
                coupon.getType().name(),
                coupon.getValue(),
                coupon.getMinSubtotal().value(),
                coupon.getMaxDiscount() == null ? null : coupon.getMaxDiscount().value());
    }
}

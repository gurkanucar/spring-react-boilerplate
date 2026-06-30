package com.gucardev.springreactboilerplate.features.cart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gucardev.springreactboilerplate.features.cart.domain.exception.CartLineNotFoundException;
import com.gucardev.springreactboilerplate.features.cart.domain.exception.CartOperationException;
import com.gucardev.springreactboilerplate.features.cart.domain.exception.InvalidCartException;
import com.gucardev.springreactboilerplate.features.cart.domain.model.Cart;
import com.gucardev.springreactboilerplate.features.cart.domain.model.CartStatus;
import com.gucardev.springreactboilerplate.features.cart.domain.model.CartTotals;
import com.gucardev.springreactboilerplate.features.cart.domain.model.CouponType;
import com.gucardev.springreactboilerplate.features.cart.domain.model.Money;
import com.gucardev.springreactboilerplate.features.shared.domain.DomainException;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Pure-domain unit test for the rich {@link Cart} aggregate. No Spring, no database — the whole point
 * is that the business rules live in the model, so they can be exercised in isolation. Each rule the
 * aggregate enforces gets a case here; the application/web/persistence layers around it are just thin
 * plumbing and are covered by their own tests.
 */
class CartTest {

    private static BigDecimal bd(String v) {
        return new BigDecimal(v);
    }

    private static Cart cartWith(String sku, String name, String price, int qty) {
        Cart cart = Cart.create("Ada Lovelace");
        cart.addItem(sku, name, bd(price), qty);
        return cart;
    }

    @Nested
    @DisplayName("lines")
    class Lines {

        @Test
        void newCart_isActiveAndEmpty() {
            Cart cart = Cart.create("Ada");
            assertThat(cart.getStatus()).isEqualTo(CartStatus.ACTIVE);
            assertThat(cart.isEmpty()).isTrue();
            assertThat(cart.subtotal()).isEqualTo(Money.zero());
        }

        @Test
        void reAddingSameSku_mergesIntoOneLine_caseInsensitive() {
            Cart cart = cartWith("kbd-01", "Keyboard", "100.00", 1);
            cart.addItem("KBD-01", "Keyboard", bd("100.00"), 2);

            assertThat(cart.getLines()).hasSize(1);
            assertThat(cart.getLines().get(0).getQuantity().value()).isEqualTo(3);
            assertThat(cart.subtotal()).isEqualTo(Money.of(bd("300.00")));
        }

        @Test
        void addingBeyondPerLineLimit_isRejected() {
            Cart cart = Cart.create("Grace");
            assertThatThrownBy(() -> cart.addItem("x-1", "Thing", bd("1.00"), 100))
                    .isInstanceOf(CartOperationException.class)
                    .extracting("code").isEqualTo("CART_LINE_QUANTITY_LIMIT");
            assertThat(cart.isEmpty()).isTrue();
        }

        @Test
        void changingQuantityToZero_removesTheLine() {
            Cart cart = cartWith("a-1", "A", "5.00", 4);
            cart.addItem("b-1", "B", bd("5.00"), 1);

            cart.changeQuantity("a-1", 0);

            assertThat(cart.getLines()).hasSize(1);
            assertThat(cart.getLines().get(0).getSku().value()).isEqualTo("B-1");
        }

        @Test
        void operatingOnMissingLine_is404() {
            Cart cart = cartWith("a-1", "A", "5.00", 1);
            assertThatThrownBy(() -> cart.removeItem("zzz-9"))
                    .isInstanceOf(CartLineNotFoundException.class);
            assertThatThrownBy(() -> cart.changeQuantity("zzz-9", 2))
                    .isInstanceOf(CartLineNotFoundException.class);
        }

        @Test
        void getLines_isAnUnmodifiableCopy() {
            Cart cart = cartWith("a-1", "A", "5.00", 1);
            assertThatThrownBy(() -> cart.getLines().clear())
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("coupons")
    class Coupons {

        @Test
        void couponBelowMinimumSubtotal_isRejectedAtApply() {
            Cart cart = cartWith("p-1", "Pricey", "100.00", 1); // subtotal 100
            assertThatThrownBy(() ->
                    cart.applyCoupon("SAVE", CouponType.PERCENTAGE, bd("10"), bd("200.00"), null))
                    .isInstanceOf(CartOperationException.class)
                    .extracting("code").isEqualTo("CART_COUPON_MIN_SUBTOTAL");
        }

        @Test
        void percentageCoupon_isCappedAtMaxDiscount() {
            Cart cart = cartWith("p-1", "Pricey", "100.00", 1); // subtotal 100
            cart.applyCoupon("CAP5", CouponType.PERCENTAGE, bd("10"), bd("50.00"), bd("5.00"));

            CartTotals totals = cart.totals();
            assertThat(totals.getDiscount()).isEqualTo(Money.of(bd("5.00"))); // min(10, 5)
            assertThat(totals.getShipping()).isEqualTo(Money.of(bd("15.00")));
            assertThat(totals.getTotal()).isEqualTo(Money.of(bd("110.00"))); // 100 - 5 + 15
        }

        @Test
        void onlyOneCoupon_atATime() {
            Cart cart = cartWith("p-1", "Pricey", "100.00", 1);
            cart.applyCoupon("FIRST", CouponType.FIXED, bd("3.00"), null, null);
            assertThatThrownBy(() ->
                    cart.applyCoupon("SECOND", CouponType.FIXED, bd("4.00"), null, null))
                    .isInstanceOf(CartOperationException.class)
                    .extracting("code").isEqualTo("CART_COUPON_ALREADY_APPLIED");
        }

        @Test
        void cannotApplyCouponToEmptyCart() {
            Cart cart = Cart.create("Edsger");
            assertThatThrownBy(() ->
                    cart.applyCoupon("ANY", CouponType.FIXED, bd("1.00"), null, null))
                    .isInstanceOf(CartOperationException.class)
                    .extracting("code").isEqualTo("CART_COUPON_EMPTY_CART");
        }

        @Test
        void fixedCoupon_neverExceedsSubtotal_andTotalNeverGoesNegative() {
            Cart cart = cartWith("q-1", "Q", "200.00", 1); // subtotal 200
            cart.applyCoupon("FIX300", CouponType.FIXED, bd("300.00"), null, null);

            CartTotals totals = cart.totals();
            assertThat(totals.getDiscount()).isEqualTo(Money.of(bd("200.00"))); // clamped to subtotal
            assertThat(totals.getTotal()).isEqualTo(Money.of(bd("15.00")));      // 0 + flat shipping
        }

        @Test
        void couponDeactivates_whenSubtotalLaterDropsBelowItsMinimum() {
            Cart cart = cartWith("s-1", "S", "60.00", 2); // subtotal 120
            cart.applyCoupon("MIN100", CouponType.PERCENTAGE, bd("10"), bd("100.00"), null);
            assertThat(cart.totals().getDiscount()).isEqualTo(Money.of(bd("12.00")));

            cart.changeQuantity("s-1", 1); // subtotal now 60, below the coupon's minimum of 100
            assertThat(cart.totals().getDiscount()).isEqualTo(Money.zero());
        }

        @Test
        void removeCoupon_clearsTheDiscount() {
            Cart cart = cartWith("p-1", "Pricey", "100.00", 1);
            cart.applyCoupon("OFF10", CouponType.PERCENTAGE, bd("10"), null, null);
            assertThat(cart.getCoupon()).isNotNull();

            cart.removeCoupon();

            assertThat(cart.getCoupon()).isNull();
            assertThat(cart.totals().getDiscount()).isEqualTo(Money.zero());
        }
    }

    @Nested
    @DisplayName("shipping")
    class Shipping {

        @Test
        void freeShipping_whenSubtotalReachesThreshold() {
            Cart cart = cartWith("r-1", "R", "150.00", 1); // exactly at the threshold
            CartTotals totals = cart.totals();
            assertThat(totals.getShipping()).isEqualTo(Money.zero());
            assertThat(totals.getTotal()).isEqualTo(Money.of(bd("150.00")));
        }

        @Test
        void shippingUsesPostDiscountSubtotal() {
            Cart cart = cartWith("r-1", "R", "160.00", 1);               // subtotal 160 (>= 150)
            cart.applyCoupon("OFF20", CouponType.FIXED, bd("20.00"), null, null); // payable 140 (< 150)
            assertThat(cart.totals().getShipping()).isEqualTo(Money.of(bd("15.00")));
        }
    }

    @Nested
    @DisplayName("checkout lifecycle")
    class Checkout {

        @Test
        void cannotCheckoutEmptyCart() {
            Cart cart = Cart.create("Margaret");
            assertThatThrownBy(cart::checkout)
                    .isInstanceOf(CartOperationException.class)
                    .extracting("code").isEqualTo("CART_CHECKOUT_EMPTY");
        }

        @Test
        void checkoutFreezesTheCart() {
            Cart cart = cartWith("t-1", "T", "10.00", 1);
            cart.checkout();

            assertThat(cart.isCheckedOut()).isTrue();
            assertThatThrownBy(() -> cart.addItem("u-1", "U", bd("1.00"), 1))
                    .isInstanceOf(CartOperationException.class)
                    .extracting("code").isEqualTo("CART_NOT_ACTIVE");
            assertThatThrownBy(cart::checkout)
                    .isInstanceOf(CartOperationException.class)
                    .extracting("code").isEqualTo("CART_NOT_ACTIVE");
        }
    }

    @Nested
    @DisplayName("value-object invariants map to the right category")
    class Invariants {

        @Test
        void invalidInputs_areValidationCategory() {
            assertThat(catch_(() -> Cart.create(" ")).getCategory()).isEqualTo(DomainException.Category.VALIDATION);
            assertThat(catch_(() -> cartWith("a", "ok", "1.00", 1)).getCode()).isEqualTo("CART_SKU_INVALID");
            assertThat(catch_(() -> cartWith("a-1", "ok", "-1.00", 1)).getCode()).isEqualTo("CART_AMOUNT_NEGATIVE");
        }

        @Test
        void invalidCart_isInvalidCartException() {
            assertThatThrownBy(() -> Cart.create(null)).isInstanceOf(InvalidCartException.class);
        }

        private DomainException catch_(Runnable r) {
            try {
                r.run();
                throw new AssertionError("expected a DomainException");
            } catch (DomainException e) {
                return e;
            }
        }
    }
}

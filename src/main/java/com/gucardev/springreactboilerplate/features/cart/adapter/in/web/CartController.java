package com.gucardev.springreactboilerplate.features.cart.adapter.in.web;

import com.gucardev.springreactboilerplate.features.cart.adapter.in.web.dto.AddCartItemRequest;
import com.gucardev.springreactboilerplate.features.cart.adapter.in.web.dto.ApplyCouponRequest;
import com.gucardev.springreactboilerplate.features.cart.adapter.in.web.dto.CartResponse;
import com.gucardev.springreactboilerplate.features.cart.adapter.in.web.dto.CreateCartRequest;
import com.gucardev.springreactboilerplate.features.cart.adapter.in.web.dto.UpdateCartItemQuantityRequest;
import com.gucardev.springreactboilerplate.features.cart.application.port.in.AddCartItemCommand;
import com.gucardev.springreactboilerplate.features.cart.application.port.in.AddCartItemUseCase;
import com.gucardev.springreactboilerplate.features.cart.application.port.in.ApplyCouponCommand;
import com.gucardev.springreactboilerplate.features.cart.application.port.in.ApplyCouponUseCase;
import com.gucardev.springreactboilerplate.features.cart.application.port.in.CheckoutCartUseCase;
import com.gucardev.springreactboilerplate.features.cart.application.port.in.CreateCartCommand;
import com.gucardev.springreactboilerplate.features.cart.application.port.in.CreateCartUseCase;
import com.gucardev.springreactboilerplate.features.cart.application.port.in.GetCartUseCase;
import com.gucardev.springreactboilerplate.features.cart.application.port.in.RemoveCartItemUseCase;
import com.gucardev.springreactboilerplate.features.cart.application.port.in.RemoveCouponUseCase;
import com.gucardev.springreactboilerplate.features.cart.application.port.in.UpdateCartItemQuantityCommand;
import com.gucardev.springreactboilerplate.features.cart.application.port.in.UpdateCartItemQuantityUseCase;
import com.gucardev.springreactboilerplate.infra.config.response.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Driving (web) adapter for the shopping-cart rich-domain demo. Every endpoint follows the same shape:
 * translate the HTTP request into a command / arguments, call a narrow input port, and map the returned
 * {@code Cart} (priced out via its {@code totals()}) back to a {@link CartResponse}. The controller
 * holds <b>no</b> business logic — all the rules live in the {@code Cart} aggregate and its value
 * objects, which is the whole point of this feature.
 *
 * <p>Rule violations surface as the right HTTP status automatically: the aggregate throws a
 * {@code DomainException} whose category the {@code GlobalExceptionHandler} maps — 422 for invalid
 * input (bad SKU, negative price), 409 for illegal operations (modifying a checked-out cart, a coupon
 * below its minimum), 404 for a missing line.
 */
@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
@Tag(name = "Carts (Rich Domain)", description = "A business-rule-heavy aggregate: lines, coupons, pricing and a checkout lifecycle.")
public class CartController {

    private final CreateCartUseCase createCartUseCase;
    private final GetCartUseCase getCartUseCase;
    private final AddCartItemUseCase addCartItemUseCase;
    private final UpdateCartItemQuantityUseCase updateCartItemQuantityUseCase;
    private final RemoveCartItemUseCase removeCartItemUseCase;
    private final ApplyCouponUseCase applyCouponUseCase;
    private final RemoveCouponUseCase removeCouponUseCase;
    private final CheckoutCartUseCase checkoutCartUseCase;
    private final CartWebMapper cartWebMapper;

    @Operation(summary = "Open a new, empty cart")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<CartResponse>> create(
            @Valid @RequestBody CreateCartRequest request) {
        CartResponse response = cartWebMapper.toResponse(
                createCartUseCase.create(new CreateCartCommand(request.customerName())));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseWrapper.created(response));
    }

    @Operation(summary = "Get a cart, priced out (subtotal, discount, shipping, total)")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<CartResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(cartWebMapper.toResponse(getCartUseCase.getById(id))));
    }

    @Operation(summary = "Add a product (re-adding a SKU merges into the existing line)")
    @PostMapping("/{id}/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<CartResponse>> addItem(
            @PathVariable UUID id, @Valid @RequestBody AddCartItemRequest request) {
        CartResponse response = cartWebMapper.toResponse(addCartItemUseCase.addItem(
                new AddCartItemCommand(id, request.sku(), request.productName(),
                        request.unitPrice(), request.quantity())));
        return ResponseEntity.ok(ApiResponseWrapper.ok(response));
    }

    @Operation(summary = "Set a line's quantity (0 removes it)")
    @PutMapping("/{id}/items/{sku}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<CartResponse>> updateQuantity(
            @PathVariable UUID id, @PathVariable String sku,
            @Valid @RequestBody UpdateCartItemQuantityRequest request) {
        CartResponse response = cartWebMapper.toResponse(updateCartItemQuantityUseCase.updateQuantity(
                new UpdateCartItemQuantityCommand(id, sku, request.quantity())));
        return ResponseEntity.ok(ApiResponseWrapper.ok(response));
    }

    @Operation(summary = "Remove a line by SKU")
    @DeleteMapping("/{id}/items/{sku}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<CartResponse>> removeItem(
            @PathVariable UUID id, @PathVariable String sku) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(
                cartWebMapper.toResponse(removeCartItemUseCase.removeItem(id, sku))));
    }

    @Operation(summary = "Apply a discount coupon")
    @PostMapping("/{id}/coupon")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<CartResponse>> applyCoupon(
            @PathVariable UUID id, @Valid @RequestBody ApplyCouponRequest request) {
        CartResponse response = cartWebMapper.toResponse(applyCouponUseCase.applyCoupon(
                new ApplyCouponCommand(id, request.code(), request.type(), request.value(),
                        request.minSubtotal(), request.maxDiscount())));
        return ResponseEntity.ok(ApiResponseWrapper.ok(response));
    }

    @Operation(summary = "Remove the applied coupon")
    @DeleteMapping("/{id}/coupon")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<CartResponse>> removeCoupon(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(
                cartWebMapper.toResponse(removeCouponUseCase.removeCoupon(id))));
    }

    @Operation(summary = "Check the cart out (locks it for ordering)")
    @PostMapping("/{id}/checkout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<CartResponse>> checkout(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(
                cartWebMapper.toResponse(checkoutCartUseCase.checkout(id))));
    }
}

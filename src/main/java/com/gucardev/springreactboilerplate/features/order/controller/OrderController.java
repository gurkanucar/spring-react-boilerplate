package com.gucardev.springreactboilerplate.features.order.controller;

import com.gucardev.springreactboilerplate.features.order.model.dto.OrderResponse;
import com.gucardev.springreactboilerplate.features.order.model.request.PlaceOrderRequest;
import com.gucardev.springreactboilerplate.features.order.service.usecase.GetOrderUseCase;
import com.gucardev.springreactboilerplate.features.order.service.usecase.PlaceOrderUseCase;
import com.gucardev.springreactboilerplate.infra.config.response.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Demonstrates the transactional outbox pattern: placing an order writes the order + an
 * {@code OrderCreated} event in one transaction; a ShedLock-guarded relay later publishes the event
 * to RabbitMQ; a consumer handles it idempotently and flips the order to CONFIRMED. Poll
 * {@code GET /{id}} after placing one to watch the status change.
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders (Outbox)", description = "Place orders and publish events via the transactional outbox.")
public class OrderController {

    private final PlaceOrderUseCase placeOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;

    @Operation(summary = "Place an order (persists order + OrderCreated outbox event atomically)")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<OrderResponse>> place(
            @Valid @RequestBody PlaceOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.created(placeOrderUseCase.execute(request)));
    }

    @Operation(summary = "Get an order (watch its status flip to CONFIRMED after the event is consumed)")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<OrderResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(getOrderUseCase.execute(id)));
    }
}

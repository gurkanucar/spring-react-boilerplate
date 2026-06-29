package com.gucardev.springreactboilerplate.features.order.adapter.in.web;

import com.gucardev.springreactboilerplate.features.order.adapter.in.web.dto.OrderResponse;
import com.gucardev.springreactboilerplate.features.order.adapter.in.web.dto.PlaceOrderRequest;
import com.gucardev.springreactboilerplate.features.order.application.port.in.GetOrderUseCase;
import com.gucardev.springreactboilerplate.features.order.application.port.in.PlaceOrderCommand;
import com.gucardev.springreactboilerplate.features.order.application.port.in.PlaceOrderUseCase;
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
 * Driving (web) adapter for the outbox demo: placing an order writes the order + an
 * {@code OrderCreated} event in one transaction; a ShedLock-guarded relay later publishes the event
 * to RabbitMQ; a consumer handles it idempotently and flips the order to CONFIRMED. Poll
 * {@code GET /{id}} after placing one to watch the status change.
 *
 * <p>The controller only talks to input ports and maps between web DTOs and the domain model.
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders (Outbox)", description = "Place orders and publish events via the transactional outbox.")
public class OrderController {

    private final PlaceOrderUseCase placeOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final OrderWebMapper orderWebMapper;

    @Operation(summary = "Place an order (persists order + OrderCreated outbox event atomically)")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<OrderResponse>> place(
            @Valid @RequestBody PlaceOrderRequest request) {
        OrderResponse response = orderWebMapper.toResponse(placeOrderUseCase.place(
                new PlaceOrderCommand(
                        request.customerName(),
                        request.product(),
                        request.quantity(),
                        request.amount())));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseWrapper.created(response));
    }

    @Operation(summary = "Get an order (watch its status flip to CONFIRMED after the event is consumed)")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<OrderResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseWrapper.ok(orderWebMapper.toResponse(getOrderUseCase.getById(id))));
    }
}

package com.owned.commerce_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutRequest {

    @NotEmpty(message = "Cart cannot be empty")
    @Valid
    private List<CartItemDto> items;
}

package com.ecommerce.furkan.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuccessEvent {
    private String success;
}
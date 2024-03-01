package com.lcdw.electronic.store.dtos;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AddItemToRequest {

    private String productId;
    private int Quantity;
}

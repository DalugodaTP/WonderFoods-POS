package dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderDetailsDto {
    private String orderId;
    private String itemCode;
    private int qty;
    private double unitPrice;
}

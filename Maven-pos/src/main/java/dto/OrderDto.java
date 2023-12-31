package dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class OrderDto {
    private String orderId;
    private String date;
    private String custId;
    //store details of all the Orders
    private List<OrderDetailsDto> list;
}

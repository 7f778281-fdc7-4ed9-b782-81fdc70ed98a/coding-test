package com.seowon.coding.domain.model;

import java.util.List;
import com.seowon.coding.service.OrderProduct;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderReq{
    private String customerName;

    private String customerEmail;

    private List<Long> productIds;

    private List<Integer> quantoties;
}
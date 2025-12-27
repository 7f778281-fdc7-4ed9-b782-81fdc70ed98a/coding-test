package com.seowon.coding.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    String customerName;
    String customerEmail;
    List<ProductListDto> products;
}

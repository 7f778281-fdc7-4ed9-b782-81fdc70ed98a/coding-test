package com.seowon.coding.service;

import com.seowon.coding.domain.model.Order;
import com.seowon.coding.domain.model.OrderItem;
import com.seowon.coding.domain.model.Product;
import com.seowon.coding.domain.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final ProductService productService;

    public OrderItemService(OrderItemRepository orderItemRepository, ProductService productService) {
        this.orderItemRepository = orderItemRepository;
        this.productService = productService;
    }

    public List<OrderItem> findAllOrderItem() {

        return orderItemRepository.findAll();
    }

    public List<OrderItem> findByProduct(Product product) {

        return orderItemRepository.findAllByProduct(product);
    }

    public void addItem(Order order,List<Long> productList, List<Integer> quantityList){


        for(int i=0; i<productList.size(); i++){

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(productService.getProductsByIds(productList).get(i));
            orderItem.setQuantity(quantityList.get(i));
            orderItem.setPrice(order.getTotalAmount());
        }
    }

    public List<OrderItem> findByProductIds(List<Long> productIds) {

        List<OrderItem> list = new ArrayList<>();

        for(int i = 0, size = productIds.size(); i < size; i++) {
            OrderItem orderItem = new OrderItem();

            List<Product> productList = productService.getProductById(productIds.get(i));
            orderItemRepository.findAllByProduct(productList.get(i));

            orderItem.setId(orderItemRepository.findAllByProduct(productList.get(i)).get(i).getId());
            orderItem.setOrder(orderItemRepository.findAllByProduct(productList.get(i)).get(i).getOrder());
            orderItem.setProduct(orderItemRepository.findAllByProduct(productList.get(i)).get(i).getProduct());
            orderItem.setQuantity(orderItemRepository.findAllByProduct(productList.get(i)).get(i).getQuantity());
            orderItem.setPrice(orderItemRepository.findAllByProduct(productList.get(i)).get(i).getPrice());

            list.add(orderItem);;
        }
        return list;
    }
}

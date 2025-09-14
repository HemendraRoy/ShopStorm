package com.shopstorm.ShopStorm.mappers;

import com.shopstorm.ShopStorm.dtos.OrderDTO;
import com.shopstorm.ShopStorm.dtos.OrderItemDTO;
import com.shopstorm.ShopStorm.entities.Order;
import com.shopstorm.ShopStorm.entities.OrderItem;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderDTO toDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getId());
        dto.setBuyerId(order.getBuyer().getId());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setStatus(order.getOrderStatus().name());
        dto.setCreatedAt(order.getDate()); // matches your entity field

        List<OrderItemDTO> items = order.getItems().stream().map(OrderMapper::toItemDTO).collect(Collectors.toList());
        dto.setItems(items);

        return dto;
    }

    public static OrderItemDTO toItemDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        return dto;
    }
}

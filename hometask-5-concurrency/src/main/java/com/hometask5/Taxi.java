package com.hometask5;

import java.util.List;

public interface Taxi {
    void run();

    void placeOrder(Order order);

    List<Order> getExecutedOrders();
}

package com.hometask5;

public interface Dispatcher {
    void run();

    void notifyAvailable(Taxi taxi);

    void placeOrder(Order order);
}

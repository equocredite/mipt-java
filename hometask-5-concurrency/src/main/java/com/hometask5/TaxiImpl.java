package com.hometask5;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TaxiImpl implements Taxi {
    private final Dispatcher dispatcher;
    private volatile Order order = null;
    private final List<Order> executedOrders = new ArrayList<>();

    TaxiImpl(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {
        while (true) {
            awaitOrder();
            executeOrder();
            dispatcher.notifyAvailable(this);
        }
    }

    @Override
    public void placeOrder(Order order) {
        synchronized (this) {
            this.order = order;
            notify();
        }
    }

    @Override
    public List<Order> getExecutedOrders() {
        synchronized (executedOrders) {
            return new ArrayList<>(executedOrders);
        }
    }

    private void executeOrder() {
        try {
            TimeUnit.MILLISECONDS.sleep(order.getDuration());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (executedOrders) {
            executedOrders.add(order);
        }
        order = null;
    }

    private void awaitOrder() {
        synchronized (this) {
            while (order == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

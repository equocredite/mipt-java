package com.hometask5;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;

public class DispatcherImpl implements Dispatcher {
    private final Queue<Taxi> freeTaxis;
    private final Queue<Order> orders = new ArrayDeque<>();

    DispatcherImpl(Collection<Taxi> taxis) {
        this.freeTaxis = new ArrayDeque<>(taxis);
    }

    @Override
    public void notifyAvailable(Taxi taxi) {
        addToQueue(freeTaxis, taxi);
    }

    @Override
    public void placeOrder(Order order) {
        addToQueue(orders, order);
    }

    @Override
    public void run() {
        while (true) {
            Taxi taxi = extractFreeTaxi();
            Order order = extractOrder();
            taxi.placeOrder(order);
        }
    }

    private static <E> E pollQueue(Queue<E> queue) {
        synchronized (queue) {
            while (queue.isEmpty()) {
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return queue.poll();
        }
    }

    private static <E> void addToQueue(Queue<E> queue, E elem) {
        synchronized (queue) {
            queue.add(elem);
            queue.notify();
        }
    }

    private Taxi extractFreeTaxi() {
        return pollQueue(freeTaxis);
    }

    private Order extractOrder() {
        return pollQueue(orders);
    }
}

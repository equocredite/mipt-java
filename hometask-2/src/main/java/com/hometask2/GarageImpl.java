package com.hometask2;

import java.util.*;

public class GarageImpl implements Garage {
    private final Map<Long, Car> cars = new HashMap<>();
    private final Map<Long, Owner> owners = new HashMap<>();
    private final Map<Owner, Set<Car>> carsByOwner = new HashMap<>();
    private final Map<String, Set<Car>> carsByBrand = new HashMap<>();
    private final NavigableMap<Integer, Set<Car>> carsByPower = new TreeMap<>(Collections.reverseOrder());
    private final NavigableMap<Integer, Set<Car>> carsByMaxVelocity = new TreeMap<>(Collections.reverseOrder());

    @Override
    public Collection<Owner> allCarsUniqueOwners() {
        return owners.values();
    }

    @Override
    public Collection<Car> topThreeCarsByMaxVelocity() {
        List<Car> cars = new ArrayList<>();
        for (var entry : carsByMaxVelocity.entrySet()) {
            for (Car car : entry.getValue()) {
                cars.add(car);
                if (cars.size() == 3) {
                    break;
                }
            }
            if (cars.size() == 3) {
                break;
            }
        }
        return cars;
    }

    @Override
    public Collection<Car> allCarsOfBrand(String brand) {
        return carsByBrand.get(brand);
    }

    @Override
    public Collection<Car> carsWithPowerMoreThan(int power) {
        List<Car> cars = new ArrayList<>();
        for (var entry : carsByPower.entrySet()) {
            if (entry.getKey() <= power) {
                break;
            }
            cars.addAll(entry.getValue());
        }
        return cars;
    }

    @Override
    public Collection<Car> allCarsOfOwner(Owner owner) {
        return carsByOwner.get(owner);
    }

    @Override
    public int meanOwnersAgeOfCarBrand(String brand) {
        var ownerIds = carsByBrand.get(brand).stream().mapToLong(Car::getOwnerId).distinct().toArray();
        int count = ownerIds.length;
        return Arrays.stream(ownerIds).mapToInt(id -> owners.get(id).getAge()).sum() / count;
    }

    @Override
    public int meanCarNumberForEachOwner() {
        return cars.size() / owners.size();
    }

    @Override
    public Car removeCar(long carId) {
        Car car = cars.get(carId);
        Owner owner = owners.get(car.getOwnerId());

        cars.remove(carId);
        owners.remove(owner.getOwnerId());

        carsByOwner.get(owner).remove(car);
        if (carsByOwner.get(owner).isEmpty()) {
            carsByOwner.remove(owner);
        }

        String brand = car.getBrand();
        carsByBrand.get(brand).remove(car);
        if (carsByBrand.get(brand).isEmpty()) {
            carsByBrand.remove(brand);
        }

        int power = car.getPower();
        carsByPower.get(power).remove(car);
        if (carsByPower.get(power).isEmpty()) {
            carsByPower.remove(power);
        }

        int maxVelocity = car.getMaxVelocity();
        carsByMaxVelocity.get(maxVelocity).remove(car);
        if (carsByMaxVelocity.get(maxVelocity).isEmpty()) {
            carsByMaxVelocity.remove(maxVelocity);
        }

        return car;
    }

    @Override
    public void addCar(Car car, Owner owner) {
        cars.put(car.getCarId(), car);
        owners.put(owner.getOwnerId(), owner);

        carsByOwner.putIfAbsent(owner, new HashSet<>());
        carsByOwner.get(owner).add(car);

        carsByBrand.putIfAbsent(car.getBrand(), new HashSet<>());
        carsByBrand.get(car.getBrand()).add(car);

        carsByPower.putIfAbsent(car.getPower(), new HashSet<>());
        carsByPower.get(car.getPower()).add(car);

        carsByMaxVelocity.putIfAbsent(car.getMaxVelocity(), new HashSet<>());
        carsByMaxVelocity.get(car.getMaxVelocity()).add(car);
    }
}

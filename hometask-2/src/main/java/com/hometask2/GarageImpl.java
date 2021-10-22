package com.hometask2;

import java.util.*;

public class GarageImpl implements Garage {
    private final Map<Long, Car> cars = new HashMap<>();
    private final Map<Long, Owner> owners = new HashMap<>();
    private final Map<Owner, Set<Car>> carsByOwner = new HashMap<>();
    private final Map<String, Set<Car>> carsByBrand = new HashMap<>();
    private final TreeSet<Car> carsByPower = new TreeSet<>(Comparator.comparing(Car::getPower).
            thenComparing(Car::getCarId));
    private final TreeSet<Car> carsByMaxVelocity = new TreeSet<>(Comparator.comparing(Car::getMaxVelocity)
            .thenComparing(Car::getCarId).reversed());

    @Override
    public Collection<Owner> allCarsUniqueOwners() {
        return owners.values();
    }

    @Override
    public Collection<Car> topThreeCarsByMaxVelocity() {
        List<Car> top = new ArrayList<>();
        for (Car car : carsByMaxVelocity) {
            top.add(car);
            if (top.size() == 3) {
                break;
            }
        }
        if (top.size() < 3) {
            throw new IllegalStateException("currently less than 3 cars in total");
        }
        return top;
    }

    @Override
    public Collection<Car> allCarsOfBrand(String brand) {
        return carsByBrand.get(brand);
    }

    @Override
    public Collection<Car> carsWithPowerMoreThan(int power) {
        return carsByPower.tailSet(new Car(Long.MAX_VALUE, "", "", 0, power, 0),
                false);
    }

    @Override
    public Collection<Car> allCarsOfOwner(Owner owner) {
        return carsByOwner.get(owner);
    }

    @Override
    public int meanOwnersAgeOfCarBrand(String brand) {
        var meanAgeOption = carsByBrand.get(brand).stream().
                mapToLong(Car::getOwnerId).
                map(id -> owners.get(id).getAge()).
                average();
        if (meanAgeOption.isEmpty()) {
            throw new IllegalArgumentException("no cars of this brand");
        }
        return (int) Math.round(meanAgeOption.getAsDouble());
    }

    @Override
    public int meanCarNumberForEachOwner() {
        return cars.size() / owners.size();
    }

    @Override
    public Car removeCar(long carId) {
        if (!cars.containsKey(carId)) {
            throw new IllegalArgumentException("no car with such id");
        }
        Car car = cars.get(carId);
        // exists because our operations are consistent
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

        carsByPower.remove(car);
        carsByMaxVelocity.remove(car);

        return car;
    }

    @Override
    public void addCar(Car car, Owner owner) {
        cars.put(car.getCarId(), car);
        owners.put(owner.getOwnerId(), owner);
        carsByOwner.computeIfAbsent(owner, k -> new HashSet<>()).add(car);
        carsByBrand.computeIfAbsent(car.getBrand(), k -> new HashSet<>()).add(car);
        carsByPower.add(car);
        carsByMaxVelocity.add(car);
    }
}

package com.hometask2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GarageTests {
    private static final Car[] CARS = new Car[] {
            new Car(0, "Volvo", "model0", 100, 100, 0),
            new Car(1, "Honda", "model1", 50, 200, 0),
            new Car(2, "Honda", "model2", 200, 300, 1),
            new Car(3, "BMW", "model3", 200, 250, 1)
    };

    private static final Owner[] OWNERS = new Owner[] {
            new Owner(0, "John", "Smith", 35),
            new Owner(1, "Dick", "Turpin", 33)
    };

    private Garage garage;

    @BeforeEach
    private void setUp() {
        garage = new GarageImpl();
        for (Car car : CARS) {
            garage.addCar(car, OWNERS[(int) car.getOwnerId()]);
        }
    }

    @Test
    void testAllCarsUniqueOwners() {
        var uniqueOwners = garage.allCarsUniqueOwners();
        assertEquals(2, uniqueOwners.size());
        assertTrue(uniqueOwners.contains(OWNERS[0]));
        assertTrue(uniqueOwners.contains(OWNERS[1]));
    }

    @Test
    void testTopThreeCarsByMaxVelocity() {
        var top = garage.topThreeCarsByMaxVelocity();
        assertEquals(3, top.size());
        assertTrue(top.contains(CARS[0]));
        assertTrue(top.contains(CARS[2]));
        assertTrue(top.contains(CARS[3]));
    }

    @Test
    void testAlLCarsOfBrand() {
        var hondas = garage.allCarsOfBrand("Honda");
        assertEquals(2, hondas.size());
        assertTrue(hondas.contains(CARS[1]));
        assertTrue(hondas.contains(CARS[2]));
    }

    @Test
    void testCarsWithPowerMoreThan() {
        var cars = garage.carsWithPowerMoreThan(200);
        assertEquals(2, cars.size());
        assertTrue(cars.contains(CARS[2]));
        assertTrue(cars.contains(CARS[3]));
    }

    @Test
    void testAllCarsOfOwner() {
        var carsOfJohn = garage.allCarsOfOwner(OWNERS[0]);
        assertEquals(2, carsOfJohn.size());
        assertTrue(carsOfJohn.contains(CARS[0]));
        assertTrue(carsOfJohn.contains(CARS[1]));
    }

    @Test
    void testMeanOwnersAgeOfCarBrand() {
        assertEquals(34, garage.meanOwnersAgeOfCarBrand("Honda"));
    }

    @Test
    void testMeanCarNumberForEachOwner() {
        assertEquals(2, garage.meanCarNumberForEachOwner());
    }

    @Test
    void testAddAndRemoveCarCorrectly() {
        Owner newOwner = new Owner(2, "Guy", "Fawkes", 24);
        Car newCar = new Car(4, "Toyota", "model4", 150, 250, 2);

        garage.addCar(newCar, newOwner);

        var uniqueOwners = garage.allCarsUniqueOwners();
        assertEquals(3, uniqueOwners.size());
        assertTrue(uniqueOwners.contains(OWNERS[0]));
        assertTrue(uniqueOwners.contains(OWNERS[1]));
        assertTrue(uniqueOwners.contains(newOwner));

        garage.removeCar(4);

        uniqueOwners = garage.allCarsUniqueOwners();
        assertEquals(2, uniqueOwners.size());
        assertTrue(uniqueOwners.contains(OWNERS[0]));
        assertTrue(uniqueOwners.contains(OWNERS[1]));
    }

    @Test
    void testRemoveNonExistentCar() {
        assertThrows(IllegalArgumentException.class, () -> garage.removeCar(4));
    }
}

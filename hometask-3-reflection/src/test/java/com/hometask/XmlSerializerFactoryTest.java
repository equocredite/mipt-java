package com.hometask;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Person {
    private final String name;
    private final int age;
    private final Person parent;
    private final Car car;

    public Person(String name, int age, Person parent, Car car) {
        this.name = name;
        this.age = age;
        this.parent = parent;
        this.car = car;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public Person getParent() {
        return parent;
    }

    public Car getCar() {
        return car;
    }
}

class Car {
    private final Long id;
    private final int power;
    private final String brand;

    public Car(Long id, int power, String brand) {
        this.id = id;
        this.power = power;
        this.brand = brand;
    }

    public Long getId() {
        return id;
    }

    public int getPower() {
        return power;
    }

    public String getBrand() {
        return brand;
    }
}

public class XmlSerializerFactoryTest {
    private final Serializer<Person> serializer = new XmlSerializerFactory<>(Person.class).createSerializer();

    @Test
    public void simpleTest() {
        Person john = new Person("John", 64, null, null);
        String expected = "<parent>null</parent>" +
                          "<car>null</car>" +
                          "<name>John</name>" +
                          "<age>64</age>";
        assertEquals(expected, serializer.serialize(john));
    }

    @Test
    public void nestedObjectTest() {
        Car car = new Car(42L, 450, "Volvo");
        Person john = new Person("John", 64, null, car);
        String expected = "<parent>null</parent>" +
                          "<car>" +
                              "<brand>Volvo</brand>" +
                              "<id>42</id>" +
                              "<power>450</power>" +
                          "</car>" +
                          "<name>John</name>" +
                          "<age>64</age>";
        assertEquals(expected, serializer.serialize(john));
    }

    @Test
    public void referencetoSameClassTest() {
        Car johnsCar = new Car(42L, 450, "Volvo");
        Person john = new Person("John", 64, null, johnsCar);

        Car mattsCar = new Car(322L, 300, "Honda");
        Person matt = new Person("Matt", 35, john, mattsCar);

        Person luke = new Person("Luke", 4, matt, null);

        String expected = "<parent>" +
                              "<parent>" +
                                  "<parent>null</parent>" +
                                  "<car>" +
                                      "<brand>Volvo</brand>" +
                                      "<id>42</id>" +
                                      "<power>450</power>" +
                                  "</car>" +
                                  "<name>John</name>" +
                                  "<age>64</age>" +
                              "</parent>" +
                              "<car>" +
                                  "<brand>Honda</brand>" +
                                  "<id>322</id>" +
                                  "<power>300</power>" +
                              "</car>" +
                              "<name>Matt</name>" +
                              "<age>35</age>" +
                          "</parent>" +
                          "<car>null</car>" +
                          "<name>Luke</name>" +
                          "<age>4</age>";
        assertEquals(expected, serializer.serialize(luke));
    }
}

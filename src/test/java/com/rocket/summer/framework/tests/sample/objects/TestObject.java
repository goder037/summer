package com.rocket.summer.framework.tests.sample.objects;

public class TestObject implements ITestObject, ITestInterface, Comparable<Object> {

    private String name;

    private int age;

    private TestObject spouse;

    public TestObject() {
    }

    public TestObject(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getAge() {
        return this.age;
    }

    @Override
    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public TestObject getSpouse() {
        return this.spouse;
    }

    @Override
    public void setSpouse(TestObject spouse) {
        this.spouse = spouse;
    }

    @Override
    public void absquatulate() {
    }

    @Override
    public int compareTo(Object o) {
        if (this.name != null && o instanceof TestObject) {
            return this.name.compareTo(((TestObject) o).getName());
        }
        else {
            return 1;
        }
    }
}

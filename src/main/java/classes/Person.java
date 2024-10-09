package classes;

import java.io.*;
import java.util.HashMap;
import java.util.List;

public class Person extends HasdSerializableImpl {
    private String name;
    private String lastName;
    private int age;
    private Integer height;
    private short shortVal;
    private byte byteVal;
    private long longVal;
    private double doubleVal;
    private float floatVal;
    private boolean booleanVal;
    private char charVal;
    private HasdSerializable childPerson;
    private List<Integer> intVals;
    private List<String> stringVals;
    private HashMap<Integer, String> mapVals;

    public Person(Builder builder) {
        this.name = builder.name;
        this.lastName = builder.lastName;
        this.age = builder.age;
        this.height = builder.height;
        this.shortVal = builder.shortVal;
        this.byteVal = builder.byteVal;
        this.longVal = builder.longVal;
        this.doubleVal = builder.doubleVal;
        this.floatVal = builder.floatVal;
        this.booleanVal = builder.booleanVal;
        this.charVal = builder.charVal;
        this.childPerson = builder.childPerson;
        this.intVals = builder.intVals;
        this.stringVals = builder.stringVals;
        this.mapVals = builder.mapVals;
    }


    @Override
    public String toString(){
        String report = String.format("person:\n" +
                "name: %s;\n" +
                "lastName: %s;\n" +
                "age: %d;\n" +
                "height: %d;\n",
                this.name,
                this.lastName,
                this.age,
                this.height);
        return report;
    }

    public static class Builder {
        private String name;
        private String lastName;
        private int age;
        private Integer height;
        private short shortVal;
        private byte byteVal;
        private long longVal;
        private double doubleVal;
        private float floatVal;
        private boolean booleanVal;
        private char charVal;
        private HasdSerializable childPerson;
        private List<Integer> intVals;
        private List<String> stringVals;
        private HashMap<Integer, String> mapVals;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder setAge(int age) {
            this.age = age;
            return this;
        }

        public Builder setHeight(Integer height) {
            this.height = height;
            return this;
        }

        public Builder setShortVal(short shortVal) {
            this.shortVal = shortVal;
            return this;
        }

        public Builder setByteVal(byte byteVal) {
            this.byteVal = byteVal;
            return this;
        }

        public Builder setLongVal(long longVal) {
            this.longVal = longVal;
            return this;
        }

        public Builder setDoubleVal(double doubleVal) {
            this.doubleVal = doubleVal;
            return this;
        }

        public Builder setFloatVal(float floatVal) {
            this.floatVal = floatVal;
            return this;
        }

        public Builder setBooleanVal(boolean booleanVal) {
            this.booleanVal = booleanVal;
            return this;
        }

        public Builder setCharVal(char charVal) {
            this.charVal = charVal;
            return this;
        }

        public Builder setChildPerson(HasdSerializable childPerson){
            this.childPerson = childPerson;
            return this;
        }

        public Builder setIntVals(List<Integer> intVals){
            this.intVals = intVals;
            return this;
        }

        public Builder setStringVals(List<String> stringVals){
            this.stringVals = stringVals;
            return this;
        }

        public Builder setMapVals(HashMap<Integer, String> mapVals){
            this.mapVals = mapVals;
            return this;
        }

        public Person build() {
            return new Person(this);
        }

        public Person deserialize(byte[] serializedPerson){
            // Здесь должен генерироваться код десериализации объекта из байтов
            return null;
        }

        public Person readFromFile(String path) throws IOException {
            FileInputStream fis = new FileInputStream(path);
            Person person = deserialize(fis.readAllBytes());
            fis.close();
            return person;
        }

    }
}

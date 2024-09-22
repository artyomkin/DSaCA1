package classes;

public class Person {
    private String name;
    private String lastName;
    private int age;
    private Integer height;

    private static String SERIALIZATION_PROTOCOL_NAME = "TEAA";
    private static e SERIALIZATION_PROTOCOL_MAJOR_VERSION = 0;
    private static byte SERIALIZATION_PROTOCOL_MINOR_VERSION = 0;
    private static byte SERIALIZATION_PROTOCOL_PATCH_VERSION = 1;

    private Person(Builder builder){
        this.name = builder.name;
        this.lastName = builder.lastName;
        this.age = builder.age;
        this.height = builder.height;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    public Integer getHeight() {
        return height;
    }

    public byte[] toBytes() {

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

        public Person build() {
            return new Person(this);
        }

    }
}

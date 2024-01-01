package cn.izualzhy;

import com.github.javafaker.Faker;

import java.util.concurrent.TimeUnit;

public class JustTest {
    public static void main(String[] args) {
        Faker faker = new Faker();

        String name = faker.name().fullName(); // Miss Samanta Schmidt
        String firstName = faker.name().firstName(); // Emory
        String lastName = faker.name().lastName(); // Barton

        String streetAddress = faker.address().streetAddress(); // 60018 Sawayn Brooks Suite 449

        System.out.println(name);
        System.out.println(faker.country().name());
//        System.out.println(faker.phoneNumber().cellPhone());
//        System.out.println(streetAddress);
        System.out.println(faker.date().birthday().toInstant().getEpochSecond());
        System.out.println(faker.job().title());
        System.out.println(faker.number().numberBetween(1, 123));
        System.out.println(faker.date().past(10, TimeUnit.DAYS).getTime());
        System.out.println(faker.date().birthday().getTime());
    }
}

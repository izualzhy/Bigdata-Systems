package cn.izualzhy;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class SerializeSample {
    void serialize() {
        User user = new User("zhangsan", 18);
        try {
            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(new java.io.FileOutputStream("user.ser"));
            oos.writeObject(user);
            oos.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    void deserialize() {
        User user = null;

        try (FileInputStream fileIn = new FileInputStream("user.ser");
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            user = (User) in.readObject();
        } catch (IOException i) {
            i.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            System.out.println("User class not found");
            c.printStackTrace();
            return;
        }

        System.out.println(Thread.currentThread().getContextClassLoader());
        System.out.println(sun.misc.VM.latestUserDefinedLoader());
        System.out.println("Deserialized User...");
        System.out.println("Name: " + user.getName());
        System.out.println("Age: " + user.getAge());
    }

    public static void main(String[] args) {
        SerializeSample sample = new SerializeSample();
        sample.serialize();
        sample.deserialize();
    }
}

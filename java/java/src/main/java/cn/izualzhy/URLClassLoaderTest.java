package cn.izualzhy;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class URLClassLoaderTest {
    void urlClassLoaderTest() throws MalformedURLException {
        ClassLoader originalClassLoader = this.getClass().getClassLoader();
        // not found
        findMySQLClass(originalClassLoader);

        ClassLoader mysqlClassLoader = new URLClassLoader(
                new URL[]{new URL("file:" + System.getProperty("user.home") + "/.m2/repository/mysql/mysql-connector-java/8.0.16/mysql-connector-java-8.0.16.jar")},
                originalClassLoader
        );
        Thread.currentThread().setContextClassLoader(mysqlClassLoader);
        // found
        findMySQLClass(mysqlClassLoader);

        Thread.currentThread().setContextClassLoader(originalClassLoader);
        // not found
        findMySQLClass(originalClassLoader);

        mysqlClassLoader = new URLClassLoader(
                new URL[]{new URL("http://127.0.0.1:8000/mysql-connector-java-8.0.16.jar")},
                originalClassLoader
        );
        Thread.currentThread().setContextClassLoader(mysqlClassLoader);
        // found
        findMySQLClass(mysqlClassLoader);

        Thread.currentThread().setContextClassLoader(originalClassLoader);
        mysqlClassLoader = new URLClassLoader(
                new URL[]{new URL("http://127.0.0.1:8000/")},
                originalClassLoader
        );
        Thread.currentThread().setContextClassLoader(mysqlClassLoader);
        // found
        findMySQLClass(mysqlClassLoader);
    }

    void findMySQLClass(ClassLoader classLoader) {
        String className = "com.mysql.cj.jdbc.Driver";
        try {
            Class<?> c = Class.forName(className, false, classLoader);
            System.out.printf("className:%s found, c:%s\n", className, c);
        } catch (ClassNotFoundException e) {
            System.out.printf("className:%s not found, e:%s\n", className, e);
        }
    }

    public static void main(String[] args) throws MalformedURLException {
        URLClassLoaderTest urlClassLoaderTest = new URLClassLoaderTest();
        urlClassLoaderTest.urlClassLoaderTest();
    }
}

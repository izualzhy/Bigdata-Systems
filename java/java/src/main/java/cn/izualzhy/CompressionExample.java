package cn.izualzhy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import java.nio.charset.StandardCharsets;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressionExample {
    public static void main(String[] args) throws IOException {
        // 原始数据
        String data = "rO0ABXNyADtvcmcuYXBhY2hlLmZsaW5rLnJ1bnRpbWUuc3RhdGUuUmV0cmlldmFibGVTdHJlYW1TdGF0ZUhhbmRsZQABHhjxVZcrAgABTAAYd3JhcHBlZFN0cmVhbVN0YXRlSGFuZGxldAAyTG9yZy9hcGFjaGUvZmxpbmsvcnVudGltZS9zdGF0ZS9TdHJlYW1TdGF0ZUhhbmRsZTt4cHNyADlvcmcuYXBhY2hlLmZsaW5rLnJ1bnRpbWUuc3RhdGUuZmlsZXN5c3RlbS5GaWxlU3RhdGVIYW5kbGUE3HXYYr0bswIAAkoACXN0YXRlU2l6ZUwACGZpbGVQYXRodAAfTG9yZy9hcGF";
        System.out.println("base64 data length: " + data.length());

        byte[] originalData = Base64.getDecoder().decode(data);
        System.out.println("data length: " + originalData.length);

        // 将原始数据进行压缩
        byte[] compressedData = compress(originalData);

        // 将压缩后的数据进行Base64编码
        String compressedBase64 = Base64.getEncoder().encodeToString(compressedData);
        System.out.println("Compressed and Base64 encoded data: " + compressedBase64);
        System.out.println("Compressed length: " + compressedBase64.length());

        // 将Base64编码的数据解码
        byte[] decodedCompressedData = Base64.getDecoder().decode(compressedBase64);

        // 将解码后的数据进行解压缩
        byte[] decompressedData = decompress(decodedCompressedData);

        // 输出解压缩后的数据
        String decompressedString = new String(decompressedData, StandardCharsets.UTF_8);
        System.out.println("Decompressed data: " + decompressedString);
        System.out.println("Decompressed length: " + decompressedString.length());

        // 将解压缩后的数据进行Base64编码
        String decompressedBase64 = Base64.getEncoder().encodeToString(decompressedData);
        System.out.println("Decompressed base64 data: " + decompressedBase64);
        System.out.println("Decompressed base64 data length: " + decompressedBase64.length());
    }

    // 使用Gzip进行压缩
    public static byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
        gzipOutputStream.write(data);
        gzipOutputStream.close();
        return outputStream.toByteArray();
    }

    // 使用Gzip进行解压缩
    public static byte[] decompress(byte[] data) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = gzipInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
        gzipInputStream.close();
        outputStream.close();
        return outputStream.toByteArray();
    }
}

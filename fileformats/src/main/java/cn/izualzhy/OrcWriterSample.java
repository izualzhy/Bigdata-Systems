package cn.izualzhy;


import com.github.javafaker.Faker;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.vector.*;
import org.apache.hadoop.hive.serde2.io.DateWritable;
import org.apache.orc.OrcFile;
import org.apache.orc.TypeDescription;
import org.apache.orc.Writer;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OrcWriterSample {
    public static void orcWriterSample() throws IOException {
        Path testFilePath = new Path("/tmp/people.orc");
        // 文件结构
        TypeDescription schema = TypeDescription.fromString(
                "struct<name:string,location:map<string,string>,birthday:date,last_login:timestamp>"
        );

        Configuration conf = new Configuration();
        FileSystem.getLocal(conf);

        Faker faker = new Faker();

        try (Writer writer =
                OrcFile.createWriter(testFilePath,
                        OrcFile.writerOptions(conf).setSchema(schema))) {
            // 创建 row batch，每一列通过单独的 ColumnVector 写入
            VectorizedRowBatch batch = schema.createRowBatch();
            BytesColumnVector name = (BytesColumnVector) batch.cols[0];
            MapColumnVector location = (MapColumnVector) batch.cols[1];
            LongColumnVector birthday = (LongColumnVector) batch.cols[2];
            TimestampColumnVector last_login = (TimestampColumnVector) batch.cols[3];

            BytesColumnVector mapKey = (BytesColumnVector) location.keys;
            BytesColumnVector mapValue = (BytesColumnVector) location.values;
            // Each map has 2 elements
            final int MAP_SIZE = 2;
            final int BATCH_SIZE = batch.getMaxSize();
            System.out.println("BATCH_SIZE : " + BATCH_SIZE);

            // Ensure the map is big enough
            mapKey.ensureSize(BATCH_SIZE * MAP_SIZE, false);
            mapValue.ensureSize(BATCH_SIZE * MAP_SIZE, false);

            // 写入 1500 行数据
            for (int i = 0; i < 1500; i++) {
                int row = batch.size++;

                name.setVal(row, faker.name().fullName().getBytes());
                birthday.vector[row] = DateWritable.dateToDays(new java.sql.Date(faker.date().birthday(1, 123).getTime()));
                last_login.time[row] = faker.date().past(10, TimeUnit.DAYS).getTime();

                location.offsets[row] = location.childCount;
                location.lengths[row] = MAP_SIZE;
                location.childCount += MAP_SIZE;

                mapKey.setVal((int) location.offsets[row], "country".getBytes());
                mapValue.setVal((int) location.offsets[row], faker.country().name().getBytes());

                mapKey.setVal((int) location.offsets[row] + 1, "address".getBytes());
                mapValue.setVal((int) location.offsets[row] + 1, faker.address().streetAddress().getBytes());

                if (row == BATCH_SIZE - 1) {
                    writer.addRowBatch(batch);
                    batch.reset();
                }
            }

            if (batch.size != 0) {
                writer.addRowBatch(batch);
                batch.reset();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        orcWriterSample();
    }
}

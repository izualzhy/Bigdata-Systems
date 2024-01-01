package cn.izualzhy;

import com.github.javafaker.Faker;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.simple.SimpleGroupFactory;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.example.GroupWriteSupport;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.PrimitiveType;
import org.apache.parquet.schema.Type;
import org.apache.parquet.schema.Types;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class ParquetWriterSample {
    static void parquetWriterSample() throws IOException {
        Types.MessageTypeBuilder schemaBuilder = Types.buildMessage();
        schemaBuilder.addField(new PrimitiveType(Type.Repetition.REQUIRED, PrimitiveType.PrimitiveTypeName.BINARY, "name"));
        schemaBuilder.addField(new PrimitiveType(Type.Repetition.REQUIRED, PrimitiveType.PrimitiveTypeName.INT64, "last_login"));

        MessageType schema = schemaBuilder.named("record");

        Configuration conf = new Configuration();
        FileSystem.getLocal(conf);

        GroupWriteSupport.setSchema(schema, conf);
        GroupWriteSupport writeSupport = new GroupWriteSupport();
        writeSupport.init(conf);


        Path testFilePath = new Path("/tmp/people.parquet");
        Faker faker = new Faker();
        try (ParquetWriter<Group> writer = new ParquetWriter<Group>(testFilePath,
                writeSupport,
                CompressionCodecName.SNAPPY,
                ParquetWriter.DEFAULT_BLOCK_SIZE, ParquetWriter.DEFAULT_PAGE_SIZE, ParquetWriter.DEFAULT_PAGE_SIZE,
                ParquetWriter.DEFAULT_IS_DICTIONARY_ENABLED,
                ParquetWriter.DEFAULT_IS_VALIDATING_ENABLED,
                ParquetWriter.DEFAULT_WRITER_VERSION,
                conf)) {
            for (int i = 0; i < 1500; i++) {
                Group group = new SimpleGroupFactory(schema).newGroup();
                group.add("name", Arrays.toString(faker.name().fullName().getBytes()));
                group.add("last_login", faker.date().past(10, TimeUnit.DAYS).getTime());

                writer.write(group);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        parquetWriterSample();
    }
}

package com.techeule.examples.avro.kafka.control;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

public class AvroToBytesSerializerDeserializer<T extends GenericRecord> {
    private final SpecificDatumWriter<T> datumWriter;
    private final SpecificDatumReader<T> datumReader;

    public AvroToBytesSerializerDeserializer(final Class<T> classType) {
        Objects.requireNonNull(classType, "classType must not be null.");
        datumWriter = new SpecificDatumWriter<>(classType);
        datumReader = new SpecificDatumReader<>(classType);
    }

    public byte[] serialize(final List<T> records) {
        if ((records == null) || records.isEmpty()) {
            throw new IllegalArgumentException("records must not be null or empty");
        }

        final var byteArrayOutputStream = new ByteArrayOutputStream();
        try (final var dataFileWriter = new DataFileWriter<>(datumWriter)) {
            tryToAppendRecords(byteArrayOutputStream, dataFileWriter, records);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private void tryToAppendRecords(final ByteArrayOutputStream byteArrayOutputStream,
                                    final DataFileWriter<T> dataFileWriter,
                                    final List<T> records) throws IOException {
        dataFileWriter.create(records.get(0).getSchema(), byteArrayOutputStream);

        for (final T avroRecord : records) {
            if (avroRecord != null) {
                dataFileWriter.append(avroRecord);
            }
        }
    }

    public List<T> deserialize(final byte[] data) {
        try (final DataFileReader<T> dataFileReader = new DataFileReader<>(new SeekableByteArrayInput(data), datumReader)) {
            return tryToReadRecords(dataFileReader);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<T> tryToReadRecords(final DataFileReader<T> dataFileReader) {
        final List<T> records = new LinkedList<>();

        while (dataFileReader.hasNext()) {
            records.add(dataFileReader.next());
        }
        return records;
    }
}
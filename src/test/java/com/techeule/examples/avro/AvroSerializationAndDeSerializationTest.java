package com.techeule.examples.avro;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Comparator;
import java.util.UUID;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.io.DatumReader;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.techeule.examples.avro.schemas.Order;

class AvroSerializationAndDeSerializationTest {
    private static final SpecificDatumWriter<Order> orderDatumWriter = new SpecificDatumWriter<>(Order.class);
    private static final DatumReader<Order> orderDatumReader = new SpecificDatumReader<>(Order.class);
    private Path orderStoreRootFolder;

    @BeforeEach
    void setUp() throws IOException {
        orderStoreRootFolder = Files.createTempDirectory(getClass().getName());
    }

    @AfterEach
    void tearDown() throws IOException {
        if (orderStoreRootFolder != null) {
            Files.walk(orderStoreRootFolder)
                 .sorted(Comparator.reverseOrder())
                 .map(Path::toFile)
                 .forEach(File::delete);
            Files.deleteIfExists(orderStoreRootFolder);
        }
    }

    @Test
    void serializeOrderTest() throws IOException {
        final var orderId = UUID.randomUUID().toString();
        final var customerId = UUID.randomUUID().toString();
        final long orderDateEpochSecondsUTC = Instant.now().getEpochSecond();
        final var order = new Order(orderId, customerId, orderDateEpochSecondsUTC);

        save(order);

        final var expectedOrderFile = orderStoreRootFolder.resolve(Order.class.getSimpleName() +
                                                                           '-' +
                                                                           customerId +
                                                                           '-' +
                                                                           orderId +
                                                                           "-order.avro");

        assertThat(expectedOrderFile).exists();

        final var avroBytes = Files.readAllBytes(expectedOrderFile);
        // Convert the avro-bytes representation to String using the UTF-8 encoding
        final var avroRecoredAsString = new String(avroBytes, StandardCharsets.UTF_8);

        // check if the file content contains the schema
        assertThat(avroRecoredAsString).contains(order.getSchema().toString());

        // I am curious and I want to see the string representation of a serialized avro-record.
        System.out.println(avroRecoredAsString);
    }

    private void save(final Order order) {
        final byte[] oderAvroAsByteArray = convertOrderToBytes(order);

        final var oderFile = orderStoreRootFolder
                .resolve(order.getClass().getSimpleName() +
                                 '-' +
                                 order.getCustomerId() +
                                 '-' +
                                 order.getOrderId() +
                                 "-order.avro")
                .toFile();

        // You can save the byte array anywhere you want.
        // for simplicity, the byte array is stored on disk in a file.
        try {
            Files.write(oderFile.toPath(), oderAvroAsByteArray, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] convertOrderToBytes(final Order order) {
        final var byteArrayOutputStream = new ByteArrayOutputStream();

        try (final var dataFileWriter = new DataFileWriter<>(orderDatumWriter)) {
            dataFileWriter.create(order.getSchema(), byteArrayOutputStream);
            dataFileWriter.append(order);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Test
    void deSerializeOrderTest() {
        final var orderId = UUID.randomUUID().toString();
        final var customerId = UUID.randomUUID().toString();
        final long orderDateEpochSecondsUTC = Instant.now().getEpochSecond();
        final var order = new Order(orderId, customerId, orderDateEpochSecondsUTC);

        save(order);
        final var deSerializedOrder = fetch(Order.class.getSimpleName() +
                                                    '-' +
                                                    customerId +
                                                    '-' +
                                                    orderId +
                                                    "-order.avro");

        // the variable order and the variable deSerializedOrder do not point to the same object in memory
        assertThat(order).isNotSameAs(deSerializedOrder)
                         // the order-object has equal values like the deSerializedOrder-object.
                         .isEqualTo(deSerializedOrder);

        // we can check if order-id, customer-id and order-date are equal
        assertThat(deSerializedOrder.getOrderId()).isEqualTo(orderId);
        assertThat(deSerializedOrder.getCustomerId()).isEqualTo(customerId);
        assertThat(deSerializedOrder.getOrderDateEpochSecondsUTC()).isEqualTo(orderDateEpochSecondsUTC);
    }

    private Order fetch(final String fileId) {
        final byte[] bytes = readBytesFromAvroFile(fileId);
        return convertBytesToOrder(bytes);
    }

    private byte[] readBytesFromAvroFile(final String fileId) {
        final var avroOrderFile = orderStoreRootFolder.resolve(fileId);
        final byte[] bytes;
        try {
            bytes = Files.readAllBytes(avroOrderFile);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return bytes;
    }

    private Order convertBytesToOrder(final byte[] bytes) {
        try (final DataFileReader<Order> dataFileReader = new DataFileReader<>(new SeekableByteArrayInput(bytes), orderDatumReader)) {
            if (dataFileReader.hasNext()) {
                return dataFileReader.next();
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        throw new IllegalStateException("Can not find Order");
    }
}

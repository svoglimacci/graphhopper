package com.graphhopper.storage;

import com.github.javafaker.Faker;
import com.graphhopper.routing.ch.PrepareEncoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import com.graphhopper.storage.CHStorage.LowWeightShortcut;
import java.util.function.Consumer;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CHStorageTest {

    @Test
    void setAndGetLevels() {
        RAMDirectory dir = new RAMDirectory();
        CHStorage store = new CHStorage(dir, "ch1", -1, false);
        store.create(30, 5);
        assertEquals(0, store.getLevel(store.toNodePointer(10)));
        store.setLevel(store.toNodePointer(10), 100);
        assertEquals(100, store.getLevel(store.toNodePointer(10)));
        store.setLevel(store.toNodePointer(29), 300);
        assertEquals(300, store.getLevel(store.toNodePointer(29)));
    }

    @Test
    void createAndLoad(@TempDir Path path) {
        {
            GHDirectory dir = new GHDirectory(path.toAbsolutePath().toString(),
                    DAType.RAM_INT_STORE);
            CHStorage chStorage = new CHStorage(dir, "car", -1, false);
            // we have to call create, because we want to create a new storage not load an existing one
            chStorage.create(5, 3);
            assertEquals(0,
                    chStorage.shortcutNodeBased(0, 1, PrepareEncoder.getScFwdDir(), 10, 3, 5));
            assertEquals(1,
                    chStorage.shortcutNodeBased(1, 2, PrepareEncoder.getScFwdDir(), 11, 4, 6));
            assertEquals(2,
                    chStorage.shortcutNodeBased(2, 3, PrepareEncoder.getScFwdDir(), 12, 5, 7));
            // exceeding the number of expected shortcuts is ok, the container will just grow
            assertEquals(3,
                    chStorage.shortcutNodeBased(3, 4, PrepareEncoder.getScFwdDir(), 13, 6, 8));
            assertEquals(5, chStorage.getNodes());
            assertEquals(4, chStorage.getShortcuts());
            chStorage.flush();
            chStorage.close();
        }
        {
            GHDirectory dir = new GHDirectory(path.toAbsolutePath().toString(),
                    DAType.RAM_INT_STORE);
            CHStorage chStorage = new CHStorage(dir, "car", -1, false);
            // this time we load from disk
            chStorage.loadExisting();
            assertEquals(4, chStorage.getShortcuts());
            assertEquals(5, chStorage.getNodes());
            long ptr = chStorage.toShortcutPointer(0);
            assertEquals(0, chStorage.getNodeA(ptr));
            assertEquals(1, chStorage.getNodeB(ptr));
            assertEquals(10, chStorage.getWeight(ptr));
            assertEquals(3, chStorage.getSkippedEdge1(ptr));
            assertEquals(5, chStorage.getSkippedEdge2(ptr));
        }
    }


    @Test
    public void testBigWeight() {
        CHStorage g = new CHStorage(new RAMDirectory(), "abc", 1024, false);
        g.shortcutNodeBased(0, 0, 0, 10, 0, 1);

        g.setWeight(0, Integer.MAX_VALUE / 1000d + 1000);
        assertEquals(Integer.MAX_VALUE / 1000d + 1000, g.getWeight(0));

        g.setWeight(0, ((long) Integer.MAX_VALUE << 1) / 1000d - 0.001);
        assertEquals(((long) Integer.MAX_VALUE << 1) / 1000d - 0.001, g.getWeight(0), 0.001);

        g.setWeight(0, ((long) Integer.MAX_VALUE << 1) / 1000d);
        assertTrue(Double.isInfinite(g.getWeight(0)));
        g.setWeight(0, ((long) Integer.MAX_VALUE << 1) / 1000d + 1);
        assertTrue(Double.isInfinite(g.getWeight(0)));
        g.setWeight(0, ((long) Integer.MAX_VALUE << 1) / 1000d + 100);
        assertTrue(Double.isInfinite(g.getWeight(0)));
    }

    @Test
    public void testLargeNodeA() {
        int nodeA = Integer.MAX_VALUE;
        RAMIntDataAccess access = new RAMIntDataAccess("", "", false, -1);
        access.create(1000);
        access.setInt(0, nodeA << 1 | 1 & PrepareEncoder.getScFwdDir());
        assertTrue(access.getInt(0) < 0);
        assertEquals(Integer.MAX_VALUE, access.getInt(0) >>> 1);
    }

    // IFT3913 Test 1
    @Test
    public void testIsClosed() {
        RAMDirectory dir = new RAMDirectory();
        CHStorage chStorage = new CHStorage(dir, "test_close", -1, false);
        chStorage.create(5, 3);
        assertFalse(chStorage.isClosed(), "CHStorage should not be closed initially");

        chStorage.shortcutNodeBased(0, 1, PrepareEncoder.getScFwdDir(), 10.0, 2, 3);
        assertFalse(chStorage.isClosed(), "CHStorage should remain open after adding shortcuts");

        chStorage.close();
        assertTrue(chStorage.isClosed(), "CHStorage should be closed after calling close()");
    }

    // IFT3913 Test 2
    @Test
    void testCreateThrowsExceptions() {
        RAMDirectory dir = new RAMDirectory();
        CHStorage store = new CHStorage(dir, "", -1, false);

        try {
            store.create(-1, 5);
        } catch (IllegalStateException exception) {
            assertEquals("CHStorage must be created with a positive number of nodes", exception.getMessage());
        }

        store.create(10, 5);
        try {
            store.create(10, 5);
        } catch (IllegalStateException exception) {
            assertEquals("CHStorage can only be created once", exception.getMessage());
        }
    }
    // IFT3913 Test 4
    @Test
    void testShortcutAndLowWeight() {
        CHStorage storage = new CHStorage(new RAMDirectory(), "test", -1, false);
        storage.create(10, 5);
        final boolean[] consumerCalled = {false};
        storage.setLowShortcutWeightConsumer(new Consumer<LowWeightShortcut>() {
            public void accept(CHStorage.LowWeightShortcut shortcut) {
                consumerCalled[0] = true;
            }
        });

        storage.shortcutNodeBased(0, 1, 0, 0.0001, 0, 0);

        assertTrue(consumerCalled[0]);
    }

    // IFT3913 Test JavaFaker
    @Test
    void testToDetailsString() {
        Faker faker = new Faker();
        RAMDirectory dir = new RAMDirectory();
        String storageName = faker.name().firstName().toLowerCase();
        CHStorage storage = new CHStorage(dir, storageName, -1, false);

        // Generate random counts within reasonable bounds
        int nodeCount = faker.number().numberBetween(10, 1000);
        int shortcutCount = faker.number().numberBetween(5, 500);

        storage.create(nodeCount, shortcutCount);

        // Add some random shortcuts to populate the storage
        for (int i = 0; i < Math.min(shortcutCount, 10); i++) {
            int nodeA = faker.number().numberBetween(0, nodeCount - 1);
            int nodeB = faker.number().numberBetween(0, nodeCount - 1);
            double weight = faker.number().randomDouble(3, 1, 100);
            int skip1 = faker.number().numberBetween(0, 20);
            int skip2 = faker.number().numberBetween(0, 20);

            storage.shortcutNodeBased(nodeA, nodeB, PrepareEncoder.getScFwdDir(),
                    weight, skip1, skip2);
        }

        String details = storage.toDetailsString();

        // Verify the string contains expected components
        assertTrue(details.contains("shortcuts:"));
        assertTrue(details.contains("nodesCH:"));
        assertTrue(details.contains("MB"));

        // Verify it contains the actual counts
        assertTrue(details.contains(String.valueOf(storage.getShortcuts())));
        assertTrue(details.contains(String.valueOf(nodeCount)));

        // Verify basic format structure
        String[] parts = details.split(",");
        assertEquals(2, parts.length, "Details string should have two main parts");
        assertTrue(parts[0].trim().startsWith("shortcuts:"));
        assertTrue(parts[1].trim().startsWith("nodesCH:"));
    }



}

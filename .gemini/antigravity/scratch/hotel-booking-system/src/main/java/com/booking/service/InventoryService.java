package com.booking.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * InventoryService maintains room availability and tracks all allocated room
 * IDs.
 *
 * <p>
 * Key data structures:
 * <ul>
 * <li>{@code availableRooms} – HashMap&lt;roomType, count&gt; : how many rooms
 * are still free
 * <li>{@code allocatedRooms} – HashMap&lt;roomType, Set&lt;roomId&gt;&gt; :
 * which IDs have been
 * assigned per type (Set enforces uniqueness – no duplicate IDs ever stored)
 * </ul>
 *
 * <p>
 * All public methods are {@code synchronized} so the service is safe to call
 * from multiple threads (e.g. multiple BookingService workers).
 */
public class InventoryService {

    // ── room type → available count ────────────────────────────────────────────
    private final Map<String, Integer> availableRooms = new HashMap<>();

    // ── room type → Set of assigned room IDs (Set = uniqueness guarantee) ──────
    private final Map<String, Set<String>> allocatedRooms = new HashMap<>();

    // Monotonically increasing counter used as part of the room ID
    private final AtomicInteger idCounter = new AtomicInteger(1000);

    // ── initialisation ─────────────────────────────────────────────────────────

    /**
     * Seed the inventory with a room type and its initial capacity.
     *
     * @param roomType room category (e.g. "STANDARD", "DELUXE", "SUITE")
     * @param count    number of rooms available for that type
     */
    public synchronized void addRoomType(String roomType, int count) {
        String key = roomType.toUpperCase();
        availableRooms.put(key, count);
        allocatedRooms.putIfAbsent(key, new HashSet<>());
        System.out.printf("[Inventory] Initialised  %-8s  → %d rooms available%n", key, count);
    }

    // ── availability check ─────────────────────────────────────────────────────

    /**
     * Returns {@code true} if at least one room of the given type is available.
     */
    public synchronized boolean isAvailable(String roomType) {
        return availableRooms.getOrDefault(roomType.toUpperCase(), 0) > 0;
    }

    /**
     * Remaining available count for a room type (used for reporting / assertions).
     */
    public synchronized int getAvailableCount(String roomType) {
        return availableRooms.getOrDefault(roomType.toUpperCase(), 0);
    }

    // ── allocation ─────────────────────────────────────────────────────────────

    /**
     * Generates a unique room ID, records it in the allocated-rooms set for that
     * type, and decrements the available count atomically.
     *
     * <p>
     * Uniqueness is enforced by the {@link Set} contract: if (by any bug) the
     * same ID were generated twice, {@code Set.add()} would return {@code false}
     * and this method would throw rather than create a double-booking.
     *
     * @param roomType the category to allocate from
     * @return the newly assigned, globally unique room ID
     * @throws IllegalStateException if no rooms are available or (defensively)
     *                               the generated ID already exists in the set
     */
    public synchronized String allocateRoom(String roomType) {
        String key = roomType.toUpperCase();

        // ① Guard: check availability before touching anything
        if (!isAvailable(key)) {
            throw new IllegalStateException(
                    "No rooms available for type: " + key);
        }

        // ② Generate a candidate ID unique by construction
        String candidateId = key + "-" + idCounter.getAndIncrement();

        // ③ Enforce uniqueness via Set.add() – this is the double-booking firewall
        Set<String> assignedSet = allocatedRooms.get(key);
        boolean added = assignedSet.add(candidateId);
        if (!added) {
            // Defensive: should never happen with our monotonic counter
            throw new IllegalStateException(
                    "Room ID collision detected – double-booking prevented: " + candidateId);
        }

        // ④ Decrement inventory immediately (atomic with step ③ because method is
        // synchronized)
        availableRooms.put(key, availableRooms.get(key) - 1);

        System.out.printf("[Inventory] Allocated    %-12s  | remaining %-8s: %d%n",
                candidateId, key, availableRooms.get(key));

        return candidateId;
    }

    // ── reporting ──────────────────────────────────────────────────────────────

    /**
     * Returns an unmodifiable view of all room IDs allocated for a given type.
     */
    public synchronized Set<String> getAllocatedRooms(String roomType) {
        return Collections.unmodifiableSet(
                allocatedRooms.getOrDefault(roomType.toUpperCase(), Collections.emptySet()));
    }

    /**
     * Prints a full inventory summary to stdout.
     */
    public synchronized void printSummary() {
        System.out.println("\n══════════════════════════════════════════════");
        System.out.println("              INVENTORY SUMMARY               ");
        System.out.println("══════════════════════════════════════════════");
        for (String type : availableRooms.keySet()) {
            int remaining = availableRooms.get(type);
            int allocated = allocatedRooms.getOrDefault(type, Collections.emptySet()).size();
            System.out.printf("  %-10s  available: %2d  |  allocated: %2d  |  IDs: %s%n",
                    type, remaining, allocated,
                    allocatedRooms.getOrDefault(type, Collections.emptySet()));
        }
        System.out.println("══════════════════════════════════════════════\n");
    }
}

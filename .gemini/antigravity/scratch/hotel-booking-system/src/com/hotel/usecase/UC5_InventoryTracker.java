package com.hotel.usecase;

import java.util.HashMap;
import java.util.Map;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * UC5: Inventory Tracker
 * Data Structure: HashMap<String, Integer>
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * WHY HashMap?
 * ────────────
 * Real-time room availability must be checked for EVERY booking request.
 * If the system had 50 room types, a linear scan (O(n)) per request
 * would be unacceptably slow.
 *
 * HashMap<RoomType, count> provides:
 * - O(1) average lookup: map.get("SUITE") → instant
 * - O(1) average update: map.put("SUITE", n-1) → instant
 * - O(n) full iteration: for reporting (acceptable)
 *
 * This is the lookup-first foundation that UC6 builds upon by adding
 * a Set to guarantee uniqueness of room IDs (preventing double-booking).
 *
 * OPERATIONS:
 * put(key, value) O(1) avg
 * get(key) O(1) avg
 * getOrDefault(k,def) O(1) avg
 * containsKey(key) O(1) avg
 * entrySet() iteration O(n)
 */
public class UC5_InventoryTracker {

    // room type → current available count
    private final Map<String, Integer> inventory = new HashMap<>();

    /** Seed the inventory with a room type and initial count. */
    public void addRoomType(String type, int count) {
        inventory.put(type.toUpperCase(), count); // O(1) avg
        System.out.printf("  [Inventory] Stocked  : %-12s → %d rooms%n", type.toUpperCase(), count);
    }

    /** Check if a room type is available (O(1)). */
    public boolean isAvailable(String type) {
        return inventory.getOrDefault(type.toUpperCase(), 0) > 0;
    }

    /** Decrement count when a room is booked (O(1)). */
    public void decrementCount(String type) {
        String key = type.toUpperCase();
        int current = inventory.getOrDefault(key, 0);
        if (current <= 0)
            throw new IllegalStateException("No rooms available: " + key);
        inventory.put(key, current - 1);
        System.out.printf("  [Inventory] Booked   : %-12s | Remaining: %d%n", key, current - 1);
    }

    /** Increment count on cancellation (O(1)). */
    public void incrementCount(String type) {
        String key = type.toUpperCase();
        inventory.put(key, inventory.getOrDefault(key, 0) + 1);
        System.out.printf("  [Inventory] Restored : %-12s | Available: %d%n",
                key, inventory.get(key));
    }

    /** Get current available count for a type. */
    public int getCount(String type) {
        return inventory.getOrDefault(type.toUpperCase(), 0);
    }

    /** Print full inventory table (O(n) iteration). */
    public void printInventory() {
        System.out.println("\n  [Inventory]  Current Availability:");
        System.out.println("    ┌────────────────┬───────────┐");
        System.out.println("    │  Room Type     │ Available │");
        System.out.println("    ├────────────────┼───────────┤");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.printf("    │ %-14s │     %-5d │%n", entry.getKey(), entry.getValue());
        }
        System.out.println("    └────────────────┴───────────┘");
    }

    // ── Demo ──────────────────────────────────────────────────────────────
    public static void demo() {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│  UC5 · Inventory Tracker  (HashMap<String,Integer>)     │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        System.out.println("  Data Structure : HashMap<String, Integer>");
        System.out.println("  Concept        : O(1) key-value lookup & update\n");

        UC5_InventoryTracker tracker = new UC5_InventoryTracker();

        tracker.addRoomType("STANDARD", 5);
        tracker.addRoomType("DELUXE", 3);
        tracker.addRoomType("SUITE", 2);
        tracker.addRoomType("PENTHOUSE", 1);

        tracker.printInventory();

        System.out.println("\n  Booking 3 STANDARD and 1 SUITE...");
        tracker.decrementCount("STANDARD");
        tracker.decrementCount("STANDARD");
        tracker.decrementCount("STANDARD");
        tracker.decrementCount("SUITE");

        System.out.println("\n  Cancellation: 1 STANDARD room returned...");
        tracker.incrementCount("STANDARD");

        tracker.printInventory();

        System.out.println("\n  Checking availability...");
        String[] types = { "STANDARD", "DELUXE", "SUITE", "PENTHOUSE" };
        for (String t : types) {
            System.out.printf("    %-12s → %s%n", t, tracker.isAvailable(t) ? "AVAILABLE" : "SOLD OUT");
        }

        // ── Assertions ────────────────────────────────────────────────────
        System.out.println("\n  [Assertions]");
        assert tracker.getCount("STANDARD") == 3 : "FAIL: STANDARD should be 3";
        assert tracker.getCount("DELUXE") == 3 : "FAIL: DELUXE should be 3";
        assert tracker.getCount("SUITE") == 1 : "FAIL: SUITE should be 1";
        assert tracker.getCount("PENTHOUSE") == 1 : "FAIL: PENTHOUSE should be 1";
        assert tracker.isAvailable("SUITE") : "FAIL: SUITE should be available";
        System.out.println("  ✓ PASS – Inventory counts consistent after bookings and cancellation.");
    }
}

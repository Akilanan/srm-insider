package com.hotel.usecase;

import com.hotel.model.BookingRequest;
import com.hotel.model.Guest;
import com.hotel.model.Reservation;
import com.hotel.model.Room;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * UC6: Room Allocation Service
 * Data Structures: HashMap<String, Integer> + HashMap<String, Set<String>>
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * WHY this combination?
 * ──────────────────────
 * UC5 gave us O(1) inventory count checks. But counts alone can't prevent
 * a race-style logical error: what if two threads (or two code paths) both
 * check "SUITE available?" simultaneously, both see count=1, and both
 * proceed to allocate — assigning the same physical room to two guests.
 *
 * The fix: a HashMap<RoomType, Set<RoomId>> tracks WHICH specific IDs
 * are already allocated.
 *
 * Set<String> guarantees uniqueness by contract:
 * - Set.add(id) returns FALSE if the ID already exists
 * - This is the "double-booking firewall"
 *
 * The synchronized keyword (on allocateRoom) ensures the three-step
 * logical unit (check → insert Set → decrement count) is ATOMIC,
 * preventing interleaving in concurrent scenarios.
 *
 * ATOMIC UNIT (all-or-nothing per allocation):
 * Step 1: Verify count > 0
 * Step 2: Set.add(candidateId) — rejects duplicates
 * Step 3: Decrement count
 * → Inventory count and ID registry are always in sync.
 */
public class UC6_RoomAllocation {

    // ── InventoryService ─────────────────────────────────────────────────
    static class InventoryService {

        // room type → remaining count (UC5 HashMap pattern)
        private final Map<String, Integer> availableRooms = new HashMap<>();

        // room type → Set of assigned IDs (uniqueness enforcer)
        private final Map<String, Set<String>> allocatedRooms = new HashMap<>();

        private int idCounter = 1000;

        /** Seed inventory. */
        void init(String type, int count) {
            String key = type.toUpperCase();
            availableRooms.put(key, count);
            allocatedRooms.put(key, new HashSet<>());
            System.out.printf("  [Inventory] Init     : %-10s → %d rooms%n", key, count);
        }

        boolean isAvailable(String type) {
            return availableRooms.getOrDefault(type.toUpperCase(), 0) > 0;
        }

        int getCount(String type) {
            return availableRooms.getOrDefault(type.toUpperCase(), 0);
        }

        Set<String> getAllocatedIds(String type) {
            return allocatedRooms.getOrDefault(type.toUpperCase(), new HashSet<>());
        }

        /**
         * Atomic allocation:
         * 1. Guard check (availability)
         * 2. Set.add() (uniqueness — returns false on collision)
         * 3. Decrement (inventory sync)
         */
        synchronized String allocateRoom(String type) {
            String key = type.toUpperCase();

            // Step 1: guard
            if (!isAvailable(key))
                throw new IllegalStateException("No rooms available: " + key);

            // Step 2: generate ID and enforce uniqueness via Set
            String roomId = key + "-" + (idCounter++);
            boolean inserted = allocatedRooms.get(key).add(roomId);
            if (!inserted)
                throw new IllegalStateException("Room ID collision (double-booking prevented): " + roomId);

            // Step 3: decrement (atomic with step 2)
            availableRooms.put(key, availableRooms.get(key) - 1);

            System.out.printf("  [Inventory] Allocated: %-14s | %s remaining: %d%n",
                    roomId, key, availableRooms.get(key));
            return roomId;
        }

        void printSummary() {
            System.out.println("\n  [Inventory] Allocation Summary:");
            System.out.println("    ┌────────────┬───────────┬───────────┬──────────────────────────────────────┐");
            System.out.println("    │ Room Type  │ Available │ Allocated │ Assigned IDs                         │");
            System.out.println("    ├────────────┼───────────┼───────────┼──────────────────────────────────────┤");
            for (String type : availableRooms.keySet()) {
                int av = availableRooms.get(type);
                Set<String> ids = allocatedRooms.getOrDefault(type, new HashSet<>());
                System.out.printf("    │ %-10s │     %-5d │     %-5d │ %-36s │%n",
                        type, av, ids.size(), ids);
            }
            System.out.println("    └────────────┴───────────┴───────────┴──────────────────────────────────────┘");
        }
    }

    // ── BookingService ───────────────────────────────────────────────────
    static class BookingService {
        private final Queue<BookingRequest> requestQueue = new ArrayDeque<>();
        private final List<Reservation> confirmedReservations = new ArrayList<>();
        private final InventoryService inventory;

        BookingService(InventoryService inv) {
            this.inventory = inv;
        }

        void enqueue(BookingRequest req) {
            requestQueue.offer(req);
            System.out.println("  [Booking]   Queued   : " + req.getGuest().getName()
                    + " → " + req.getRoomType());
        }

        void processAll() {
            System.out.println("\n  [Booking]  ─── Processing queue ───");
            while (!requestQueue.isEmpty()) {
                BookingRequest req = requestQueue.poll();
                String type = req.getRoomType();

                if (!inventory.isAvailable(type)) {
                    System.out.printf("  [Booking]   ✗ DECLINED : %-10s (no %s left)%n",
                            req.getGuest().getName(), type);
                    continue;
                }

                try {
                    String roomId = inventory.allocateRoom(type);
                    String confId = "CONF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                    Room room = new Room(roomId, type,
                            type.equals("SUITE") ? 250.0 : type.equals("DELUXE") ? 140.0 : 80.0);
                    Reservation res = new Reservation(confId, req.getGuest(), room,
                            req.getCheckInDate(), req.getNights());
                    confirmedReservations.add(res);
                    System.out.printf("  [Booking]   ✓ CONFIRMED: %-10s → %s (%s)%n",
                            req.getGuest().getName(), roomId, confId);
                } catch (IllegalStateException e) {
                    System.out.println("  [Booking]   ✗ BLOCKED  : " + e.getMessage());
                }
            }
        }

        List<Reservation> getConfirmed() {
            return confirmedReservations;
        }
    }

    // ── Demo ──────────────────────────────────────────────────────────────
    public static void demo() {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│  UC6 · Room Allocation  (HashMap + Set)                 │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        System.out.println("  Data Structures: HashMap<String,Integer> + HashMap<String,Set<String>>");
        System.out.println("  Concept        : Atomic allocation, double-booking prevention\n");

        InventoryService inv = new InventoryService();
        inv.init("STANDARD", 3);
        inv.init("DELUXE", 2);
        inv.init("SUITE", 1);

        BookingService svc = new BookingService(inv);

        LocalDate d = LocalDate.of(2024, 8, 1);
        Guest[] guests = {
                new Guest("G1", "Alice", "a@h", 1), new Guest("G2", "Bob", "b@h", 3),
                new Guest("G3", "Carol", "c@h", 5), new Guest("G4", "David", "d@h", 2),
                new Guest("G5", "Eve", "e@h", 4), new Guest("G6", "Frank", "f@h", 1),
                new Guest("G7", "Grace", "g@h", 1), new Guest("G8", "Henry", "h@h", 2),
                new Guest("G9", "Irene", "i@h", 3)
        };
        String[] types = { "STANDARD", "DELUXE", "SUITE", "STANDARD", "DELUXE",
                "STANDARD", "SUITE", "STANDARD", "DELUXE" };

        System.out.println("\n  Queueing 9 requests (6 fit, 3 over-capacity)...");
        for (int i = 0; i < guests.length; i++) {
            svc.enqueue(new BookingRequest("R" + (i + 1), guests[i], types[i], d, 2));
        }

        svc.processAll();
        inv.printSummary();

        // Uniqueness check
        System.out.println("\n  [Assertions]");
        Set<String> seen = new HashSet<>();
        for (Reservation r : svc.getConfirmed()) {
            boolean unique = seen.add(r.getRoom().getRoomId());
            assert unique : "FAIL: Duplicate room ID detected: " + r.getRoom().getRoomId();
        }
        for (String t : new String[] { "STANDARD", "DELUXE", "SUITE" }) {
            assert inv.getCount(t) >= 0 : "FAIL: Negative inventory for " + t;
        }
        assert svc.getConfirmed().size() == 6 : "FAIL: Expected 6 confirmed bookings";
        System.out.println("  ✓ PASS – All room IDs unique. No double-bookings. Inventory consistent.");
    }
}

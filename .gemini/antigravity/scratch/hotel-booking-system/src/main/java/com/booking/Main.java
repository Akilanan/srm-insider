package com.booking;

import com.booking.model.BookingRequest;
import com.booking.service.BookingService;
import com.booking.service.InventoryService;

/**
 * Entry point – demonstrates the full booking flow end-to-end:
 * 1. Seed inventory
 * 2. Queue booking requests (including over-capacity and duplicate-type
 * requests)
 * 3. Process queue
 * 4. Print confirmed reservations and final inventory state
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║      HOTEL BOOKING SYSTEM – DEMO RUN        ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        // ── 1. Set up inventory ────────────────────────────────────────────────
        InventoryService inventory = new InventoryService();
        inventory.addRoomType("STANDARD", 3);
        inventory.addRoomType("DELUXE", 2);
        inventory.addRoomType("SUITE", 1);

        // ── 2. Create booking service ──────────────────────────────────────────
        BookingService bookingService = new BookingService(inventory);

        // ── 3. Enqueue requests ────────────────────────────────────────────────
        // Normal requests (should all succeed)
        bookingService.enqueue(new BookingRequest("Alice", "STANDARD", 3));
        bookingService.enqueue(new BookingRequest("Bob", "DELUXE", 2));
        bookingService.enqueue(new BookingRequest("Carol", "SUITE", 5));
        bookingService.enqueue(new BookingRequest("David", "STANDARD", 1));
        bookingService.enqueue(new BookingRequest("Eva", "DELUXE", 4));
        bookingService.enqueue(new BookingRequest("Frank", "STANDARD", 2));

        // Over-capacity requests (should be declined)
        bookingService.enqueue(new BookingRequest("Grace", "SUITE", 3)); // 0 SUITE left
        bookingService.enqueue(new BookingRequest("Henry", "STANDARD", 1)); // 0 STANDARD left
        bookingService.enqueue(new BookingRequest("Irene", "DELUXE", 6)); // 0 DELUXE left

        // Unknown room type
        bookingService.enqueue(new BookingRequest("Jack", "PENTHOUSE", 2)); // type not seeded

        // ── 4. Process the queue (FIFO) ────────────────────────────────────────
        bookingService.processAll();

        // ── 5. Print reports ───────────────────────────────────────────────────
        bookingService.printConfirmedReservations();
        inventory.printSummary();

        // ── 6. Assertions – verify system consistency ──────────────────────────
        System.out.println("RUNNING CONSISTENCY ASSERTIONS …\n");

        // All confirmed room IDs must be globally unique (Set property verified here)
        java.util.Set<String> allRoomIds = new java.util.HashSet<>();
        for (var res : bookingService.getConfirmedReservations()) {
            boolean isNew = allRoomIds.add(res.getAssignedRoomId());
            assert isNew
                    : "DOUBLE-BOOKING DETECTED: room ID " + res.getAssignedRoomId() + " assigned more than once!";
        }

        // Inventory counts must be zero or positive
        for (String type : new String[] { "STANDARD", "DELUXE", "SUITE" }) {
            int remaining = inventory.getAvailableCount(type);
            assert remaining >= 0
                    : "Negative inventory for " + type + " – consistency violated!";
        }

        System.out.println("  ✓  All room IDs are unique – no double-bookings.");
        System.out.println("  ✓  Inventory is non-negative for all room types.");
        System.out.println("\n[DEMO COMPLETE]");
    }
}

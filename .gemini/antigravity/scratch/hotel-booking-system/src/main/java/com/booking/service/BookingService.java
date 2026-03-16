package com.booking.service;

import com.booking.model.BookingRequest;
import com.booking.model.Reservation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

/**
 * BookingService dequeues {@link BookingRequest}s in FIFO order, coordinates
 * with {@link InventoryService} to allocate a unique room, and produces a
 * confirmed {@link Reservation}.
 *
 * <p>
 * The class is intentionally decoupled from InventoryService so that each
 * responsibility is clearly separated:
 * <ul>
 * <li>BookingService – request queue management and business orchestration
 * <li>InventoryService – stateful room inventory and uniqueness enforcement
 * </ul>
 */
public class BookingService {

    // ── FIFO request queue ─────────────────────────────────────────────────────
    private final Queue<BookingRequest> requestQueue = new ArrayDeque<>();

    // ── dependency ─────────────────────────────────────────────────────────────
    private final InventoryService inventoryService;

    // ── confirmed reservations ledger ──────────────────────────────────────────
    private final List<Reservation> confirmedReservations = new ArrayList<>();

    public BookingService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // ── queue management ───────────────────────────────────────────────────────

    /**
     * Enqueues a new booking request (tail of FIFO queue).
     */
    public void enqueue(BookingRequest request) {
        requestQueue.offer(request);
        System.out.printf("[Booking]   Enqueued     %s%n", request);
    }

    // ── processing ─────────────────────────────────────────────────────────────

    /**
     * Processes ALL pending requests from the queue until it is empty.
     *
     * <p>
     * For each request:
     * <ol>
     * <li>Dequeue (FIFO – {@link ArrayDeque#poll()})
     * <li>Check availability via InventoryService
     * <li>Allocate a unique room ID (InventoryService enforces Set uniqueness)
     * <li>Generate a confirmation ID
     * <li>Build and store the {@link Reservation}
     * </ol>
     */
    public void processAll() {
        System.out.println("\n──────────────────────────────────────────────");
        System.out.println("       PROCESSING BOOKING QUEUE               ");
        System.out.println("──────────────────────────────────────────────");

        while (!requestQueue.isEmpty()) {
            // Step 1 – Dequeue in FIFO order
            BookingRequest req = requestQueue.poll();
            System.out.printf("%n[Booking]   Processing   %s%n", req);

            String roomType = req.getRoomType();

            // Step 2 – Check availability
            if (!inventoryService.isAvailable(roomType)) {
                System.out.printf("[Booking]   ✗ DECLINED   No '%s' rooms available for %s%n",
                        roomType, req.getGuestName());
                continue;
            }

            // Steps 3 & 4 – Allocate room (unique ID generated + inventory decremented
            // atomically)
            String assignedRoomId;
            try {
                assignedRoomId = inventoryService.allocateRoom(roomType);
            } catch (IllegalStateException e) {
                System.out.printf("[Booking]   ✗ BLOCKED    %s%n", e.getMessage());
                continue;
            }

            // Step 5 – Generate confirmation ID and build Reservation
            String confirmationId = "CONF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Reservation res = new Reservation(
                    confirmationId,
                    req.getGuestName(),
                    roomType,
                    assignedRoomId,
                    req.getNights());

            confirmedReservations.add(res);
            System.out.printf("[Booking]   ✓ CONFIRMED  %s%n", res);
        }

        System.out.println("\n──────────────────────────────────────────────");
    }

    // ── reporting ──────────────────────────────────────────────────────────────

    /** Returns an unmodifiable view of all confirmed reservations. */
    public List<Reservation> getConfirmedReservations() {
        return Collections.unmodifiableList(confirmedReservations);
    }

    /** Prints all confirmed reservations. */
    public void printConfirmedReservations() {
        System.out.println("\n══════════════════════════════════════════════");
        System.out.println("          CONFIRMED RESERVATIONS              ");
        System.out.println("══════════════════════════════════════════════");
        if (confirmedReservations.isEmpty()) {
            System.out.println("  (none)");
        } else {
            confirmedReservations.forEach(r -> System.out.println("  " + r));
        }
        System.out.println("══════════════════════════════════════════════\n");
    }
}

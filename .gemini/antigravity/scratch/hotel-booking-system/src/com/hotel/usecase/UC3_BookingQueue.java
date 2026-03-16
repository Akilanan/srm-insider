package com.hotel.usecase;

import com.hotel.model.BookingRequest;
import com.hotel.model.Guest;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * UC3: Booking Queue
 * Data Structure: ArrayDeque<BookingRequest> used as a Queue (FIFO)
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * WHY ArrayDeque as Queue?
 * ────────────────────────
 * Booking requests arrive at a hotel front desk asynchronously (online,
 * phone, walk-in). They must be served in ARRIVAL ORDER — first come,
 * first served — a textbook FIFO requirement.
 *
 * ArrayDeque<T> is Java's recommended Queue implementation:
 * - offer(e) : add to TAIL → O(1)
 * - poll() : remove from HEAD → O(1) (null if empty)
 * - peek() : inspect head without removal → O(1)
 * - No null-element overhead (unlike LinkedList nodes)
 * - Faster than LinkedList for queue workloads due to array locality
 *
 * FIFO GUARANTEE: Whoever called offer() first is poll()'d first.
 * This mirrors a fair ticketing system — no one is skipped.
 *
 * LIMITATION INTRODUCED HERE (leads to UC4):
 * Pure FIFO ignores guest VIP status. A Platinum guest who arrives
 * second is still served second. UC4 solves this with PriorityQueue.
 */
public class UC3_BookingQueue {

    // FIFO queue — tail = newest request, head = next to process
    private final Queue<BookingRequest> requestQueue = new ArrayDeque<>();
    private final List<String> processedLog = new ArrayList<>();

    /** Enqueue a request at the TAIL. */
    public void enqueue(BookingRequest request) {
        requestQueue.offer(request); // O(1)
        System.out.println("  [Queue]  → Enqueued  : " + request.getGuest().getName()
                + " (" + request.getRoomType() + ") | Queue size: " + requestQueue.size());
    }

    /** Peek at the next request to be processed without removing it. */
    public BookingRequest peek() {
        return requestQueue.peek(); // O(1)
    }

    /**
     * Process ALL requests in strict FIFO order.
     * Simulates the front desk working through the queue one by one.
     */
    public void processAll() {
        System.out.println("\n  [Queue]  ─── Processing queue (FIFO) ───");
        int position = 1;
        while (!requestQueue.isEmpty()) {
            BookingRequest req = requestQueue.poll(); // remove from HEAD — O(1)
            String log = String.format("    Served #%d : %-10s | Type: %-10s | Check-in: %s",
                    position++,
                    req.getGuest().getName(),
                    req.getRoomType(),
                    req.getCheckInDate());
            System.out.println(log);
            processedLog.add(req.getGuest().getName());
        }
    }

    public int size() {
        return requestQueue.size();
    }

    public List<String> getProcessedLog() {
        return processedLog;
    }

    // ── Demo ──────────────────────────────────────────────────────────────
    public static void demo() {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│  UC3 · Booking Queue  (ArrayDeque / FIFO)               │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        System.out.println("  Data Structure : ArrayDeque<BookingRequest> as Queue");
        System.out.println("  Concept        : FIFO — fair request handling\n");

        UC3_BookingQueue queue = new UC3_BookingQueue();

        LocalDate base = LocalDate.of(2024, 6, 1);

        // Enqueue in arrival order: Alice=Standard guest, Bob=VIP(5), Carol=Standard
        // Note: even though Bob is VIP, he arrives second → served second (pure FIFO)
        Guest alice = new Guest("G001", "Alice", "a@hotel.com", 1);
        Guest bob = new Guest("G002", "Bob", "b@hotel.com", 5); // Platinum VIP – arrives 2nd
        Guest carol = new Guest("G003", "Carol", "c@hotel.com", 1);
        Guest david = new Guest("G004", "David", "d@hotel.com", 2);

        queue.enqueue(new BookingRequest("REQ-1", alice, "STANDARD", base, 2));
        queue.enqueue(new BookingRequest("REQ-2", bob, "SUITE", base.plusDays(1), 3));
        queue.enqueue(new BookingRequest("REQ-3", carol, "DELUXE", base.plusDays(2), 1));
        queue.enqueue(new BookingRequest("REQ-4", david, "STANDARD", base.plusDays(1), 2));

        System.out.println("\n  [Queue]  Head of queue: " + queue.peek().getGuest().getName()
                + " (first to be served)");

        queue.processAll();

        // ── Assertions ────────────────────────────────────────────────────
        System.out.println("\n  [Assertions]");
        List<String> log = queue.getProcessedLog();
        assert log.size() == 4 : "FAIL: Expected 4 processed";
        assert log.get(0).equals("Alice") : "FAIL: Alice should be first (FIFO)";
        assert log.get(1).equals("Bob") : "FAIL: Bob should be second (arrives 2nd)";
        assert queue.size() == 0 : "FAIL: Queue should be empty after processing";
        System.out.println("  ✓ PASS – 4 requests processed in strict FIFO order.");
        System.out.println("  ⚠  Limitation: Bob (VIP=5) served after Alice (VIP=1) — UC4 fixes this!");
    }
}

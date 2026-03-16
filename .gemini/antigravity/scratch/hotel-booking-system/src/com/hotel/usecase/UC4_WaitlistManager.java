package com.hotel.usecase;

import com.hotel.model.BookingRequest;
import com.hotel.model.Guest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * UC4: VIP Waitlist Manager
 * Data Structure: PriorityQueue<BookingRequest>
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * WHY PriorityQueue?
 * ──────────────────
 * UC3 showed that pure FIFO ignores guest VIP status. A hotel's premium
 * tier program requires Platinum members to be served before Standard
 * members — regardless of arrival order.
 *
 * PriorityQueue<T> is a MIN-HEAP by default:
 * - poll() : always returns the element with the LOWEST compareTo value → O(log
 * n)
 * - offer() : insert in heap order → O(log n)
 * - peek() : inspect min element → O(1)
 *
 * Since our BookingRequest.compareTo() reverses priority (higher number = lower
 * compareTo), poll() consistently returns the HIGHEST VIP guest.
 *
 * REAL-WORLD ANALOGY:
 * Airport priority boarding — passengers are called by status tier,
 * not by arrival time at the gate.
 *
 * TRADE-OFF vs UC3:
 * FIFO (ArrayDeque) : fair for all, O(1) enqueue & dequeue
 * Priority (PQ) : fair by status, O(log n) enqueue & dequeue
 */
public class UC4_WaitlistManager {

    // Min-heap on BookingRequest.compareTo() → effectively max-heap on VIP priority
    private final PriorityQueue<BookingRequest> waitlist = new PriorityQueue<>();
    private final List<String> servedOrder = new ArrayList<>();

    /** Add a request to the waitlist (heap insertion). */
    public void addToWaitlist(BookingRequest request) {
        waitlist.offer(request); // O(log n)
        System.out.printf("  [Waitlist]  → Added  : %-10s  VIP=%-1d  | Waitlist size: %d%n",
                request.getGuest().getName(),
                request.getGuest().getVipPriority(),
                waitlist.size());
    }

    /**
     * Serve ALL waitlisted guests in PRIORITY ORDER.
     * Highest VIP guest is poll()'d first.
     */
    public void serveAll() {
        System.out.println("\n  [Waitlist]  ─── Serving by VIP Priority (highest first) ───");
        int rank = 1;
        while (!waitlist.isEmpty()) {
            BookingRequest req = waitlist.poll(); // O(log n) — heap restructure
            String msg = String.format("    Rank #%d : %-10s  VIP=%-1d  | %s",
                    rank++,
                    req.getGuest().getName(),
                    req.getGuest().getVipPriority(),
                    req.getRoomType());
            System.out.println(msg);
            servedOrder.add(req.getGuest().getName());
        }
    }

    public int size() {
        return waitlist.size();
    }

    public List<String> getServedOrder() {
        return servedOrder;
    }

    // ── Demo ──────────────────────────────────────────────────────────────
    public static void demo() {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│  UC4 · VIP Waitlist  (PriorityQueue<BookingRequest>)    │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        System.out.println("  Data Structure : PriorityQueue<BookingRequest>");
        System.out.println("  Concept        : Priority-based ordering (min-heap / VIP-first)\n");

        UC4_WaitlistManager waitlist = new UC4_WaitlistManager();

        LocalDate checkIn = LocalDate.of(2024, 7, 15);

        // Add in mixed order — PriorityQueue will serve in VIP order regardless
        Guest frank = new Guest("G010", "Frank", "f@h.com", 1); // Standard
        Guest grace = new Guest("G011", "Grace", "g@h.com", 5); // Platinum VIP (added 2nd)
        Guest henry = new Guest("G012", "Henry", "h@h.com", 3); // Gold
        Guest irene = new Guest("G013", "Irene", "i@h.com", 5); // Platinum VIP (added 4th)
        Guest jack = new Guest("G014", "Jack", "j@h.com", 2); // Silver

        waitlist.addToWaitlist(new BookingRequest("W-1", frank, "STANDARD", checkIn, 2));
        waitlist.addToWaitlist(new BookingRequest("W-2", grace, "SUITE", checkIn, 3));
        waitlist.addToWaitlist(new BookingRequest("W-3", henry, "DELUXE", checkIn, 1));
        waitlist.addToWaitlist(new BookingRequest("W-4", irene, "SUITE", checkIn, 2));
        waitlist.addToWaitlist(new BookingRequest("W-5", jack, "STANDARD", checkIn, 1));

        waitlist.serveAll();

        // ── Assertions ────────────────────────────────────────────────────
        System.out.println("\n  [Assertions]");
        List<String> order = waitlist.getServedOrder();
        assert order.size() == 5 : "FAIL: Expected 5 served";
        // First two must be Platinum VIPs (priority=5): Grace and Irene
        assert (order.get(0).equals("Grace") || order.get(0).equals("Irene"))
                : "FAIL: First served should be a Platinum VIP (Grace or Irene)";
        assert (order.get(1).equals("Grace") || order.get(1).equals("Irene"))
                : "FAIL: Second served should be a Platinum VIP (Grace or Irene)";
        // Frank (VIP=1) must be last
        assert order.get(4).equals("Frank") : "FAIL: Standard guest Frank should be last";
        System.out.println("  ✓ PASS – Both Platinum VIPs served first; Standard guest served last.");
    }
}

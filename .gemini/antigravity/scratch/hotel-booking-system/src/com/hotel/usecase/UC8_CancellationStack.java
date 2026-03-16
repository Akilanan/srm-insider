package com.hotel.usecase;

import com.hotel.model.Guest;
import com.hotel.model.Reservation;
import com.hotel.model.Room;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EmptyStackException;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * UC8: Cancellation Stack (Undo / Rollback)
 * Data Structure: Deque<Reservation> used as a Stack (LIFO)
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * WHY Stack (LIFO)?
 * ─────────────────
 * Hotel policy: a guest who cancels should get refunded in REVERSE ORDER
 * of their reservation — the MOST RECENT booking is the easiest to undo
 * (e.g., the room hasn't been prepared yet), mirroring how "ctrl+Z" undo
 * works in any editor.
 *
 * LIFO = Last In, First Out:
 * push(reservation) : add to TOP → O(1)
 * pop() : remove from TOP → O(1) (most recent booking cancelled first)
 * peek() : inspect top without removal → O(1)
 *
 * WHY ArrayDeque instead of java.util.Stack?
 * ────────────────────────────────────────────
 * java.util.Stack extends Vector which is synchronized — unnecessary overhead.
 * Java documentation explicitly recommends Deque for LIFO stacks.
 * ArrayDeque provides O(1) push/pop with no synchronization cost.
 *
 * OPERATIONS:
 * push(e) = addFirst(e) O(1)
 * pop() = removeFirst() O(1) – throws if empty
 * peek() = peekFirst() O(1) – null if empty
 *
 * REAL-WORLD ANALOGY:
 * A stack of hotel check-in slips — the most recent slip is on top
 * and is the first to be removed when a cancellation is processed.
 */
public class UC8_CancellationStack {

    // Stack of confirmed reservations (top = most recent)
    private final Deque<Reservation> cancellationStack = new ArrayDeque<>();
    private int cancelledCount = 0;

    /** Push a new reservation onto the stack when confirmed. */
    public void push(Reservation reservation) {
        cancellationStack.addFirst(reservation); // addFirst = push to top, O(1)
        System.out.printf("  [Stack]  ↑ Pushed  : %-10s (conf: %s) | Stack size: %d%n",
                reservation.getGuest().getName(),
                reservation.getConfirmationId(),
                cancellationStack.size());
    }

    /**
     * Cancel the most recent reservation (pop from top of stack).
     * Simulates an "undo last booking" operation.
     */
    public Reservation cancel() {
        if (cancellationStack.isEmpty()) {
            throw new EmptyStackException();
        }
        Reservation cancelled = cancellationStack.removeFirst(); // removeFirst = pop, O(1)
        cancelledCount++;
        System.out.printf("  [Stack]  ↓ Cancelled: %-10s (conf: %s) | Refund: $%.2f | Remaining: %d%n",
                cancelled.getGuest().getName(),
                cancelled.getConfirmationId(),
                cancelled.getTotalCost(),
                cancellationStack.size());
        return cancelled;
    }

    /** Inspect the most recent reservation without removing it. */
    public Reservation peek() {
        return cancellationStack.peekFirst(); // O(1)
    }

    public int size() {
        return cancellationStack.size();
    }

    public int cancelledCount() {
        return cancelledCount;
    }

    public boolean isEmpty() {
        return cancellationStack.isEmpty();
    }

    // ── Demo ──────────────────────────────────────────────────────────────
    public static void demo() {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│  UC8 · Cancellation Stack  (ArrayDeque as LIFO Stack)   │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        System.out.println("  Data Structure : Deque<Reservation> → ArrayDeque (Stack / LIFO)");
        System.out.println("  Concept        : LIFO undo — most recent booking cancelled first\n");

        UC8_CancellationStack stack = new UC8_CancellationStack();

        // Create sample reservations
        LocalDate d = LocalDate.of(2024, 10, 1);
        Guest[] guests = {
                new Guest("G1", "Alice", "a@h.com", 1),
                new Guest("G2", "Bob", "b@h.com", 2),
                new Guest("G3", "Carol", "c@h.com", 3),
                new Guest("G4", "David", "d@h.com", 4),
        };
        Room[] rooms = {
                new Room("R101", "STANDARD", 80.0),
                new Room("R201", "DELUXE", 140.0),
                new Room("R301", "SUITE", 250.0),
                new Room("P001", "PENTHOUSE", 600.0),
        };
        String[] confs = { "CONF-AAA1", "CONF-BBB2", "CONF-CCC3", "CONF-DDD4" };

        System.out.println("  Booking 4 reservations (each pushed onto the cancellation stack)...");
        for (int i = 0; i < guests.length; i++) {
            Reservation r = new Reservation(confs[i], guests[i], rooms[i], d.plusDays(i), 2);
            stack.push(r);
        }

        System.out.println("\n  Most recent reservation (top of stack): "
                + (stack.peek() != null ? stack.peek().getGuest().getName() : "none"));

        System.out.println("\n  Cancelling last 2 bookings (LIFO order)...");
        Reservation c1 = stack.cancel(); // David (most recent)
        Reservation c2 = stack.cancel(); // Carol

        System.out.println("\n  Remaining on stack: " + stack.size());

        // ── Assertions ────────────────────────────────────────────────────
        System.out.println("\n  [Assertions]");
        assert c1.getGuest().getName().equals("David") : "FAIL: David should be cancelled first (LIFO)";
        assert c2.getGuest().getName().equals("Carol") : "FAIL: Carol should be cancelled second";
        assert stack.size() == 2 : "FAIL: 2 reservations should remain";
        assert stack.cancelledCount() == 2 : "FAIL: Cancelled count should be 2";
        assert stack.peek().getGuest().getName().equals("Bob") : "FAIL: Bob should now be at top";
        System.out.println("  ✓ PASS – LIFO order confirmed. David cancelled first, Carol second.");
    }
}

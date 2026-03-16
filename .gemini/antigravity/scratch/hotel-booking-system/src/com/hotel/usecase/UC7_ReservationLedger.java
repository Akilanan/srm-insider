package com.hotel.usecase;

import com.hotel.model.Guest;
import com.hotel.model.Reservation;
import com.hotel.model.Room;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * UC7: Reservation Ledger
 * Data Structure: TreeMap<LocalDate, Reservation>
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * WHY TreeMap?
 * ────────────
 * The hotel's finance team needs a ledger ordered by CHECK-IN DATE to:
 * - Generate chronological revenue reports
 * - Find all reservations in a date range (e.g., this month)
 * - Identify the earliest/latest check-in dates instantly
 *
 * TreeMap<K, V> is a RED-BLACK TREE under the hood:
 * - Keys always stay sorted by natural order (or Comparator)
 * - put(key, value) : O(log n) – tree insertion
 * - get(key) : O(log n) – tree traversal
 * - firstKey() : O(log n) – leftmost node
 * - lastKey() : O(log n) – rightmost node
 * - subMap(from, to) : O(log n + k) – range query (k = results)
 *
 * CONTRAST with HashMap (UC5):
 * HashMap : O(1) avg lookup, but NO ordering guarantee
 * TreeMap : O(log n) lookup, but ALWAYS sorted by key
 *
 * LocalDate implements Comparable naturally (chronological order),
 * so no custom Comparator is needed.
 *
 * NOTE: For simplicity, this demo uses one reservation per date.
 * A production system would use TreeMap<LocalDate, List<Reservation>>.
 */
public class UC7_ReservationLedger {

    // Sorted by check-in date ascending (TreeMap natural order on LocalDate)
    private final TreeMap<LocalDate, Reservation> ledger = new TreeMap<>();

    /** Add a reservation to the ledger indexed by check-in date. */
    public void addReservation(Reservation reservation) {
        ledger.put(reservation.getCheckInDate(), reservation); // O(log n)
        System.out.printf("  [Ledger]  Added  : check-in %-12s → %s%n",
                reservation.getCheckInDate(), reservation.getGuest().getName());
    }

    /** Get the earliest check-in date in the ledger. */
    public LocalDate earliestCheckIn() {
        return ledger.isEmpty() ? null : ledger.firstKey(); // O(log n)
    }

    /** Get the latest check-in date in the ledger. */
    public LocalDate latestCheckIn() {
        return ledger.isEmpty() ? null : ledger.lastKey(); // O(log n)
    }

    /**
     * Date-range query: reservations whose check-in falls in [from, to].
     * subMap is O(log n + k) where k = number of results.
     */
    public Collection<Reservation> reservationsInRange(LocalDate from, LocalDate to) {
        return ledger.subMap(from, true, to, true).values(); // O(log n + k)
    }

    /** Chronological report — iteration is O(n) in sorted order. */
    public void printLedger() {
        System.out.println("\n  [Ledger]  Chronological Reservation Report:");
        System.out.println("    ┌────────────┬──────────┬──────────────┬────────┬──────────┐");
        System.out.println("    │ Check-In   │ Guest    │ Confirmation │ Nights │ Total    │");
        System.out.println("    ├────────────┼──────────┼──────────────┼────────┼──────────┤");
        for (Map.Entry<LocalDate, Reservation> e : ledger.entrySet()) {
            Reservation r = e.getValue();
            System.out.printf("    │ %-10s │ %-8s │ %-12s │   %-4d │ $%-7.2f│%n",
                    r.getCheckInDate(), r.getGuest().getName(),
                    r.getConfirmationId(), r.getNights(), r.getTotalCost());
        }
        System.out.println("    └────────────┴──────────┴──────────────┴────────┴──────────┘");
    }

    public int size() {
        return ledger.size();
    }

    // ── Demo ──────────────────────────────────────────────────────────────
    public static void demo() {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│  UC7 · Reservation Ledger  (TreeMap<LocalDate,…>)       │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        System.out.println("  Data Structure : TreeMap<LocalDate, Reservation>");
        System.out.println("  Concept        : Sorted map, chronological queries, range scan\n");

        UC7_ReservationLedger ledger = new UC7_ReservationLedger();

        // Add reservations OUT OF DATE ORDER — TreeMap will sort them
        Guest[] guests = {
                new Guest("G1", "Alice", "a@h.com", 1),
                new Guest("G2", "Bob", "b@h.com", 3),
                new Guest("G3", "Carol", "c@h.com", 5),
                new Guest("G4", "David", "d@h.com", 2),
                new Guest("G5", "Eve", "e@h.com", 4),
        };
        String[] confIds = { "CONF-AAAA", "CONF-BBBB", "CONF-CCCC", "CONF-DDDD", "CONF-EEEE" };
        LocalDate[] dates = {
                LocalDate.of(2024, 9, 15),
                LocalDate.of(2024, 9, 1), // earlier — added second, sorted first
                LocalDate.of(2024, 9, 20),
                LocalDate.of(2024, 9, 8),
                LocalDate.of(2024, 9, 25),
        };
        double[] prices = { 80.0, 140.0, 250.0, 80.0, 140.0 };
        int[] nights = { 3, 2, 4, 1, 2 };

        for (int i = 0; i < guests.length; i++) {
            Room room = new Room("R-" + (100 + i), "STANDARD", prices[i]);
            Reservation r = new Reservation(confIds[i], guests[i], room, dates[i], nights[i]);
            ledger.addReservation(r);
        }

        ledger.printLedger();

        System.out.println("\n  Earliest check-in : " + ledger.earliestCheckIn());
        System.out.println("  Latest check-in   : " + ledger.latestCheckIn());

        LocalDate rangeFrom = LocalDate.of(2024, 9, 8);
        LocalDate rangeTo = LocalDate.of(2024, 9, 20);
        Collection<Reservation> rangeResult = ledger.reservationsInRange(rangeFrom, rangeTo);
        System.out.println("\n  Reservations Sept 8–20 (range query):");
        for (Reservation r : rangeResult) {
            System.out.println("    → " + r.getGuest().getName() + " on " + r.getCheckInDate());
        }

        // ── Assertions ────────────────────────────────────────────────────
        System.out.println("\n  [Assertions]");
        assert ledger.size() == 5 : "FAIL: Should have 5 reservations";
        assert ledger.earliestCheckIn().equals(LocalDate.of(2024, 9, 1)) : "FAIL: Earliest should be Sept 1";
        assert ledger.latestCheckIn().equals(LocalDate.of(2024, 9, 25)) : "FAIL: Latest should be Sept 25";
        assert rangeResult.size() == 3 : "FAIL: Range should return 3";
        System.out.println("  ✓ PASS – Ledger sorted chronologically. Range query returns correct results.");
    }
}

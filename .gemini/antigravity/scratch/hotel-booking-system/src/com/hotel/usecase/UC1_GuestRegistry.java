package com.hotel.usecase;

import com.hotel.model.Guest;

import java.util.ArrayList;
import java.util.List;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * UC1: Guest Registry
 * Data Structure: ArrayList<Guest>
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * WHY ArrayList?
 * ──────────────
 * The hotel needs a dynamic list of registered guests that:
 * - Grows as new guests check in → ArrayList auto-resizes (amortized O(1) add)
 * - Allows iteration over ALL guests for reports → O(n) sequential scan
 * - Supports index-based access for displaying guest #N → O(1) get(index)
 *
 * A plain array would require manual resizing. A LinkedList would be slower
 * for index access. ArrayList is the right balance for a read-heavy registry.
 *
 * OPERATIONS:
 * add(guest) O(1) amortized – add to end
 * get(index) O(1) – index access
 * linear search O(n) – scan for matching guestId
 * size() O(1)
 */
public class UC1_GuestRegistry {

    // The registry – backed by a resizable array
    private final List<Guest> guestList = new ArrayList<>();

    /** Register a new guest at the tail of the list. */
    public void registerGuest(Guest guest) {
        guestList.add(guest); // O(1) amortised
        System.out.println("  [Registry] Registered : " + guest);
    }

    /**
     * Find a guest by their ID using a linear scan.
     *
     * O(n) – acceptable for a small guest registry. In a production system
     * this would be backed by a HashMap for O(1) lookup (see UC5).
     */
    public Guest findGuestById(String guestId) {
        for (Guest g : guestList) { // O(n)
            if (g.getGuestId().equals(guestId)) {
                return g;
            }
        }
        return null;
    }

    /** Print all registered guests with index numbers. */
    public void printAllGuests() {
        System.out.println("\n  [Registry] All Registered Guests (" + guestList.size() + " total):");
        for (int i = 0; i < guestList.size(); i++) {
            System.out.printf("    [%d] %s%n", i + 1, guestList.get(i));
        }
    }

    public int size() {
        return guestList.size();
    }

    // ── Demo ──────────────────────────────────────────────────────────────
    public static void demo() {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│  UC1 · Guest Registry  (ArrayList<Guest>)               │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        System.out.println("  Data Structure : ArrayList<Guest>");
        System.out.println("  Concept        : Dynamic list, index access, linear search\n");

        UC1_GuestRegistry registry = new UC1_GuestRegistry();

        // Register guests
        Guest alice = new Guest("G001", "Alice", "alice@email.com", 1);
        Guest bob = new Guest("G002", "Bob", "bob@email.com", 3);
        Guest carol = new Guest("G003", "Carol", "carol@email.com", 5); // Platinum VIP
        Guest david = new Guest("G004", "David", "david@email.com", 2);
        Guest eve = new Guest("G005", "Eve", "eve@email.com", 4);

        registry.registerGuest(alice);
        registry.registerGuest(bob);
        registry.registerGuest(carol);
        registry.registerGuest(david);
        registry.registerGuest(eve);

        registry.printAllGuests();

        // Linear search demo
        System.out.println("\n  [Registry] Searching for guest ID 'G003'...");
        Guest found = registry.findGuestById("G003");
        System.out.println("  [Registry] Found      : " + found);

        System.out.println("\n  [Registry] Searching for guest ID 'G999' (not in list)...");
        Guest notFound = registry.findGuestById("G999");
        System.out.println("  [Registry] Found      : " + (notFound == null ? "null (not found)" : notFound));

        // ── Assertions ────────────────────────────────────────────────────
        System.out.println("\n  [Assertions]");
        assert registry.size() == 5 : "FAIL: Expected 5 guests";
        assert found != null : "FAIL: G003 should be found";
        assert found.getName().equals("Carol") : "FAIL: G003 should be Carol";
        assert notFound == null : "FAIL: G999 should not exist";
        System.out.println("  ✓ PASS – 5 guests registered, search works correctly.");
    }
}

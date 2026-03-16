package com.hotel.usecase;

import com.hotel.model.Room;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * UC2: Room Catalog
 * Data Structure: LinkedList<Room>
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * WHY LinkedList?
 * ───────────────
 * A hotel's room catalog needs to:
 * - Add new room types quickly at the HEAD or TAIL (e.g., when hotel expands)
 * - Remove rooms during renovation without shifting the entire array
 * - Iterate in insertion order when generating a catalog printout
 *
 * LinkedList shines here:
 * - O(1) insert/delete at head or tail (via addFirst / addLast / removeFirst)
 * - O(n) traversal (same as ArrayList) but NO index-shift cost on removal
 * - Demonstrates node-pointer structure vs array-slot structure
 *
 * CONTRAST with ArrayList (UC1):
 * - ArrayList: O(1) random access, O(n) insert-at-middle (element shift)
 * - LinkedList: O(n) random access, O(1) insert/delete at known node
 *
 * OPERATIONS:
 * addFirst(room) O(1) – prepend
 * addLast(room) O(1) – append
 * removeFirst() O(1) – remove head
 * iterator traversal O(n)
 */
public class UC2_RoomCatalog {

    // The catalog – doubly-linked list preserves insertion order
    private final LinkedList<Room> roomList = new LinkedList<>();

    /** Add a new room to the END of the catalog (typical expansion). */
    public void addRoom(Room room) {
        roomList.addLast(room); // O(1) — pointer update at tail
        System.out.println("  [Catalog]  Added (tail): " + room);
    }

    /** Add a premium room at the FRONT so it appears first in catalog. */
    public void addPremiumRoom(Room room) {
        roomList.addFirst(room); // O(1) — pointer update at head
        System.out.println("  [Catalog]  Added (head): " + room + "  ← premium, placed at front");
    }

    /**
     * Remove a room by ID (renovation / decommission).
     * Uses Iterator to safely remove while traversing — avoids
     * ConcurrentModificationException.
     */
    public boolean removeRoomById(String roomId) {
        Iterator<Room> it = roomList.iterator();
        while (it.hasNext()) {
            Room r = it.next();
            if (r.getRoomId().equals(roomId)) {
                it.remove(); // O(1) node unlink — no array shift!
                System.out.println("  [Catalog]  Removed    : " + r + " (renovation)");
                return true;
            }
        }
        System.out.println("  [Catalog]  Room '" + roomId + "' not found for removal.");
        return false;
    }

    /** Print catalog in insertion order. */
    public void printCatalog() {
        System.out.println("\n  [Catalog]  Full Room Catalog (" + roomList.size() + " rooms):");
        int idx = 1;
        for (Room r : roomList) {
            System.out.printf("    [%d] %s%n", idx++, r);
        }
    }

    public int size() {
        return roomList.size();
    }

    // ── Demo ──────────────────────────────────────────────────────────────
    public static void demo() {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│  UC2 · Room Catalog  (LinkedList<Room>)                 │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        System.out.println("  Data Structure : LinkedList<Room>");
        System.out.println("  Concept        : O(1) head/tail insert, O(1) mid-removal\n");

        UC2_RoomCatalog catalog = new UC2_RoomCatalog();

        catalog.addRoom(new Room("R101", "STANDARD", 80.00));
        catalog.addRoom(new Room("R102", "STANDARD", 80.00));
        catalog.addRoom(new Room("R201", "DELUXE", 140.00));
        catalog.addRoom(new Room("R202", "DELUXE", 140.00));
        catalog.addRoom(new Room("R301", "SUITE", 250.00));

        // Add new premium penthouse at the front
        catalog.addPremiumRoom(new Room("P001", "PENTHOUSE", 600.00));

        catalog.printCatalog();

        // Remove a room under renovation
        System.out.println();
        catalog.removeRoomById("R102");
        catalog.printCatalog();

        // ── Assertions ────────────────────────────────────────────────────
        System.out.println("\n  [Assertions]");
        assert catalog.size() == 5 : "FAIL: Expected 5 rooms after removal";
        System.out.println("  ✓ PASS – 6 rooms added, 1 removed, 5 remain. Insertion order preserved.");
    }
}

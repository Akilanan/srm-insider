package com.hotel.model;

/**
 * Value object representing a hotel guest.
 *
 * Implements Comparable<Guest> so that PriorityQueue (UC4) can order
 * guests by VIP priority — higher priority value = served first.
 */
public class Guest implements Comparable<Guest> {

    private final String guestId;
    private final String name;
    private final String email;
    private final int vipPriority; // 1 = standard, 5 = platinum VIP

    public Guest(String guestId, String name, String email, int vipPriority) {
        this.guestId     = guestId;
        this.name        = name;
        this.email       = email;
        this.vipPriority = vipPriority;
    }

    // ── Accessors ──────────────────────────────────────────────────────────
    public String getGuestId()     { return guestId;     }
    public String getName()        { return name;        }
    public String getEmail()       { return email;       }
    public int    getVipPriority() { return vipPriority; }

    /**
     * Natural ordering: HIGHER priority number → polled FIRST from PriorityQueue.
     * PriorityQueue is min-heap by default, so we invert the comparison.
     */
    @Override
    public int compareTo(Guest other) {
        return Integer.compare(other.vipPriority, this.vipPriority); // descending
    }

    @Override
    public String toString() {
        return String.format("Guest{id='%s', name='%s', vip=%d}", guestId, name, vipPriority);
    }
}

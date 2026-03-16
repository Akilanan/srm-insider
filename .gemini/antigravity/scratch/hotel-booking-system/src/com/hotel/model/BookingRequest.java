package com.hotel.model;

import java.time.LocalDate;

/**
 * Immutable value object representing a guest's booking request.
 *
 * Implements Comparable<BookingRequest> so that PriorityQueue (UC4)
 * can order requests by the guest's VIP priority (higher = first).
 */
public class BookingRequest implements Comparable<BookingRequest> {

    private final String    requestId;
    private final Guest     guest;
    private final String    roomType;      // STANDARD | DELUXE | SUITE
    private final LocalDate checkInDate;
    private final int       nights;

    public BookingRequest(String requestId, Guest guest,
                          String roomType, LocalDate checkInDate, int nights) {
        this.requestId   = requestId;
        this.guest       = guest;
        this.roomType    = roomType.toUpperCase();
        this.checkInDate = checkInDate;
        this.nights      = nights;
    }

    // ── Accessors ──────────────────────────────────────────────────────────
    public String    getRequestId()   { return requestId;   }
    public Guest     getGuest()       { return guest;       }
    public String    getRoomType()    { return roomType;    }
    public LocalDate getCheckInDate() { return checkInDate; }
    public int       getNights()      { return nights;      }

    /**
     * PriorityQueue ordering: delegate to Guest's VIP priority (descending).
     * Highest VIP level is served first.
     */
    @Override
    public int compareTo(BookingRequest other) {
        return this.guest.compareTo(other.guest);
    }

    @Override
    public String toString() {
        return String.format("BookingRequest{id='%s', guest='%s', type='%s', checkIn=%s, nights=%d}",
                requestId, guest.getName(), roomType, checkInDate, nights);
    }
}

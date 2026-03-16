package com.booking.model;

/**
 * Represents a single booking request queued by a guest.
 */
public class BookingRequest {

    private final String guestName;
    private final String roomType;   // e.g. "DELUXE", "SUITE", "STANDARD"
    private final int nights;

    public BookingRequest(String guestName, String roomType, int nights) {
        this.guestName = guestName;
        this.roomType  = roomType.toUpperCase();
        this.nights    = nights;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType()  { return roomType;  }
    public int    getNights()    { return nights;     }

    @Override
    public String toString() {
        return String.format("BookingRequest{guest='%s', type='%s', nights=%d}",
                guestName, roomType, nights);
    }
}

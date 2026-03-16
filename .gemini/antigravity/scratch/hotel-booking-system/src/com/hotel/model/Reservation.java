package com.hotel.model;

import java.time.LocalDate;

/**
 * Immutable confirmed reservation record.
 *
 * Created only when a BookingRequest is successfully matched to a Room.
 * Stored in the ReservationLedger (UC7) and can be pushed/popped
 * from a CancellationStack (UC8).
 */
public class Reservation {

    private final String confirmationId;
    private final Guest guest;
    private final Room room;
    private final LocalDate checkInDate;
    private final int nights;
    private final double totalCost;

    public Reservation(String confirmationId, Guest guest, Room room,
            LocalDate checkInDate, int nights) {
        this.confirmationId = confirmationId;
        this.guest = guest;
        this.room = room;
        this.checkInDate = checkInDate;
        this.nights = nights;
        this.totalCost = room.getPricePerNight() * nights;
    }

    // ── Accessors ──────────────────────────────────────────────────────────
    public String getConfirmationId() {
        return confirmationId;
    }

    public Guest getGuest() {
        return guest;
    }

    public Room getRoom() {
        return room;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public int getNights() {
        return nights;
    }

    public double getTotalCost() {
        return totalCost;
    }

    @Override
    public String toString() {
        return String.format(
                "Reservation{conf='%s', guest='%s', room='%s', checkIn=%s, nights=%d, total=$%.2f}",
                confirmationId, guest.getName(), room.getRoomId(), checkInDate, nights, totalCost);
    }
}

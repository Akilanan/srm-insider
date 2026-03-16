package com.hotel.model;

/**
 * Immutable value object representing a hotel room.
 *
 * Used as the concrete entity assigned to a confirmed reservation.
 * Room type is stored in uppercase to ensure consistent HashMap lookups.
 */
public class Room {

    private final String roomId;
    private final String type;           // STANDARD | DELUXE | SUITE
    private final double pricePerNight;

    public Room(String roomId, String type, double pricePerNight) {
        this.roomId       = roomId;
        this.type         = type.toUpperCase();
        this.pricePerNight = pricePerNight;
    }

    // ── Accessors ──────────────────────────────────────────────────────────
    public String getRoomId()        { return roomId;        }
    public String getType()          { return type;          }
    public double getPricePerNight() { return pricePerNight; }

    @Override
    public String toString() {
        return String.format("Room{id='%s', type='%s', price=%.2f/night}", roomId, type, pricePerNight);
    }
}

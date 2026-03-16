package com.booking.model;

/**
 * Represents a confirmed reservation returned after successful room allocation.
 */
public class Reservation {

    private final String confirmationId;
    private final String guestName;
    private final String roomType;
    private final String assignedRoomId;
    private final int    nights;

    public Reservation(String confirmationId,
                       String guestName,
                       String roomType,
                       String assignedRoomId,
                       int    nights) {
        this.confirmationId  = confirmationId;
        this.guestName       = guestName;
        this.roomType        = roomType;
        this.assignedRoomId  = assignedRoomId;
        this.nights          = nights;
    }

    public String getConfirmationId()  { return confirmationId;  }
    public String getGuestName()       { return guestName;       }
    public String getRoomType()        { return roomType;         }
    public String getAssignedRoomId()  { return assignedRoomId;  }
    public int    getNights()          { return nights;           }

    @Override
    public String toString() {
        return String.format(
            "Reservation{confirmationId='%s', guest='%s', room='%s', type='%s', nights=%d}",
            confirmationId, guestName, assignedRoomId, roomType, nights);
    }
}

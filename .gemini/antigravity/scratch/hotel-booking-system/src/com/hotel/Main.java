package com.hotel;

import com.hotel.usecase.UC1_GuestRegistry;
import com.hotel.usecase.UC2_RoomCatalog;
import com.hotel.usecase.UC3_BookingQueue;
import com.hotel.usecase.UC4_WaitlistManager;
import com.hotel.usecase.UC5_InventoryTracker;
import com.hotel.usecase.UC6_RoomAllocation;
import com.hotel.usecase.UC7_ReservationLedger;
import com.hotel.usecase.UC8_CancellationStack;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * Hotel Booking Management System – Main Demo Runner
 * Core Java & Data Structures · App-Based Learning Project
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * Runs all 8 use cases sequentially, each demonstrating a different
 * Java data structure applied to a real hotel management scenario.
 *
 * Compile & Run (PowerShell):
 * .\compile_and_run.ps1
 *
 * Or manually:
 * javac -d out -sourcepath src (Get-ChildItem -Recurse src -Filter
 * *.java).FullName
 * java -ea -cp out com.hotel.Main
 * ═══════════════════════════════════════════════════════════════════════════
 */
public class Main {

    public static void main(String[] args) {

        printBanner();

        // ── UC1: ArrayList – Guest Registry ──────────────────────────────
        UC1_GuestRegistry.demo();

        // ── UC2: LinkedList – Room Catalog ───────────────────────────────
        UC2_RoomCatalog.demo();

        // ── UC3: ArrayDeque (FIFO) – Booking Queue ───────────────────────
        UC3_BookingQueue.demo();

        // ── UC4: PriorityQueue – VIP Waitlist Manager ────────────────────
        UC4_WaitlistManager.demo();

        // ── UC5: HashMap – Real-Time Inventory Tracker ───────────────────
        UC5_InventoryTracker.demo();

        // ── UC6: HashMap + Set – Room Allocation & Double-Booking Guard ──
        UC6_RoomAllocation.demo();

        // ── UC7: TreeMap – Chronological Reservation Ledger ──────────────
        UC7_ReservationLedger.demo();

        // ── UC8: Stack (LIFO) – Cancellation / Undo ─────────────────────
        UC8_CancellationStack.demo();

        printSummaryTable();

        System.out.println("\n╔═════════════════════════════════════════════════════════╗");
        System.out.println("║            ✓  ALL USE CASES COMPLETE  ✓                ║");
        System.out.println("╚═════════════════════════════════════════════════════════╝\n");
    }

    private static void printBanner() {
        System.out.println("╔═════════════════════════════════════════════════════════════════╗");
        System.out.println("║       HOTEL BOOKING MANAGEMENT SYSTEM                           ║");
        System.out.println("║       App-Based Learning · Core Java & Data Structures          ║");
        System.out.println("╠═════════════════════════════════════════════════════════════════╣");
        System.out.println("║  UC1  ArrayList        Guest Registry                           ║");
        System.out.println("║  UC2  LinkedList        Room Catalog                            ║");
        System.out.println("║  UC3  ArrayDeque(FIFO)  Booking Queue                           ║");
        System.out.println("║  UC4  PriorityQueue     VIP Waitlist Manager                    ║");
        System.out.println("║  UC5  HashMap           Inventory Tracker                       ║");
        System.out.println("║  UC6  HashMap + Set     Room Allocation & Double-Booking Guard  ║");
        System.out.println("║  UC7  TreeMap           Reservation Ledger (by date)            ║");
        System.out.println("║  UC8  Stack (LIFO)      Cancellation / Undo                     ║");
        System.out.println("╚═════════════════════════════════════════════════════════════════╝");
    }

    private static void printSummaryTable() {
        System.out.println("\n\n══════════════════════════════════════════════════════════════════");
        System.out.println("  DATA STRUCTURES SUMMARY");
        System.out.println("──────────────────────────────────────────────────────────────────");
        System.out.printf("  %-4s  %-22s  %-18s  %-20s%n",
                "UC", "Data Structure", "Key Operation", "Time Complexity");
        System.out.println("──────────────────────────────────────────────────────────────────");
        System.out.printf("  %-4s  %-22s  %-18s  %-20s%n", "1", "ArrayList<Guest>", "add / get(index)",
                "O(1) amortised");
        System.out.printf("  %-4s  %-22s  %-18s  %-20s%n", "2", "LinkedList<Room>", "addFirst/Last", "O(1)");
        System.out.printf("  %-4s  %-22s  %-18s  %-20s%n", "3", "ArrayDeque (FIFO Queue)", "offer / poll", "O(1)");
        System.out.printf("  %-4s  %-22s  %-18s  %-20s%n", "4", "PriorityQueue", "offer / poll", "O(log n)");
        System.out.printf("  %-4s  %-22s  %-18s  %-20s%n", "5", "HashMap<String,Integer>", "get / put", "O(1) avg");
        System.out.printf("  %-4s  %-22s  %-18s  %-20s%n", "6", "HashMap + Set<String>", "Set.add (unique)",
                "O(1) avg");
        System.out.printf("  %-4s  %-22s  %-18s  %-20s%n", "7", "TreeMap<LocalDate,…>", "subMap (range)", "O(log n)");
        System.out.printf("  %-4s  %-22s  %-18s  %-20s%n", "8", "ArrayDeque (LIFO Stack)", "push / pop", "O(1)");
        System.out.println("──────────────────────────────────────────────────────────────────");
    }
}

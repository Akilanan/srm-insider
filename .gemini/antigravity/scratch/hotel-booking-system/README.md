# Hotel Booking Management System
> **App-Based Learning · Core Java & Data Structures**

A fully-working, console-based hotel booking system built with **pure Core Java** and no external dependencies. Every feature is implemented using a _different_ Java data structure, making each use case a focused learning exercise.

---

## 📚 Learning Objectives

By studying this project you will understand:
- **When** to use each data structure (not just *how*)
- **Why** each structure was chosen over the alternatives
- How data structures enable real-world properties: fairness, uniqueness, ordering, and efficiency
- How OOP principles (encapsulation, immutability, Comparable) interact with collections

---

## 🗂 Use Cases & Data Structures

| # | Class | Data Structure | Hotel Domain Concept |
|---|-------|---------------|----------------------|
| 1 | `UC1_GuestRegistry` | `ArrayList<Guest>` | Dynamic guest list, linear search |
| 2 | `UC2_RoomCatalog` | `LinkedList<Room>` | O(1) head/tail insert, mid-list removal |
| 3 | `UC3_BookingQueue` | `ArrayDeque` (FIFO Queue) | Fair, first-come-first-served request handling |
| 4 | `UC4_WaitlistManager` | `PriorityQueue<BookingRequest>` | VIP guests served first (priority ordering) |
| 5 | `UC5_InventoryTracker` | `HashMap<String, Integer>` | O(1) real-time availability lookup |
| 6 | `UC6_RoomAllocation` | `HashMap<String, Set<String>>` | Atomic allocation, double-booking prevention |
| 7 | `UC7_ReservationLedger` | `TreeMap<LocalDate, Reservation>` | Chronological ledger, date-range queries |
| 8 | `UC8_CancellationStack` | `ArrayDeque` (LIFO Stack) | Undo / rollback last booking |

---

## 📁 Project Structure

```
hotel-booking-system/
├── src/
│   └── com/hotel/
│       ├── model/
│       │   ├── Guest.java
│       │   ├── Room.java
│       │   ├── BookingRequest.java
│       │   └── Reservation.java
│       ├── usecase/
│       │   ├── UC1_GuestRegistry.java
│       │   ├── UC2_RoomCatalog.java
│       │   ├── UC3_BookingQueue.java
│       │   ├── UC4_WaitlistManager.java
│       │   ├── UC5_InventoryTracker.java
│       │   ├── UC6_RoomAllocation.java
│       │   ├── UC7_ReservationLedger.java
│       │   └── UC8_CancellationStack.java
│       └── Main.java
├── compile_and_run.ps1
├── .gitignore
└── README.md
```

---

## ▶ How to Run

### Option A – PowerShell Script (Windows)
```powershell
cd C:\path\to\hotel-booking-system
.\compile_and_run.ps1
```

### Option B – Manual
```powershell
# Compile
$src = "src"
$out = "out"
New-Item -ItemType Directory -Force -Path $out | Out-Null
$files = (Get-ChildItem -Recurse -Filter "*.java" $src).FullName
javac -d $out $files

# Run (with assertions enabled)
java -ea -cp $out com.hotel.Main
```

---

## ✅ Expected Output

Running the system produces output for all 8 use cases, each ending with:
```
  ✓ PASS – [assertion summary]
```
Followed by a final data structures summary table and:
```
╔═════════════════════════════════════════════════════════╗
║            ✓  ALL USE CASES COMPLETE  ✓                ║
╚═════════════════════════════════════════════════════════╝
```

---

## 🧠 Key Design Decisions

### Double-Booking Prevention (UC6)
The `InventoryService.allocateRoom()` method is `synchronized` and performs three steps atomically:
1. **Guard** – Check availability count > 0
2. **Set.add()** – Insert room ID into `Set<String>` (returns `false` on duplicate → exception)
3. **Decrement** – Only after successful Set insertion

This ensures the inventory count and the allocated ID registry are **always in sync**.

### VIP Priority (UC4)
`BookingRequest` implements `Comparable<BookingRequest>` by delegating to `Guest.compareTo()`.  
`Guest.compareTo()` reverses the natural integer order (`other - this`) so that **higher VIP number = lower compareTo value** = polled first from Java's min-heap `PriorityQueue`.

### Why `ArrayDeque` over `java.util.Stack` (UC8)
`Stack` extends `Vector` (synchronized, legacy). The Java API explicitly recommends `Deque` implementations for LIFO stacks. `ArrayDeque.addFirst()` / `removeFirst()` are O(1) with no lock overhead.

---

## 🛠 Requirements

- **Java 11+** (uses `LocalDate`, `var`-compatible, standard collections)
- No external libraries or build tools required

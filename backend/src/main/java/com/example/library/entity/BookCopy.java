package com.example.library.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "book_copy")
public class BookCopy {

    @Id
    private UUID id;

    @Column(name = "book_id", nullable = false)
    private UUID bookId;

    @Column(name = "inventory_number", nullable = false, unique = true)
    private String inventoryNumber;

    @Column(nullable = false)
    private String status;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getBookId() { return bookId; }
    public void setBookId(UUID bookId) { this.bookId = bookId; }
    public String getInventoryNumber() { return inventoryNumber; }
    public void setInventoryNumber(String inventoryNumber) { this.inventoryNumber = inventoryNumber; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

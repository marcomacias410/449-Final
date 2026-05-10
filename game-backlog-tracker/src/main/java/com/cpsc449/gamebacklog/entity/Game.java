package com.cpsc449.gamebacklog.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Game entity. Each Game belongs to exactly one User (One-to-Many).
 * The user_id foreign key is the link.
 */
@Entity
@Table(name = "games", indexes = {
        @Index(name = "idx_games_user_id", columnList = "user_id")
})
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 50)
    private String platform;

    @Column(length = 50)
    private String genre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GameStatus status;

    @Column(name = "hours_played")
    private Double hoursPlayed;

    /** 1-10 user rating, optional. */
    private Integer rating;

    @Column(length = 1000)
    private String notes;

    @Column(name = "date_added", nullable = false, updatable = false)
    private LocalDateTime dateAdded;

    @Column(name = "date_updated")
    private LocalDateTime dateUpdated;

    /**
     * Many-Games-to-One-User. The owning side; user_id is the FK column.
     * EAGER would be wasteful here — we only need the ID for ownership checks,
     * which is already in the FK column.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_games_user"))
    private User user;

    @PrePersist
    void onCreate() {
        this.dateAdded = LocalDateTime.now();
        this.dateUpdated = this.dateAdded;
    }

    @PreUpdate
    void onUpdate() {
        this.dateUpdated = LocalDateTime.now();
    }

    public Game() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public GameStatus getStatus() { return status; }
    public void setStatus(GameStatus status) { this.status = status; }

    public Double getHoursPlayed() { return hoursPlayed; }
    public void setHoursPlayed(Double hoursPlayed) { this.hoursPlayed = hoursPlayed; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getDateAdded() { return dateAdded; }
    public void setDateAdded(LocalDateTime dateAdded) { this.dateAdded = dateAdded; }

    public LocalDateTime getDateUpdated() { return dateUpdated; }
    public void setDateUpdated(LocalDateTime dateUpdated) { this.dateUpdated = dateUpdated; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}

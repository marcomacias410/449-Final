package com.cpsc449.gamebacklog.dto;

import com.cpsc449.gamebacklog.entity.Game;
import com.cpsc449.gamebacklog.entity.GameStatus;

import java.time.LocalDateTime;

public class GameResponse {

    private Long id;
    private String title;
    private String platform;
    private String genre;
    private GameStatus status;
    private Double hoursPlayed;
    private Integer rating;
    private String notes;
    private LocalDateTime dateAdded;
    private LocalDateTime dateUpdated;
    private Long userId;

    public GameResponse() {}

    public static GameResponse from(Game g) {
        GameResponse r = new GameResponse();
        r.id = g.getId();
        r.title = g.getTitle();
        r.platform = g.getPlatform();
        r.genre = g.getGenre();
        r.status = g.getStatus();
        r.hoursPlayed = g.getHoursPlayed();
        r.rating = g.getRating();
        r.notes = g.getNotes();
        r.dateAdded = g.getDateAdded();
        r.dateUpdated = g.getDateUpdated();
        r.userId = g.getUser() != null ? g.getUser().getId() : null;
        return r;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getPlatform() { return platform; }
    public String getGenre() { return genre; }
    public GameStatus getStatus() { return status; }
    public Double getHoursPlayed() { return hoursPlayed; }
    public Integer getRating() { return rating; }
    public String getNotes() { return notes; }
    public LocalDateTime getDateAdded() { return dateAdded; }
    public LocalDateTime getDateUpdated() { return dateUpdated; }
    public Long getUserId() { return userId; }
}

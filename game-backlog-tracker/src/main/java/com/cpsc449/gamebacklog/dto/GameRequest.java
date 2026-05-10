package com.cpsc449.gamebacklog.dto;

import com.cpsc449.gamebacklog.entity.GameStatus;
import jakarta.validation.constraints.*;

public class GameRequest {

    @NotBlank(message = "title is required")
    @Size(max = 150, message = "title must be 150 characters or fewer")
    private String title;

    @Size(max = 50)
    private String platform;

    @Size(max = 50)
    private String genre;

    @NotNull(message = "status is required (BACKLOG, PLAYING, COMPLETED, DROPPED, ON_HOLD)")
    private GameStatus status;

    @PositiveOrZero(message = "hoursPlayed cannot be negative")
    private Double hoursPlayed;

    @Min(value = 1, message = "rating must be between 1 and 10")
    @Max(value = 10, message = "rating must be between 1 and 10")
    private Integer rating;

    @Size(max = 1000, message = "notes must be 1000 characters or fewer")
    private String notes;

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
}

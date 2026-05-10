package com.cpsc449.gamebacklog.controller;

import com.cpsc449.gamebacklog.dto.GameRequest;
import com.cpsc449.gamebacklog.dto.GameResponse;
import com.cpsc449.gamebacklog.security.AuthenticatedUser;
import com.cpsc449.gamebacklog.service.GameService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<GameResponse> create(@AuthenticationPrincipal AuthenticatedUser me,
                                               @Valid @RequestBody GameRequest req) {
        GameResponse created = gameService.create(me.userId(), req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** Returns ONLY the authenticated user's games. */
    @GetMapping
    public ResponseEntity<List<GameResponse>> findAll(@AuthenticationPrincipal AuthenticatedUser me) {
        return ResponseEntity.ok(gameService.findAllForUser(me.userId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameResponse> findOne(@AuthenticationPrincipal AuthenticatedUser me,
                                                @PathVariable Long id) {
        return ResponseEntity.ok(gameService.findOneForUser(me.userId(), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GameResponse> update(@AuthenticationPrincipal AuthenticatedUser me,
                                               @PathVariable Long id,
                                               @Valid @RequestBody GameRequest req) {
        return ResponseEntity.ok(gameService.update(me.userId(), id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal AuthenticatedUser me,
                                       @PathVariable Long id) {
        gameService.delete(me.userId(), id);
        return ResponseEntity.noContent().build();
    }
}

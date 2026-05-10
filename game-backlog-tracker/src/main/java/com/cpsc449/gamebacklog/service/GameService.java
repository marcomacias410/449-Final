package com.cpsc449.gamebacklog.service;

import com.cpsc449.gamebacklog.dto.GameRequest;
import com.cpsc449.gamebacklog.dto.GameResponse;
import com.cpsc449.gamebacklog.entity.Game;
import com.cpsc449.gamebacklog.entity.User;
import com.cpsc449.gamebacklog.exception.ForbiddenException;
import com.cpsc449.gamebacklog.exception.ResourceNotFoundException;
import com.cpsc449.gamebacklog.repository.GameRepository;
import com.cpsc449.gamebacklog.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public GameService(GameRepository gameRepository, UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public GameResponse create(Long userId, GameRequest req) {
        // user_id ALWAYS comes from the JWT — never the request body.
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user not found"));

        Game g = new Game();
        applyRequest(g, req);
        g.setUser(owner);

        Game saved = gameRepository.save(g);
        return GameResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<GameResponse> findAllForUser(Long userId) {
        return gameRepository.findAllByUserId(userId).stream()
                .map(GameResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public GameResponse findOneForUser(Long userId, Long gameId) {
        Game g = loadAndAuthorize(userId, gameId);
        return GameResponse.from(g);
    }

    @Transactional
    public GameResponse update(Long userId, Long gameId, GameRequest req) {
        Game g = loadAndAuthorize(userId, gameId);
        applyRequest(g, req);
        return GameResponse.from(gameRepository.save(g));
    }

    @Transactional
    public void delete(Long userId, Long gameId) {
        Game g = loadAndAuthorize(userId, gameId);
        gameRepository.delete(g);
    }

    /**
     * Loads the game by ID and confirms it belongs to the authenticated user.
     * 404 if it doesn't exist; 403 if it exists but belongs to someone else.
     * (Spec requires this distinction explicitly.)
     */
    private Game loadAndAuthorize(Long userId, Long gameId) {
        Game g = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Game with id " + gameId + " not found"));

        if (!g.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You do not have permission to access this game");
        }
        return g;
    }

    private void applyRequest(Game g, GameRequest req) {
        g.setTitle(req.getTitle().trim());
        g.setPlatform(req.getPlatform());
        g.setGenre(req.getGenre());
        g.setStatus(req.getStatus());
        g.setHoursPlayed(req.getHoursPlayed());
        g.setRating(req.getRating());
        g.setNotes(req.getNotes());
    }
}

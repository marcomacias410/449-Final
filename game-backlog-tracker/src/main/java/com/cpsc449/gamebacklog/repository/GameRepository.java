package com.cpsc449.gamebacklog.repository;

import com.cpsc449.gamebacklog.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    /** Used by GET /api/games — returns ONLY this user's games. */
    List<Game> findAllByUserId(Long userId);
}

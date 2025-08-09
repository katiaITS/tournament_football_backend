package com.tournament_football_backend.repository;

import com.tournament_football_backend.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByName(String name);
    boolean existsByName(String name);

    @Query("SELECT t FROM Team t WHERE t.name LIKE %:keyword%")
    List<Team> findByNameContaining(@Param("keyword") String keyword);

    @Query("SELECT t FROM Team t LEFT JOIN FETCH t.players WHERE t.id = :id")
    Optional<Team> findByIdWithPlayers(@Param("id") Long id);

    @Query("SELECT t FROM Team t JOIN t.players p WHERE p.id = :playerId")
    List<Team> findByPlayerId(@Param("playerId") Long playerId);
}
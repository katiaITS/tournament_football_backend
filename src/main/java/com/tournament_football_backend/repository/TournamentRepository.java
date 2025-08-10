package com.tournament_football_backend.repository;

import com.tournament_football_backend.model.Tournament;
import com.tournament_football_backend.model.TournamentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * TournamentRepository.java
 * This interface defines methods for accessing and manipulating Tournament entities in the database.
 * It extends JpaRepository to provide basic CRUD operations and custom query methods.
 */
@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    List<Tournament> findByStatus(TournamentStatus status);
    List<Tournament> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    List<Tournament> findByStartDateAfter(LocalDate date);
    List<Tournament> findByEndDateBefore(LocalDate date);

    @Query("SELECT t FROM Tournament t WHERE t.name LIKE %:keyword%")
    List<Tournament> findByNameContaining(@Param("keyword") String keyword);

    @Query("SELECT t FROM Tournament t LEFT JOIN FETCH t.participatingTeams WHERE t.id = :id")
    Optional<Tournament> findByIdWithTeams(@Param("id") Long id);

    @Query("SELECT t FROM Tournament t LEFT JOIN FETCH t.matches WHERE t.id = :id")
    Optional<Tournament> findByIdWithMatches(@Param("id") Long id);

    @Query("SELECT t FROM Tournament t JOIN t.participatingTeams pt WHERE pt.id = :teamId")
    List<Tournament> findByParticipatingTeamId(@Param("teamId") Long teamId);

    @Query("SELECT COUNT(pt) FROM Tournament t JOIN t.participatingTeams pt WHERE t.id = :tournamentId")
    int countTeamsByTournamentId(@Param("tournamentId") Long tournamentId);
}
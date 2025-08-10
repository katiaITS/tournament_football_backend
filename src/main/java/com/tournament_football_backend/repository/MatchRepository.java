package com.tournament_football_backend.repository;

import com.tournament_football_backend.model.Match;
import com.tournament_football_backend.model.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/* MatchRepository.java
 * This interface defines methods for accessing and manipulating Match entities in the database.
 * It extends JpaRepository to provide basic CRUD operations and custom query methods.
 */
@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByTournamentId(Long tournamentId);

    List<Match> findByStatus(MatchStatus status);

    @Query("SELECT m FROM Match m WHERE m.homeTeam.id = :teamId OR m.awayTeam.id = :teamId")
    List<Match> findByTeamId(@Param("teamId") Long teamId);

    @Query("SELECT m FROM Match m WHERE m.tournament.id = :tournamentId AND m.status = :status")
    List<Match> findByTournamentIdAndStatus(@Param("tournamentId") Long tournamentId, @Param("status") MatchStatus status);

    @Query("SELECT m FROM Match m WHERE m.matchDate BETWEEN :startDate AND :endDate")
    List<Match> findByMatchDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT m FROM Match m WHERE m.homeTeam.id = :homeTeamId AND m.awayTeam.id = :awayTeamId AND m.tournament.id = :tournamentId")
    List<Match> findSpecificMatch(@Param("homeTeamId") Long homeTeamId, @Param("awayTeamId") Long awayTeamId, @Param("tournamentId") Long tournamentId);

    @Query("SELECT COUNT(m) FROM Match m WHERE m.tournament.id = :tournamentId AND m.status = 'COMPLETED' AND " +
            "((m.homeTeam.id = :teamId AND m.homeGoals > m.awayGoals) OR " +
            "(m.awayTeam.id = :teamId AND m.awayGoals > m.homeGoals))")
    int countWinsByTeamInTournament(@Param("teamId") Long teamId, @Param("tournamentId") Long tournamentId);

    @Query("SELECT COUNT(m) FROM Match m WHERE m.tournament.id = :tournamentId AND m.status = 'COMPLETED' AND " +
            "((m.homeTeam.id = :teamId AND m.homeGoals < m.awayGoals) OR " +
            "(m.awayTeam.id = :teamId AND m.awayGoals < m.homeGoals))")
    int countLossesByTeamInTournament(@Param("teamId") Long teamId, @Param("tournamentId") Long tournamentId);
}
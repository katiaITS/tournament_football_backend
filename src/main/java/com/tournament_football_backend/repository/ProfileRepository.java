package com.tournament_football_backend.repository;

import com.tournament_football_backend.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/* * ProfileRepository is responsible for managing Profile entities in the database.
 * It extends JpaRepository to provide basic CRUD operations and custom query methods.
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserId(Long userId);
}
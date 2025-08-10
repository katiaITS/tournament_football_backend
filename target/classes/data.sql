-- Users with different roles and complete profiles
INSERT IGNORE INTO users (username, email, password, role) VALUES
('admin', 'admin@tournamentfootball.com', '$2a$10$9P5rY/LVvhSDE0k559TGhOZz0qXzC0rOha8Hi9IS69gac/dnRzMFG', 'ROLE_ADMIN'),
('mario_rossi', 'mario.rossi@email.com', '$2a$10$9P5rY/LVvhSDE0k559TGhOZz0qXzC0rOha8Hi9IS69gac/dnRzMFG', 'ROLE_USER'),
('luca_bianchi', 'luca.bianchi@email.com', '$2a$10$9P5rY/LVvhSDE0k559TGhOZz0qXzC0rOha8Hi9IS69gac/dnRzMFG', 'ROLE_USER'),
('giuseppe_verdi', 'giuseppe.verdi@email.com', '$2a$10$9P5rY/LVvhSDE0k559TGhOZz0qXzC0rOha8Hi9IS69gac/dnRzMFG', 'ROLE_USER'),
('francesco_neri', 'francesco.neri@email.com', '$2a$10$9P5rY/LVvhSDE0k559TGhOZz0qXzC0rOha8Hi9IS69gac/dnRzMFG', 'ROLE_USER'),
('antonio_blu', 'antonio.blu@email.com', '$2a$10$9P5rY/LVvhSDE0k559TGhOZz0qXzC0rOha8Hi9IS69gac/dnRzMFG', 'ROLE_USER'),
('marco_gialli', 'marco.gialli@email.com', '$2a$10$9P5rY/LVvhSDE0k559TGhOZz0qXzC0rOha8Hi9IS69gac/dnRzMFG', 'ROLE_USER'),
('sara_viola', 'sara.viola@email.com', '$2a$10$9P5rY/LVvhSDE0k559TGhOZz0qXzC0rOha8Hi9IS69gac/dnRzMFG', 'ROLE_USER'),
('alessio_verde', 'alessio.verde@email.com', '$2a$10$9P5rY/LVvhSDE0k559TGhOZz0qXzC0rOha8Hi9IS69gac/dnRzMFG', 'ROLE_USER'),
('chiara_rosa', 'chiara.rosa@email.com', '$2a$10$9P5rY/LVvhSDE0k559TGhOZz0qXzC0rOha8Hi9IS69gac/dnRzMFG', 'ROLE_USER'),
('davide_oro', 'davide.oro@email.com', '$2a$10$9P5rY/LVvhSDE0k559TGhOZz0qXzC0rOha8Hi9IS69gac/dnRzMFG', 'ROLE_USER'),
('federica_silver', 'federica.silver@email.com', '$2a$10$9P5rY/LVvhSDE0k559TGhOZz0qXzC0rOha8Hi9IS69gac/dnRzMFG', 'ROLE_USER'),
('matteo_bronze', 'matteo.bronze@email.com', '$2a$10$9P5rY/LVvhSDE0k559TGhOZz0qXzC0rOha8Hi9IS69gac/dnRzMFG', 'ROLE_ADMIN'),
('giulia_diamond', 'giulia.diamond@email.com', '$2a$10$9P5rY/LVvhSDE0k559TGhOZz0qXzC0rOha8Hi9IS69gac/dnRzMFG', 'ROLE_USER'),
('lorenzo_platinum', 'lorenzo.platinum@email.com', '$2a$10$9P5rY/LVvhSDE0k559TGhOZz0qXzC0rOha8Hi9IS69gac/dnRzMFG', 'ROLE_USER');

-- Detailed profiles for all users
INSERT IGNORE INTO profiles (user_id, first_name, last_name, birth_date, phone, city, bio) VALUES
(1, 'Admin', 'System', '1980-01-01', '123-456-789', 'Milan', 'Tournament management system administrator'),
(2, 'Mario', 'Rossi', '1990-05-15', '333-111-2222', 'Milan', 'Football player for 10 years, prefers playing as midfielder'),
(3, 'Luca', 'Bianchi', '1985-12-20', '333-222-3333', 'Rome', 'Expert goalkeeper with over 15 years of experience'),
(4, 'Giuseppe', 'Verdi', '1992-08-10', '333-333-4444', 'Naples', 'Fast and accurate striker, specialized in long-range shots'),
(5, 'Francesco', 'Neri', '1988-03-25', '333-444-5555', 'Turin', 'Central defender, team captain for 5 years'),
(6, 'Antonio', 'Blu', '1991-11-05', '333-555-6666', 'Bologna', 'Technical midfielder, skilled in passing and game building'),
(7, 'Marco', 'Gialli', '1987-07-18', '333-666-7777', 'Florence', 'Right winger, fast and accurate in crosses'),
(8, 'Sara', 'Viola', '1994-02-14', '333-777-8888', 'Venice', 'Versatile player, can play in all positions'),
(9, 'Alessio', 'Verde', '1989-09-30', '333-888-9999', 'Genoa', 'Defensive midfielder, specialist in tackles'),
(10, 'Chiara', 'Rosa', '1993-06-12', '333-999-0000', 'Palermo', 'Creative attacking midfielder, skilled in assists'),
(11, 'Davide', 'Oro', '1986-04-08', '333-000-1111', 'Bari', 'Expert libero, defensive leader of the team'),
(12, 'Federica', 'Silver', '1995-01-22', '333-111-0000', 'Catania', 'Agile and fast second striker'),
(13, 'Matteo', 'Bronze', '1984-10-03', '333-222-1111', 'Trieste', 'Coach and administrator, former professional player'),
(14, 'Giulia', 'Diamond', '1996-08-17', '333-333-2222', 'Verona', 'Young but promising goalkeeper'),
(15, 'Lorenzo', 'Platinum', '1990-12-05', '333-444-3333', 'Brescia', 'Experienced center forward, team top scorer');

-- Teams with detailed descriptions
INSERT IGNORE INTO teams (name) VALUES
('Milan Lions'),
('Rome Eagles'),
('Naples Tigers'),
('Turin Wolves'),
('Bologna Dragons'),
('Florence Falcons'),
('Venice Serpents'),
('Genoa Bears'),
('Palermo Panthers'),
('Bari Dolphins'),
('Catania Griffins'),
('Trieste Condors');

-- More realistic player-team associations
INSERT IGNORE INTO team_players (team_id, user_id) VALUES
-- Milan Lions (5 players)
(1, 2), (1, 3), (1, 6), (1, 9), (1, 12),
-- Rome Eagles (4 players)
(2, 4), (2, 7), (2, 10), (2, 14),
-- Naples Tigers (5 players)
(3, 5), (3, 8), (3, 11), (3, 15), (3, 2),
-- Turin Wolves (4 players)
(4, 6), (4, 9), (4, 3), (4, 13),
-- Bologna Dragons (3 players)
(5, 7), (5, 10), (5, 4),
-- Florence Falcons (4 players)
(6, 8), (6, 11), (6, 5), (6, 12),
-- Venice Serpents (3 players)
(7, 14), (7, 15), (7, 13),
-- Genoa Bears (3 players)
(8, 9), (8, 10), (8, 11),
-- Palermo Panthers (2 players)
(9, 12), (9, 8),
-- Bari Dolphins (2 players)
(10, 15), (10, 14),
-- Catania Griffins (2 players)
(11, 13), (11, 7),
-- Trieste Condors (2 players)
(12, 6), (12, 4);

-- Tournaments with complete details and realistic dates
INSERT IGNORE INTO tournaments (name, description, start_date, end_date, max_teams, status) VALUES
('Italian Football Cup 2025', 'National football tournament with best Italian teams. Prize pool: 5000€', '2025-03-01', '2025-03-31',  16, 'IN_PROGRESS'),
('Milan Spring Tournament', 'Local Milanese competition for amateur and semi-professional teams', '2025-04-15', '2025-05-15', 12, 'OPEN'),
('Southern Football Champions', 'Tournament for the best teams from southern Italy, knockout format', '2025-06-01', '2025-06-30',  16, 'OPEN'),
('Giuseppe Meazza Memorial', 'Memorial tournament dedicated to the great champion, with historic teams', '2025-08-01', '2025-08-15',  10, 'OPEN'),
('Regional Trophy', 'Inter-regional competition with representatives from various cities', '2025-09-01', '2025-09-30',  20, 'OPEN'),
('Autumn Cup', 'Autumn tournament for higher category teams', '2025-10-15', '2025-11-15',  8, 'COMPLETED'),
('Winter Cup', 'Winter indoor competition, reduced field 3vs3', '2025-12-01', '2025-12-20',  4, 'OPEN');

-- Team registrations to tournaments
INSERT IGNORE INTO tournament_teams (tournament_id, team_id) VALUES
-- Italian Cup (8 teams)
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8),
-- Milan Tournament (6 teams)
(2, 1), (2, 4), (2, 6), (2, 9), (2, 10), (2, 12),
-- Southern Champions (6 teams)
(3, 2), (3, 3), (3, 9), (3, 10), (3, 11), (3, 8),
-- Meazza Memorial (8 teams)
(4, 1), (4, 2), (4, 5), (4, 6), (4, 7), (4, 8), (4, 11), (4, 12),
-- Regional Trophy (10 teams)
(5, 1), (5, 2), (5, 3), (5, 4), (5, 5), (5, 6), (5, 7), (5, 8), (5, 9), (5, 10),
-- Autumn Cup (6 teams)
(6, 2), (6, 4), (6, 6), (6, 8), (6, 10), (6, 12),
-- Winter Cup (4 teams)
(7, 1), (7, 3), (7, 5), (7, 7);

-- Matches with varied results and realistic dates
INSERT IGNORE INTO matches (home_team_id, away_team_id, tournament_id, match_date, home_goals, away_goals, status) VALUES
-- Italian Cup - Quarter finals (completed)
(1, 2, 1, '2025-03-05 15:00:00', 2, 1, 'COMPLETED'),
(3, 4, 1, '2025-03-05 16:30:00', 0, 3, 'COMPLETED'),
(5, 6, 1, '2025-03-06 15:00:00', 1, 1, 'COMPLETED'),
(7, 8, 1, '2025-03-06 16:30:00', 3, 2, 'COMPLETED'),
-- Italian Cup - Semi finals (scheduled)
(1, 4, 1, '2025-03-20 15:00:00', 0, 0, 'SCHEDULED'),
(6, 7, 1, '2025-03-20 17:00:00', 0, 0, 'SCHEDULED'),
-- Milan Tournament - Group phase
(1, 4, 2, '2025-04-20 14:00:00', 0, 0, 'SCHEDULED'),
(6, 9, 2, '2025-04-20 15:30:00', 0, 0, 'SCHEDULED'),
(10, 12, 2, '2025-04-21 14:00:00', 0, 0, 'SCHEDULED'),
-- Southern Champions - Qualifying matches
(2, 3, 3, '2025-06-05 18:00:00', 0, 0, 'SCHEDULED'),
(9, 10, 3, '2025-06-05 19:30:00', 0, 0, 'SCHEDULED'),
(11, 8, 3, '2025-06-06 18:00:00', 0, 0, 'SCHEDULED'),
-- Meazza Memorial - First round
(1, 5, 4, '2025-08-03 19:00:00', 0, 0, 'SCHEDULED'),
(2, 6, 4, '2025-08-03 20:30:00', 0, 0, 'SCHEDULED'),
(7, 8, 4, '2025-08-04 19:00:00', 0, 0, 'SCHEDULED'),
(11, 12, 4, '2025-08-04 20:30:00', 0, 0, 'SCHEDULED'),
-- Regional Trophy - Elimination groups
(1, 3, 5, '2025-09-07 16:00:00', 0, 0, 'SCHEDULED'),
(2, 4, 5, '2025-09-07 17:30:00', 0, 0, 'SCHEDULED'),
(5, 7, 5, '2025-09-08 16:00:00', 0, 0, 'SCHEDULED'),
(6, 8, 5, '2025-09-08 17:30:00', 0, 0, 'SCHEDULED'),
(9, 10, 5, '2025-09-09 16:00:00', 0, 0, 'SCHEDULED'),
-- Autumn Cup - Preliminary matches
(2, 4, 6, '2025-10-20 15:00:00', 0, 0, 'SCHEDULED'),
(6, 8, 6, '2025-10-20 16:30:00', 0, 0, 'SCHEDULED'),
(10, 12, 6, '2025-10-21 15:00:00', 0, 0, 'SCHEDULED'),
-- Winter Cup - 3vs3 format
(1, 3, 7, '2025-12-05 18:00:00', 0, 0, 'SCHEDULED'),
(5, 7, 7, '2025-12-05 18:30:00', 0, 0, 'SCHEDULED'),
(9, 11, 7, '2025-12-05 19:00:00', 0, 0, 'SCHEDULED'),
(6, 4, 7, '2025-12-05 19:30:00', 0, 0, 'SCHEDULED');
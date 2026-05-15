-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 15, 2026 at 10:28 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `parkease`
--

-- --------------------------------------------------------

--
-- Table structure for table `inquiries`
--

CREATE TABLE `inquiries` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `message` text NOT NULL,
  `submitted_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `inquiries`
--

INSERT INTO `inquiries` (`id`, `name`, `email`, `message`, `submitted_at`) VALUES
(1, 'Ajit Adhikari', 'ajitad1019@gmail.com', 'iloveyou', '2026-05-14 12:10:39'),
(2, 'Ajit Adhikari', 'ajitad1019@gmail.com', 'hhgvgyujvihk', '2026-05-14 17:01:13'),
(3, 'prayus', 'prayush@gmail.com', 'gayyyy', '2026-05-15 08:20:08');

-- --------------------------------------------------------

--
-- Table structure for table `parking_sessions`
--

CREATE TABLE `parking_sessions` (
  `session_id` int(11) NOT NULL,
  `vehicle_id` int(11) NOT NULL,
  `slot_id` int(11) NOT NULL,
  `entry_time` timestamp NOT NULL DEFAULT current_timestamp(),
  `exit_time` timestamp NULL DEFAULT NULL,
  `total_hours` decimal(5,2) DEFAULT NULL,
  `total_charges` decimal(10,2) DEFAULT NULL,
  `payment_status` enum('pending','paid','overdue') NOT NULL DEFAULT 'pending',
  `status` enum('active','completed','cancelled') NOT NULL DEFAULT 'active',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ;

--
-- Dumping data for table `parking_sessions`
--

INSERT INTO `parking_sessions` (`session_id`, `vehicle_id`, `slot_id`, `entry_time`, `exit_time`, `total_hours`, `total_charges`, `payment_status`, `status`, `created_at`) VALUES
(2, 1, 19, '2026-05-15 07:31:19', '2026-05-15 07:32:40', 1.00, 5.00, 'paid', 'completed', '2026-05-15 07:31:19');

-- --------------------------------------------------------

--
-- Table structure for table `parking_slots`
--

CREATE TABLE `parking_slots` (
  `slot_id` int(11) NOT NULL,
  `zone_id` int(11) NOT NULL,
  `slot_number` varchar(20) NOT NULL,
  `vehicle_type` enum('CAR','BIKE','TRUCK') NOT NULL,
  `hourly_rate` decimal(10,2) NOT NULL,
  `status` enum('available','occupied','maintenance','reserved') NOT NULL DEFAULT 'available',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ;

--
-- Dumping data for table `parking_slots`
--

INSERT INTO `parking_slots` (`slot_id`, `zone_id`, `slot_number`, `vehicle_type`, `hourly_rate`, `status`, `created_at`, `updated_at`) VALUES
(19, 3, 'A-102', '', 70.00, 'available', '2026-05-15 04:50:31', '2026-05-15 07:32:40');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `phone` varchar(15) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password_hash` varchar(64) NOT NULL,
  `salt` varchar(64) NOT NULL,
  `role` enum('admin','user') NOT NULL DEFAULT 'user',
  `status` enum('pending','approved','rejected','suspended') NOT NULL DEFAULT 'pending',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `name`, `phone`, `email`, `password_hash`, `salt`, `role`, `status`, `created_at`, `updated_at`) VALUES
(1, 'Ajit Adhikari', '9824135234', 'np01cp4s230169@islingtoncollege.edu.np', 'NIMuLvPwd/I8qqGFfmra0MInj0RZ/GlrgP2YOxa+Fz4=', 'Zb/qADXTDTp1WuLmoStWVQ==', 'user', 'approved', '2026-05-10 03:49:42', '2026-05-14 12:51:43'),
(2, 'System Admin', '1234567890', 'admin@parkease.com', 'mBu3YQph12MkzNjUQVobcqKAPoOAYL9P1TvK5Hn+ED8=', 'VdD8LRRVQkzCo/QQOPBdlw==', 'admin', 'approved', '2026-05-10 03:51:05', '2026-05-14 13:02:24'),
(3, 'manish mandal', '2345678912', 'Manish@gmail.com', 'AXp3KUrp393bN785BG8TRa56XzRsFUQgY3AdFQiMpSk=', 'RFStnSyaOPrhbzNbyAmTdg==', 'user', 'approved', '2026-05-10 04:13:19', '2026-05-14 12:51:15');

-- --------------------------------------------------------

--
-- Table structure for table `vehicles`
--

CREATE TABLE `vehicles` (
  `vehicle_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `registration_number` varchar(20) NOT NULL,
  `vehicle_type` enum('CAR','BIKE','TRUCK') NOT NULL,
  `make` varchar(50) DEFAULT NULL,
  `model` varchar(50) DEFAULT NULL,
  `color` varchar(30) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `vehicles`
--

INSERT INTO `vehicles` (`vehicle_id`, `user_id`, `registration_number`, `vehicle_type`, `make`, `model`, `color`, `created_at`) VALUES
(1, 3, '98452', '', 'KTm', '2023', 'blue', '2026-05-15 04:15:11'),
(2, 3, '5037', '', 'yamaha', '2023', 'blue', '2026-05-15 08:25:56');

-- --------------------------------------------------------

--
-- Table structure for table `zones`
--

CREATE TABLE `zones` (
  `zone_id` int(11) NOT NULL,
  `zone_name` varchar(50) NOT NULL,
  `capacity` int(11) NOT NULL DEFAULT 0,
  `description` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ;

--
-- Dumping data for table `zones`
--

INSERT INTO `zones` (`zone_id`, `zone_name`, `capacity`, `description`, `created_at`) VALUES
(3, 'Main Floor', 8, 'Building A', '2026-05-15 02:13:19');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `inquiries`
--
ALTER TABLE `inquiries`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `parking_sessions`
--
ALTER TABLE `parking_sessions`
  ADD PRIMARY KEY (`session_id`),
  ADD KEY `idx_parking_sessions_vehicle_id` (`vehicle_id`),
  ADD KEY `idx_parking_sessions_slot_id` (`slot_id`),
  ADD KEY `idx_parking_sessions_entry_time` (`entry_time`),
  ADD KEY `idx_parking_sessions_status` (`status`),
  ADD KEY `idx_parking_sessions_payment_status` (`payment_status`),
  ADD KEY `idx_parking_sessions_active` (`status`,`entry_time`);

--
-- Indexes for table `parking_slots`
--
ALTER TABLE `parking_slots`
  ADD PRIMARY KEY (`slot_id`),
  ADD UNIQUE KEY `uq_parking_slots_zone_slot` (`zone_id`,`slot_number`),
  ADD KEY `idx_parking_slots_zone_id` (`zone_id`),
  ADD KEY `idx_parking_slots_status` (`status`),
  ADD KEY `idx_parking_slots_vehicle_type` (`vehicle_type`),
  ADD KEY `idx_parking_slots_slot_number` (`slot_number`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `uq_users_phone` (`phone`),
  ADD UNIQUE KEY `uq_users_email` (`email`),
  ADD KEY `idx_users_role` (`role`),
  ADD KEY `idx_users_status` (`status`),
  ADD KEY `idx_users_email` (`email`),
  ADD KEY `idx_users_phone` (`phone`);

--
-- Indexes for table `vehicles`
--
ALTER TABLE `vehicles`
  ADD PRIMARY KEY (`vehicle_id`),
  ADD UNIQUE KEY `uq_vehicles_registration_number` (`registration_number`),
  ADD KEY `idx_vehicles_user_id` (`user_id`),
  ADD KEY `idx_vehicles_vehicle_type` (`vehicle_type`);

--
-- Indexes for table `zones`
--
ALTER TABLE `zones`
  ADD PRIMARY KEY (`zone_id`),
  ADD UNIQUE KEY `uq_zones_zone_name` (`zone_name`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `inquiries`
--
ALTER TABLE `inquiries`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `parking_sessions`
--
ALTER TABLE `parking_sessions`
  MODIFY `session_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `parking_slots`
--
ALTER TABLE `parking_slots`
  MODIFY `slot_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `vehicles`
--
ALTER TABLE `vehicles`
  MODIFY `vehicle_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `zones`
--
ALTER TABLE `zones`
  MODIFY `zone_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `parking_sessions`
--
ALTER TABLE `parking_sessions`
  ADD CONSTRAINT `fk_parking_sessions_slot` FOREIGN KEY (`slot_id`) REFERENCES `parking_slots` (`slot_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_parking_sessions_vehicle` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`vehicle_id`) ON UPDATE CASCADE;

--
-- Constraints for table `parking_slots`
--
ALTER TABLE `parking_slots`
  ADD CONSTRAINT `fk_parking_slots_zone` FOREIGN KEY (`zone_id`) REFERENCES `zones` (`zone_id`) ON UPDATE CASCADE;

--
-- Constraints for table `vehicles`
--
ALTER TABLE `vehicles`
  ADD CONSTRAINT `fk_vehicles_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

-- phpMyAdmin SQL Dump
-- version 4.9.5deb2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Jan 20, 2021 at 09:06 PM
-- Server version: 8.0.22-0ubuntu0.20.04.3
-- PHP Version: 7.4.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `ess`
--

-- --------------------------------------------------------

--
-- Table structure for table `deletedmessages`
--

CREATE TABLE `deletedmessages` (
  `msgID` bigint NOT NULL COMMENT 'primary key',
  `user` bigint NOT NULL COMMENT 'user id',
  `text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT 'text',
  `timeSent` bigint NOT NULL COMMENT 'time in ms sent'
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `dupemessages`
--

CREATE TABLE `dupemessages` (
  `id` bigint NOT NULL,
  `user` bigint NOT NULL,
  `hash` varchar(255) CHARACTER SET latin1 NOT NULL,
  `issued` bigint NOT NULL,
  `expires` bigint NOT NULL,
  `hasExpired` tinyint(1) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `infractions`
--

CREATE TABLE `infractions` (
  `id` int NOT NULL COMMENT 'primary key',
  `offender` bigint NOT NULL COMMENT 'userID of who is getting punished',
  `moderator` bigint NOT NULL COMMENT 'userID of the moderator who applied the action',
  `type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'infraction type',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT 'Reason given for this',
  `issued` bigint NOT NULL COMMENT 'Time in millis when this was given out',
  `expires` bigint DEFAULT NULL COMMENT 'when does this expire?',
  `hasExpired` tinyint(1) DEFAULT NULL COMMENT 'Has the alloted time been up? Used to stop checking'
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `deletedmessages`
--
ALTER TABLE `deletedmessages`
  ADD PRIMARY KEY (`msgID`);

--
-- Indexes for table `dupemessages`
--
ALTER TABLE `dupemessages`
  ADD PRIMARY KEY (`id`),
  ADD KEY `index_hash` (`hash`),
  ADD KEY `index_expires` (`expires`);

--
-- Indexes for table `infractions`
--
ALTER TABLE `infractions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `index_userID` (`offender`) USING BTREE,
  ADD KEY `index_expires` (`expires`) USING BTREE;

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `infractions`
--
ALTER TABLE `infractions`
  MODIFY `id` int NOT NULL AUTO_INCREMENT COMMENT 'primary key';
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

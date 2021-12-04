-- phpMyAdmin SQL Dump
-- version 4.6.6deb5ubuntu0.5
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Dec 04, 2021 at 05:55 AM
-- Server version: 10.1.48-MariaDB-0ubuntu0.18.04.1
-- PHP Version: 7.2.24-0ubuntu0.18.04.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Database: `dealbot`
--

-- --------------------------------------------------------

--
-- Table structure for table `achv_log`
--

CREATE TABLE `achv_log` (
                            `tgid` bigint(20) NOT NULL,
                            `achv` enum('GETTING_STARTED','FAMILIAR','ADDICTED','WINNER','ADEPTED','MASTER','WHAT_WAS_THIS_DEBT','WHERE_DID_IT_GO','NICE_DEAL_WITH_U','YOUR_PROPERTY_ISNT_YOURS','RENT_COLLECTOR','WELCOME_HOME','MANSION','HOTEL_MANAGER','SHOCK_BILL','THANK_YOU') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `bans`
--

CREATE TABLE `bans` (
                        `id` int(11) NOT NULL,
                        `tgid` bigint(20) DEFAULT NULL,
                        `until` int(11) NOT NULL,
                        `count` int(11) NOT NULL,
                        `type` enum('COMMAND','ADMIN') DEFAULT NULL,
                        `reason` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `games`
--

CREATE TABLE `games` (
                         `id` int(11) NOT NULL,
                         `gid` bigint(20) DEFAULT NULL,
                         `game_sequence` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `groups`
--

CREATE TABLE `groups` (
                          `gid` bigint(20) NOT NULL,
                          `wait_time` int(11) NOT NULL DEFAULT '120',
                          `turn_wait_time` int(11) DEFAULT '90',
                          `say_no_time` int(11) DEFAULT '15',
                          `pay_time` int(11) DEFAULT '30',
                          `current_game` int(11) DEFAULT NULL,
                          `lang` enum('en','zh','hk','en_us','en_gb') NOT NULL DEFAULT 'en',
                          `protest_mode` bit(1) DEFAULT b'1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `next_game`
--

CREATE TABLE `next_game` (
                             `tgid` bigint(20) NOT NULL,
                             `gid` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `tg_users`
--

CREATE TABLE `tg_users` (
                            `tgid` bigint(20) NOT NULL,
                            `username` varchar(32) DEFAULT NULL,
                            `game_minutes` float NOT NULL DEFAULT '0',
                            `game_count` int(11) NOT NULL DEFAULT '0',
                            `won_count` int(11) NOT NULL DEFAULT '0',
                            `cards_played` int(11) NOT NULL DEFAULT '0',
                            `currency_collected` int(11) NOT NULL DEFAULT '0',
                            `properties_collected` int(11) NOT NULL DEFAULT '0',
                            `rent_collected` int(11) NOT NULL DEFAULT '0',
                            `current_game` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `achv_log`
--
ALTER TABLE `achv_log`
    ADD PRIMARY KEY (`tgid`,`achv`);

--
-- Indexes for table `bans`
--
ALTER TABLE `bans`
    ADD PRIMARY KEY (`id`);

--
-- Indexes for table `games`
--
ALTER TABLE `games`
    ADD PRIMARY KEY (`id`),
    ADD KEY `FK_games_groups` (`gid`);

--
-- Indexes for table `groups`
--
ALTER TABLE `groups`
    ADD PRIMARY KEY (`gid`),
    ADD KEY `FK_groups_games` (`current_game`);

--
-- Indexes for table `next_game`
--
ALTER TABLE `next_game`
    ADD PRIMARY KEY (`tgid`,`gid`);

--
-- Indexes for table `tg_users`
--
ALTER TABLE `tg_users`
    ADD PRIMARY KEY (`tgid`),
    ADD KEY `FK_tg_users_groups` (`current_game`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `bans`
--
ALTER TABLE `bans`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1365;
--
-- AUTO_INCREMENT for table `games`
--
ALTER TABLE `games`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=64463;
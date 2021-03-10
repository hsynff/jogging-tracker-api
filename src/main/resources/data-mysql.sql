-- Rest of the script will fail if users already added (that means script executed already). Just ignore errors

-- Populate Users
INSERT INTO `user` (`id`,`email`,`failed_login_attempts`,`password`,`role`,`status`,`first_name`,`last_name`,`id_image_data`)
VALUES
(1,'admin@admin',0,'$2a$10$lEqTOK0rMv9S758bAJVcw.vDJH7an25ZbmhdvjGHHSAO/lkX5fXwy','ROLE_ADMIN','ACTIVE','John','Doe',NULL),
(2,'mgr@mgr',0,'$2a$10$lEqTOK0rMv9S758bAJVcw.vDJH7an25ZbmhdvjGHHSAO/lkX5fXwy','ROLE_MANAGER','ACTIVE','David','Gilmour',NULL),
(3,'usr@usr',0,'$2a$10$lEqTOK0rMv9S758bAJVcw.vDJH7an25ZbmhdvjGHHSAO/lkX5fXwy','ROLE_USER','ACTIVE','Hannah','Bless',NULL);


-- Populate Weather Conditions
INSERT INTO `weather_condition` (`id`,`summary`,`temperature_high`,`temperature_low`,`wind_speed`,`zone`)
VALUES
(1,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(2,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(3,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(4,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(5,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(6,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(7,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(8,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(9,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(10,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(11,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(12,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(13,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(14,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(15,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(16,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(17,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(18,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(19,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(20,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(21,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(22,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(23,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(24,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(25,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(26,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(27,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(28,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(29,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(30,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles'),
(31,'Partly cloudy throughout the day',53.36,44.5,9.27,'America/Los_Angeles');


-- Populate Records
INSERT INTO `record` (`id`,`date`,`distance`,`time`,`id_user`,`latitude`,`longitude`,`id_weather`)
VALUES
(1,'2021-01-01',1000,30,3,37.7749,-122.419,1),
(2,'2021-01-02',1200,30,3,37.7749,-122.419,2),
(3,'2021-01-03',1300,30,3,37.7749,-122.419,3),
(4,'2021-01-04',1400,30,3,37.7749,-122.419,4),
(5,'2021-01-05',1500,30,3,37.7749,-122.419,5),
(6,'2021-01-06',1600,40,3,37.7749,-122.419,6),
(7,'2021-01-07',1700,40,3,37.7749,-122.419,7),
(8,'2021-01-08',1800,40,3,37.7749,-122.419,8),
(9,'2021-01-09',1900,40,3,37.7749,-122.419,9),
(10,'2021-01-10',2000,40,3,37.7749,-122.419,10),
(11,'2021-01-11',2100,50,3,37.7749,-122.419,11),
(12,'2021-01-12',2200,50,3,37.7749,-122.419,12),
(13,'2021-01-13',2300,50,3,37.7749,-122.419,13),
(14,'2021-01-14',2400,50,3,37.7749,-122.419,14),
(15,'2021-01-15',2500,50,3,37.7749,-122.419,15),
(16,'2021-01-16',2600,60,3,37.7749,-122.419,16),
(17,'2021-01-17',2700,60,3,37.7749,-122.419,17),
(18,'2021-01-18',2800,60,3,37.7749,-122.419,18),
(19,'2021-01-19',2900,60,3,37.7749,-122.419,19),
(20,'2021-01-20',3000,60,3,37.7749,-122.419,20),
(21,'2021-01-21',3100,70,3,37.7749,-122.419,21),
(22,'2021-01-22',3200,70,3,37.7749,-122.419,22),
(23,'2021-01-23',3300,70,3,37.7749,-122.419,23),
(24,'2021-01-24',3400,70,3,37.7749,-122.419,24),
(25,'2021-01-25',3500,70,3,37.7749,-122.419,25),
(26,'2021-01-26',3600,80,3,37.7749,-122.419,26),
(27,'2021-01-27',3700,80,3,37.7749,-122.419,27),
(28,'2021-01-28',3800,80,3,37.7749,-122.419,28),
(29,'2021-01-29',3900,80,3,37.7749,-122.419,29),
(30,'2021-01-30',4000,80,3,37.7749,-122.419,30),
(31,'2021-01-31',4100,90,3,37.7749,-122.419,31);




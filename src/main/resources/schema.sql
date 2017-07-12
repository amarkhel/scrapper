CREATE TABLE `scala`.`games` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `gameId` INT NOT NULL,
  `playersSize` INT NOT NULL,
  `players` VARCHAR(512) NOT NULL,
  `location` VARCHAR(45) NOT NULL,
  `rounds` INT NOT NULL,
  `result` VARCHAR(45) NOT NULL,
  `tournamentResult` VARCHAR(45) NOT NULL,
  `year` INT NOT NULL,
  `month` INT NOT NULL,
  `day` INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `gameId_UNIQUE` (`gameId` ASC),
  INDEX `countPlayers` (`playersSize` ASC),
  FULLTEXT INDEX `players` (`players` ASC),
  INDEX `location` (`location` ASC),
  INDEX `rounds` (`rounds` ASC),
  INDEX `result` (`result` ASC),
  INDEX `tournResult` (`tournamentResult` ASC),
  INDEX `year` (`year` ASC),
  INDEX `month` (`month` ASC),
  INDEX `day` (`day` ASC));
  
  CREATE TABLE `scala`.`invalid` (
  `id` INT NOT NULL,
  `gameId` INT NOT NULL,
  `reason` VARCHAR(256) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `gameId_UNIQUE` (`gameId` ASC),
  INDEX `reason` (`reason` ASC));
  
ALTER TABLE `scala`.`invalid` 
CHANGE COLUMN `id` `id` INT(11) NOT NULL AUTO_INCREMENT ;
ALTER TABLE `scala`.`games` 
CHANGE COLUMN `players` `players` VARCHAR(1024) NOT NULL ;

ALTER TABLE `scala`.`invalid` 
ADD COLUMN `year` INT NULL AFTER `reason`;
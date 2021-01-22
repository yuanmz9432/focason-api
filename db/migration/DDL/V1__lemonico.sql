-- MySQL Script generated by MySQL Workbench
-- Mon Mar  2 12:03:39 2020
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

DROP database IF EXISTS `lemonico`;
CREATE DATABASE `lemonico`;
use `lemonico`;

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Table `lemonico`.`User`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lemonico`.`user` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `name` VARCHAR(45) NOT NULL COMMENT '用户名',
  `email` VARCHAR(100) NOT NULL COMMENT '邮箱账号',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `status` INT NOT NULL COMMENT '认证状态：0:未认证，1:已认证',
  `validate_code` VARCHAR(32) NOT NULL COMMENT '激活码',
  `register_time` DATETIME NOT NULL COMMENT '认证时效(截止时间)',
  `version_no` INT NOT NULL,
  `create_by` VARCHAR(30) NOT NULL,
  `create_time` DATETIME NOT NULL,
  `update_by` VARCHAR(30) NOT NULL,
  `update_time` DATETIME NOT NULL,
  `is_actived` INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `create_time_UNIQUE` (`create_time` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `lemonico`.`file`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lemonico`.`file_info` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `user_id` INT COMMENT '用户ID',
  `name` VARCHAR(200) NOT NULL COMMENT '文件名',
  `type` INT(1) NOT NULL COMMENT '文件类型',
  `download_code` VARCHAR(6) NOT NULL COMMENT '下载码',
  `size` VARCHAR(11) NOT NULL COMMENT '文件大小',
  `count` INT NOT NULL COMMENT '下载次数',
  `days` INT NOT NULL COMMENT '保存日数',
  `version_no` INT NOT NULL,
  `create_by` VARCHAR(30) NOT NULL,
  `create_time` DATETIME NOT NULL,
  `update_by` VARCHAR(30) NOT NULL,
  `update_time` DATETIME NOT NULL,
  `is_actived` INT NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -----------------------------------------------------
-- Table `lemonico`.`Address`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `lemonico`.`Address` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL COMMENT '收货人姓名',
  `zip` VARCHAR(7) NOT NULL COMMENT '邮编',
  `prefecture` INT NOT NULL COMMENT '省会',
  `city` INT NOT NULL COMMENT '城市',
  `address` VARCHAR(100) NOT NULL COMMENT '详细地址',
  `phone` VARCHAR(11) NOT NULL COMMENT '联系电话',
  `is_default` INT NOT NULL COMMENT '默认地址：0:否，1:是',
  `version_no` INT NOT NULL,
  `create_by` VARCHAR(30) NOT NULL,
  `create_time` DATETIME NOT NULL,
  `update_by` VARCHAR(30) NOT NULL,
  `update_time` DATETIME NOT NULL,
  `is_actived` INT NOT NULL,
  `User_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Address_User_idx` (`User_id` ASC),
  CONSTRAINT `fk_Address_User`
    FOREIGN KEY (`User_id`)
    REFERENCES `lemonico`.`User` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

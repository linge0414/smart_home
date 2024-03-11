/*
 Navicat MySQL Data Transfer

 Source Server         : huaweiyun
 Source Server Type    : MySQL
 Source Server Version : 50650
 Source Host           : 123.60.191.251:3306
 Source Schema         : smarthome

 Target Server Type    : MySQL
 Target Server Version : 50650
 File Encoding         : 65001

 Date: 18/06/2023 22:00:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for historical_data
-- ----------------------------
DROP TABLE IF EXISTS `historical_data`;
CREATE TABLE `historical_data`  (
  `id` char(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NOT NULL COMMENT 'id',
  `humidified` int(4) NULL DEFAULT NULL COMMENT '加湿开关',
  `temperature` int(255) NULL DEFAULT NULL COMMENT '温度',
  `humidity` int(255) NULL DEFAULT NULL COMMENT '湿度',
  `ambientBrightness` int(255) NULL DEFAULT NULL COMMENT '环境亮度',
  `vehACSwitch` int(255) NULL DEFAULT NULL COMMENT '空调开关',
  `lightLuminance` int(255) NULL DEFAULT NULL COMMENT '主灯亮度',
  `lightSwitch` int(255) NULL DEFAULT NULL COMMENT '主灯开关',
  `time` datetime NULL DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_german2_ci ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;

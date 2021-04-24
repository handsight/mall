/*
 Navicat Premium Data Transfer

 Source Server         : 172.30.3.107
 Source Server Type    : MySQL
 Source Server Version : 50722
 Source Host           : 172.30.3.107:3307
 Source Schema         : sharding_0

 Target Server Type    : MySQL
 Target Server Version : 50722
 File Encoding         : 65001

 Date: 24/04/2021 11:22:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for m_order
-- ----------------------------
DROP TABLE IF EXISTS `m_order`;
CREATE TABLE `m_order`  (
  `seckillId` int(11) NOT NULL,
  `userId` int(20) NULL DEFAULT NULL,
  `state` int(10) NULL DEFAULT NULL,
  `createTime` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`seckillId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of m_order
-- ----------------------------
INSERT INTO `m_order` VALUES (1, 9527, 1, '2021-04-24 10:40:13');

-- ----------------------------
-- Table structure for m_seckill_stock
-- ----------------------------
DROP TABLE IF EXISTS `m_seckill_stock`;
CREATE TABLE `m_seckill_stock`  (
  `seckillId` int(11) NOT NULL,
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `inventory` int(20) NULL DEFAULT NULL,
  `startTime` datetime(0) NULL DEFAULT NULL,
  `endTime` datetime(0) NULL DEFAULT NULL,
  `createTime` datetime(0) NULL DEFAULT NULL,
  `version` int(20) NULL DEFAULT NULL,
  PRIMARY KEY (`seckillId`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of m_seckill_stock
-- ----------------------------
INSERT INTO `m_seckill_stock` VALUES (1, '秒杀商品', 97, NULL, NULL, NULL, 1);

SET FOREIGN_KEY_CHECKS = 1;

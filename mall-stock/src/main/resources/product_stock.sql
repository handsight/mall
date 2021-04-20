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

 Date: 09/04/2021 16:41:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for product_stock
-- ----------------------------
DROP TABLE IF EXISTS `product_stock`;
CREATE TABLE `product_stock`  (
  `id` int(11) NOT NULL,
  `productId` int(20) NULL DEFAULT NULL,
  `stockSegment` int(20) NULL DEFAULT NULL,
  `stock01` int(20) NULL DEFAULT NULL,
  `stock02` int(20) NULL DEFAULT NULL,
  `stock03` int(20) NULL DEFAULT NULL,
  `stock04` int(20) NULL DEFAULT NULL,
  `stock05` int(20) NULL DEFAULT NULL,
  `stock06` int(20) NULL DEFAULT NULL,
  `stock07` int(20) NULL DEFAULT NULL,
  `stock08` int(20) NULL DEFAULT NULL,
  `stock09` int(20) NULL DEFAULT NULL,
  `stock10` int(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_stock
-- ----------------------------
INSERT INTO `product_stock` VALUES (1, 9527, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);

SET FOREIGN_KEY_CHECKS = 1;

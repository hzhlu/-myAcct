/*
Navicat SQLite Data Transfer

Source Server         : myAcct
Source Server Version : 30714
Source Host           : :0

Target Server Type    : SQLite
Target Server Version : 30714
File Encoding         : 65001

Date: 2018-08-17 08:44:29
*/

PRAGMA foreign_keys = OFF;

-- ----------------------------
-- Table structure for myAcct
-- ----------------------------
DROP TABLE IF EXISTS "main"."myAcct";
CREATE TABLE "myAcct" (
"id"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
"name"  TEXT,
"content"  TEXT,
"create_date"  TEXT,
"update_date"  TEXT,
"security_key"  TEXT,
"key_verify_code"  TEXT,
"mac"  TEXT
);

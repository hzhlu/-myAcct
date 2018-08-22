/*
Navicat SQLite Data Transfer

Source Server         : myAcct
Source Server Version : 30714
Source Host           : :0

Target Server Type    : SQLite
Target Server Version : 30714
File Encoding         : 65001

Date: 2018-08-22 11:04:26
*/

PRAGMA foreign_keys = OFF;

-- ----------------------------
-- Table structure for myAcct
-- ----------------------------
DROP TABLE IF EXISTS "main"."myAcct";
CREATE TABLE "myAcct" (
"id"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
"pid"  INTEGER DEFAULT 0,
"name"  TEXT,
"content"  TEXT,
"create_date"  TEXT,
"update_date"  TEXT,
"salt"  TEXT,
"key_verify_code"  TEXT,
"mac"  TEXT,
"draworder"  REAL DEFAULT 0,
CONSTRAINT "ak_name" UNIQUE ("name" COLLATE RTRIM ASC)
);

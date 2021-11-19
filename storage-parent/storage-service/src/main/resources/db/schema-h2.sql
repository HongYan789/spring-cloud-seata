DROP TABLE IF EXISTS storage;

CREATE TABLE storage
(
	id INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
	product_code VARCHAR(30) NULL DEFAULT NULL COMMENT '商品编码',
	count INT(11) NULL DEFAULT NULL COMMENT '数量',
	PRIMARY KEY (id)
);


DROP TABLE IF EXISTS undo_log;

CREATE TABLE `undo_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(100) NOT NULL,
  `context` varchar(128) NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int(11) NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  `ext` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ;
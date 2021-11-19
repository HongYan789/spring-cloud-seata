DROP TABLE IF EXISTS account;

CREATE TABLE account
(
	id INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
	user_id VARCHAR(30) NULL DEFAULT NULL COMMENT '用户id',
	money DECIMAL(10,2) NULL DEFAULT 0 COMMENT '账户余额',
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
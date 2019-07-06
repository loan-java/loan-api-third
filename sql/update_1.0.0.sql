
-- 修改订单号长度
alter table tb_order change column order_no order_no varchar(40) not null comment '订单号';

-- 订单表添加来源
alter table tb_order add column source int default 0 comment '订单来源：0-聚合，1-融泽';

-- 添加唯一索引
create unique index uk_order_source on tb_order(order_no,source);


CREATE TABLE `tb_user_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(64) DEFAULT NULL COMMENT '订单编号',
  `uid` int(20) DEFAULT NULL COMMENT '用户id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `source` int(4) DEFAULT NULL COMMENT '备用， 1-聚合，2-融泽',
  PRIMARY KEY (`id`),
  UNIQUE KEY `pk_uid_order_source` (`uid`,`order_no`,`source`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 COMMENT='融泽用户和订单关联表';

CREATE TABLE `tb_user_sms` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mobile` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


alter table tb_decision_res_detail add column order_no  varchar(30) default null comment '订单编号' after order_id;

create unique index order_no on tb_decision_res_detail(order_no);



#0613新增新分控表结构
drop table tb_decision_pb_detail;
CREATE TABLE `tb_decision_pb_detail` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `order_id` bigint(11) DEFAULT NULL COMMENT '订单id',
  `order_no` varchar(30) DEFAULT NULL COMMENT '(融泽)订单编号',
  `loan_no` varchar(64) DEFAULT NULL COMMENT '风控订单号',
  `loan_money` bigint(20) DEFAULT NULL COMMENT '放款金额',
	`loan_rate`   varchar(32) DEFAULT NULL COMMENT '放款利率',
	`loan_number` tinyint(4) DEFAULT NULL COMMENT '放款期数',
	`loan_unit` varchar(8) DEFAULT NULL COMMENT '放款单位',
  `pb_code` varchar(32) DEFAULT NULL COMMENT '风控状态标识',
	`result` varchar(32) DEFAULT NULL COMMENT '审核结果',
	`msg` varchar(32) DEFAULT NULL COMMENT '风控状态描述',
  `pb_desc` varchar(32) DEFAULT NULL COMMENT '风控状态描述',
  `score` varchar(32) DEFAULT NULL COMMENT '分数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `loanNo` (`loan_no`),
  UNIQUE KEY `orderNo` (`order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=282 DEFAULT CHARSET=utf8;


#20190620新增sql
ALTER TABLE tb_merchant DROP risk_type;
alter table tb_merchant add risk_type tinyint(4) DEFAULT 2 COMMENT '1-新颜，2-十露盘，3-指谜（默认2）';

drop table tb_decision_zm_detail;
CREATE TABLE `tb_decision_zm_detail` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `order_id` bigint(11) DEFAULT NULL COMMENT '订单id',
  `order_no` varchar(30) DEFAULT NULL COMMENT '(融泽)订单编号',
  `request_id` varchar(64) DEFAULT NULL COMMENT '风控订单号',
  `return_code` varchar(32) DEFAULT NULL COMMENT '风控状态标识',
	`return_info` varchar(32) DEFAULT NULL COMMENT '审核结果',
  `score` varchar(32) DEFAULT NULL COMMENT '分数',
  `history_apply` text DEFAULT NULL COMMENT '请求次数文本',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `requestId` (`request_id`),
  UNIQUE KEY `orderNo` (`order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=282 DEFAULT CHARSET=utf8;



#20190702新增字段
alter table tb_user_order add merchant_rate_id tinyint(6) DEFAULT 1 COMMENT '借款信息id';



#探针A
-- ----------------------------
-- Table structure for tb_type_filter
-- ----------------------------
DROP TABLE IF EXISTS `tb_type_filter`;
CREATE TABLE `tb_type_filter`  (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `uid` bigint(11) NULL DEFAULT NULL COMMENT '用户id',
  `order_no` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单编号-融泽',
  `type` int(255) NULL DEFAULT NULL COMMENT '1-新颜',
  `result` varchar(255) DEFAULT NULL COMMENT '系统处理结果',
  `resultl_str` varchar(2048) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '返回结果参数',
  `create_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no_type`(`order_no`, `type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;










-- 修改订单号长度
alter table tb_order change column order_no order_no varchar(40) not null comment '订单号';

-- 添加唯一索引
create unique index uk_order_no on tb_order(order_no,source);

-- 订单表添加来源
alter table tb_order add column source int default 0 comment '订单来源：0-聚合，1-融泽';

-- 融泽用户和订单关联表
CREATE TABLE `tb_user_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(64) DEFAULT NULL COMMENT '订单编号',
  `uid` int(20) DEFAULT NULL COMMENT '用户id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `source` int(4) DEFAULT NULL COMMENT '备用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='融泽用户和订单关联表';
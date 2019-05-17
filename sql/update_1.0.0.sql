
-- 修改订单号长度
alter table tb_order change column order_no order_no varchar(40) not null comment '订单号';

-- 添加唯一索引
create unique index uk_order_no on tb_order(order_no,source);

-- 订单表添加来源
alter table tb_order add column source int default 0 comment '订单来源：0-聚合，1-融泽';
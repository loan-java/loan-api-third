
-- 修改订单号长度
alter table tb_order change column order_no order_no varchar(32) not null comment '订单号';

-- 添加唯一索引
create unique index uk_order_no on tb_order(order_no);
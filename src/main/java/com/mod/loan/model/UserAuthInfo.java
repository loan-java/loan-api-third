package com.mod.loan.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "tb_user_auth_info")
public class UserAuthInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    private Long uid;

    /**
     * 订单编号
     */
    @Column(name = "order_no")
    private String orderNo;

    /**
     * 身份证正面编号
     */
    @Column(name = "id_positive")
    private String idPositive;

    /**
     * 身份证背面编号
     */
    @Column(name = "id_negative")
    private String idNegative;

    /**
     * 活体认证编号
     */
    @Column(name = "photo_assay")
    private String photoAssay;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取用户ID
     *
     * @return uid - 用户ID
     */
    public Long getUid() {
        return uid;
    }

    /**
     * 设置用户ID
     *
     * @param uid 用户ID
     */
    public void setUid(Long uid) {
        this.uid = uid;
    }

    /**
     * 获取订单编号
     *
     * @return order_no - 订单编号
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 设置订单编号
     *
     * @param orderNo 订单编号
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 获取身份证正面编号
     *
     * @return id_positive - 身份证正面编号
     */
    public String getIdPositive() {
        return idPositive;
    }

    /**
     * 设置身份证正面编号
     *
     * @param idPositive 身份证正面编号
     */
    public void setIdPositive(String idPositive) {
        this.idPositive = idPositive;
    }

    /**
     * 获取身份证背面编号
     *
     * @return id_negative - 身份证背面编号
     */
    public String getIdNegative() {
        return idNegative;
    }

    /**
     * 设置身份证背面编号
     *
     * @param idNegative 身份证背面编号
     */
    public void setIdNegative(String idNegative) {
        this.idNegative = idNegative;
    }

    /**
     * 获取活体认证编号
     *
     * @return photo_assay - 活体认证编号
     */
    public String getPhotoAssay() {
        return photoAssay;
    }

    /**
     * 设置活体认证编号
     *
     * @param photoAssay 活体认证编号
     */
    public void setPhotoAssay(String photoAssay) {
        this.photoAssay = photoAssay;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取修改时间
     *
     * @return update_time - 修改时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置修改时间
     *
     * @param updateTime 修改时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
package com.mod.loan.model;

import java.util.Date;
import javax.persistence.*;

@Table(name = "tb_merchant_origin")
public class MerchantOrigin {
	@Id
	private Long id;

	/**
	 * 所属商户
	 */
	private String merchant;

	/**
	 * 渠道别名
	 */
	@Column(name = "origin_name")
	private String originName;

	/**
	 * 渠道编号
	 */
	@Column(name = "origin_no")
	private String originNo;

	/**
	 * 0-停用;1-启用
	 */
	private Integer status;

	@Column(name = "create_time")
	private Date createTime;

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
	 * 获取所属商户
	 *
	 * @return merchant - 所属商户
	 */
	public String getMerchant() {
		return merchant;
	}

	/**
	 * 设置所属商户
	 *
	 * @param merchant
	 *            所属商户
	 */
	public void setMerchant(String merchant) {
		this.merchant = merchant == null ? null : merchant.trim();
	}

	/**
	 * 获取渠道别名
	 *
	 * @return origin_name - 渠道别名
	 */
	public String getOriginName() {
		return originName;
	}

	/**
	 * 设置渠道别名
	 *
	 * @param originName
	 *            渠道别名
	 */
	public void setOriginName(String originName) {
		this.originName = originName == null ? null : originName.trim();
	}

	/**
	 * 获取渠道编号
	 *
	 * @return origin_no - 渠道编号
	 */
	public String getOriginNo() {
		return originNo;
	}

	/**
	 * 设置渠道编号
	 *
	 * @param originNo
	 *            渠道编号
	 */
	public void setOriginNo(String originNo) {
		this.originNo = originNo == null ? null : originNo.trim();
	}

	/**
	 * 获取0-停用;1-启用
	 *
	 * @return status - 0-停用;1-启用
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * 设置0-停用;1-启用
	 *
	 * @param status
	 *            0-停用;1-启用
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * @return create_time
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
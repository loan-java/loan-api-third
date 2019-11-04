package com.mod.loan.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_app_version")
public class AppVersion {
    @Id
    private Long id;

    /**
     * app别名
     */
    @Column(name = "version_alias")
    private String versionAlias;

    /**
     * 类型，ios,android
     */
    @Column(name = "version_type")
    private String versionType;

    /**
     * 版本名称，1.0.0
     */
    @Column(name = "version_name")
    private String versionName;

    /**
     * 版本编号数字越大越新，1
     */
    @Column(name = "version_code")
    private Integer versionCode;

    /**
     * 强制更新，0-否，1-是
     */
    @Column(name = "version_force")
    private Integer versionForce;

    /**
     * 状态，0-停用，1-启用
     */
    @Column(name = "version_status")
    private Integer versionStatus;

    /**
     * 下载地址
     */
    @Column(name = "version_url")
    private String versionUrl;

    /**
     * 应用市场
     */
    @Column(name = "app_market")
    private String appMarket;
    /**
     * 版本更新内容
     */
    @Column(name = "version_content")
    private String versionContent;

    /**
     * 创建时间
     */
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
     * 获取app别名
     *
     * @return version_alias - app别名
     */
    public String getVersionAlias() {
        return versionAlias;
    }

    /**
     * 设置app别名
     *
     * @param versionAlias app别名
     */
    public void setVersionAlias(String versionAlias) {
        this.versionAlias = versionAlias == null ? null : versionAlias.trim();
    }

    /**
     * 获取类型，ios,android
     *
     * @return version_type - 类型，ios,android
     */
    public String getVersionType() {
        return versionType;
    }

    /**
     * 设置类型，ios,android
     *
     * @param versionType 类型，ios,android
     */
    public void setVersionType(String versionType) {
        this.versionType = versionType == null ? null : versionType.trim();
    }

    /**
     * 获取版本名称，1.0.0
     *
     * @return version_name - 版本名称，1.0.0
     */
    public String getVersionName() {
        return versionName;
    }

    /**
     * 设置版本名称，1.0.0
     *
     * @param versionName 版本名称，1.0.0
     */
    public void setVersionName(String versionName) {
        this.versionName = versionName == null ? null : versionName.trim();
    }

    /**
     * 获取版本编号数字越大越新，1
     *
     * @return version_code - 版本编号数字越大越新，1
     */
    public Integer getVersionCode() {
        return versionCode;
    }

    /**
     * 设置版本编号数字越大越新，1
     *
     * @param versionCode 版本编号数字越大越新，1
     */
    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }

    /**
     * 获取强制更新，0-否，1-是
     *
     * @return version_force - 强制更新，0-否，1-是
     */
    public Integer getVersionForce() {
        return versionForce;
    }

    /**
     * 设置强制更新，0-否，1-是
     *
     * @param versionForce 强制更新，0-否，1-是
     */
    public void setVersionForce(Integer versionForce) {
        this.versionForce = versionForce;
    }

    /**
     * 获取状态，0-停用，1-启用
     *
     * @return version_status - 状态，0-停用，1-启用
     */
    public Integer getVersionStatus() {
        return versionStatus;
    }

    /**
     * 设置状态，0-停用，1-启用
     *
     * @param versionStatus 状态，0-停用，1-启用
     */
    public void setVersionStatus(Integer versionStatus) {
        this.versionStatus = versionStatus;
    }

    /**
     * 获取下载地址
     *
     * @return version_url - 下载地址
     */
    public String getVersionUrl() {
        return versionUrl;
    }

    /**
     * 设置下载地址
     *
     * @param versionUrl 下载地址
     */
    public void setVersionUrl(String versionUrl) {
        this.versionUrl = versionUrl == null ? null : versionUrl.trim();
    }

    /**
     * 获取版本更新内容
     *
     * @return version_content - 版本更新内容
     */
    public String getVersionContent() {
        return versionContent;
    }

    /**
     * 设置版本更新内容
     *
     * @param versionContent 版本更新内容
     */
    public void setVersionContent(String versionContent) {
        this.versionContent = versionContent == null ? null : versionContent.trim();
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

	public String getAppMarket() {
		return appMarket;
	}

	public void setAppMarket(String appMarket) {
		this.appMarket = appMarket;
	}
    
    
}
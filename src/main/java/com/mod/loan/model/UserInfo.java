package com.mod.loan.model;


import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "tb_user_info")
public class UserInfo {
    @Id
    private Long uid;

    /**
     * 学历
     */
    private String education;

    /**
     * 居住省份
     */
    @Column(name = "live_province")
    private String liveProvince;

    /**
     * 居住城市
     */
    @Column(name = "live_city")
    private String liveCity;

    /**
     * 居住区
     */
    @Column(name = "live_district")
    private String liveDistrict;

    /**
     * 具体地址
     */
    @Column(name = "live_address")
    private String liveAddress;

    /**
     * 居住时长
     */
    @Column(name = "live_time")
    private String liveTime;

    /**
     * 婚姻状况
     */
    @Column(name = "live_marry")
    private String liveMarry;

    /**
     * 职业
     */
    @Column(name = "work_type")
    private String workType;

    /**
     * 公司
     */
    @Column(name = "work_company")
    private String workCompany;

    /**
     * 工作地址
     */
    @Column(name = "work_address")
    private String workAddress;

    /**
     * 直系联系人关系。 父子
     */
    @Column(name = "direct_contact")
    private String directContact;

    /**
     * 直系联系人姓名。李三
     */
    @Column(name = "direct_contact_name")
    private String directContactName;

    /**
     * 直系联系人电话。1263637
     */
    @Column(name = "direct_contact_phone")
    private String directContactPhone;


    /**
     * 其他联系人关系。 朋友
     */
    @Column(name = "others_contact")
    private String othersContact;

    /**
     * 其他联系人姓名。李三
     */
    @Column(name = "others_contact_name")
    private String othersContactName;

    /**
     * 其他联系人电话。1263637
     */
    @Column(name = "others_contact_phone")
    private String othersContactPhone;
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 工作单位地址-省
     */
    @Column(name = "work_company_province")
    private String workCompanyProvince;

    /**
     * 工作单位地址-市
     */
    @Column(name = "work_company_city")
    private String workCompanyCity;

    /**
     * 工作单位地址-区
     */
    @Column(name = "work_company_area")
    private String workCompanyArea;

    /**
     * 单位电话
     */
    @Column(name = "work_company_phone")
    private String workCompanyPhone;

    /**
     * 岗位
     */
    @Column(name = "job_title")
    private String jobTitle;

    /**
     * 月收入
     */
    @Column(name = "income_monthly_yuan")
    private BigDecimal incomeMonthlyYuan;

    /**
     * @return uid
     */
    public Long getUid() {
        return uid;
    }

    /**
     * @param uid
     */
    public void setUid(Long uid) {
        this.uid = uid;
    }

    /**
     * 获取学历
     *
     * @return education - 学历
     */
    public String getEducation() {
        return education;
    }

    /**
     * 设置学历
     *
     * @param education 学历
     */
    public void setEducation(String education) {
        this.education = education == null ? null : education.trim();
    }

    /**
     * 获取居住省份
     *
     * @return live_province - 居住省份
     */
    public String getLiveProvince() {
        return liveProvince;
    }

    /**
     * 设置居住省份
     *
     * @param liveProvince 居住省份
     */
    public void setLiveProvince(String liveProvince) {
        this.liveProvince = liveProvince == null ? null : liveProvince.trim();
    }

    /**
     * 获取居住城市
     *
     * @return live_city - 居住城市
     */
    public String getLiveCity() {
        return liveCity;
    }

    /**
     * 设置居住城市
     *
     * @param liveCity 居住城市
     */
    public void setLiveCity(String liveCity) {
        this.liveCity = liveCity == null ? null : liveCity.trim();
    }

    /**
     * 获取居住区
     *
     * @return live_district - 居住区
     */
    public String getLiveDistrict() {
        return liveDistrict;
    }

    /**
     * 设置居住区
     *
     * @param liveDistrict 居住区
     */
    public void setLiveDistrict(String liveDistrict) {
        this.liveDistrict = liveDistrict == null ? null : liveDistrict.trim();
    }

    /**
     * 获取具体地址
     *
     * @return live_address - 具体地址
     */
    public String getLiveAddress() {
        return liveAddress;
    }

    /**
     * 设置具体地址
     *
     * @param liveAddress 具体地址
     */
    public void setLiveAddress(String liveAddress) {
        this.liveAddress = liveAddress == null ? null : liveAddress.trim();
    }

    /**
     * 获取居住时长
     *
     * @return live_time - 居住时长
     */
    public String getLiveTime() {
        return liveTime;
    }

    /**
     * 设置居住时长
     *
     * @param liveTime 居住时长
     */
    public void setLiveTime(String liveTime) {
        this.liveTime = liveTime == null ? null : liveTime.trim();
    }

    /**
     * 获取婚姻状况
     *
     * @return live_marry - 婚姻状况
     */
    public String getLiveMarry() {
        return liveMarry;
    }

    /**
     * 设置婚姻状况
     *
     * @param liveMarry 婚姻状况
     */
    public void setLiveMarry(String liveMarry) {
        this.liveMarry = liveMarry == null ? null : liveMarry.trim();
    }

    /**
     * 获取职业
     *
     * @return work_type - 职业
     */
    public String getWorkType() {
        return workType;
    }

    /**
     * 设置职业
     *
     * @param workType 职业
     */
    public void setWorkType(String workType) {
        this.workType = workType == null ? null : workType.trim();
    }

    /**
     * 获取公司
     *
     * @return work_company - 公司
     */
    public String getWorkCompany() {
        return workCompany;
    }

    /**
     * 设置公司
     *
     * @param workCompany 公司
     */
    public void setWorkCompany(String workCompany) {
        this.workCompany = workCompany == null ? null : workCompany.trim();
    }

    /**
     * 获取工作地址
     *
     * @return work_address - 工作地址
     */
    public String getWorkAddress() {
        return workAddress;
    }

    /**
     * 设置工作地址
     *
     * @param workAddress 工作地址
     */
    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress == null ? null : workAddress.trim();
    }

    /**
     * 获取直系联系人,如： 父子|李三|1263637,12233
     *
     * @return direct_contact - 直系联系人,如： 父子|李三|1263637,12233
     */
    public String getDirectContact() {
        return directContact;
    }

    /**
     * 设置直系联系人,如： 父子|李三|1263637,12233
     *
     * @param directContact 直系联系人,如： 父子|李三|1263637,12233
     */
    public void setDirectContact(String directContact) {
        this.directContact = directContact == null ? null : directContact.trim();
    }

    /**
     * 获取其他联系人,如： 朋友|李三|1263637,12233
     *
     * @return others_contact - 其他联系人,如： 朋友|李三|1263637,12233
     */
    public String getOthersContact() {
        return othersContact;
    }

    /**
     * 设置其他联系人,如： 朋友|李三|1263637,12233
     *
     * @param othersContact 其他联系人,如： 朋友|李三|1263637,12233
     */
    public void setOthersContact(String othersContact) {
        this.othersContact = othersContact == null ? null : othersContact.trim();
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
     * @return update_time
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getDirectContactName() {
        return directContactName;
    }

    public void setDirectContactName(String directContactName) {
        this.directContactName = directContactName;
    }

    public String getDirectContactPhone() {
        return directContactPhone;
    }

    public void setDirectContactPhone(String directContactPhone) {
        this.directContactPhone = directContactPhone;
    }

    public String getOthersContactName() {
        return othersContactName;
    }

    public void setOthersContactName(String othersContactName) {
        this.othersContactName = othersContactName;
    }

    public String getOthersContactPhone() {
        return othersContactPhone;
    }

    public void setOthersContactPhone(String othersContactPhone) {
        this.othersContactPhone = othersContactPhone;
    }

    public String getWorkCompanyProvince() {
        return workCompanyProvince;
    }

    public void setWorkCompanyProvince(String workCompanyProvince) {
        this.workCompanyProvince = workCompanyProvince;
    }

    public String getWorkCompanyCity() {
        return workCompanyCity;
    }

    public void setWorkCompanyCity(String workCompanyCity) {
        this.workCompanyCity = workCompanyCity;
    }

    public String getWorkCompanyArea() {
        return workCompanyArea;
    }

    public void setWorkCompanyArea(String workCompanyArea) {
        this.workCompanyArea = workCompanyArea;
    }

    public String getWorkCompanyPhone() {
        return workCompanyPhone;
    }

    public void setWorkCompanyPhone(String workCompanyPhone) {
        this.workCompanyPhone = workCompanyPhone;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public BigDecimal getIncomeMonthlyYuan() {
        return incomeMonthlyYuan;
    }

    public void setIncomeMonthlyYuan(BigDecimal incomeMonthlyYuan) {
        this.incomeMonthlyYuan = incomeMonthlyYuan;
    }
}
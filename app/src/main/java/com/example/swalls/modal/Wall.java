package com.example.swalls.modal;

/**
 * <p>
 * 问题墙
 * </p>
 *
 * @author stylefeng123
 * @since 2018-10-29
 */

public class Wall {

    private static final long serialVersionUID = 1L;

    /**
     * 对象id
     */
    private Long id;
    /**
     * 微信用户id
     */
    private String openId;
    /**
     * 微信用户昵称 nickName
     */
    private String writerName;
    /**
     * 微信用户头像 picture
     */
    private String picture;
    /**
     * 摘要
     */
    private String abstracts;
    /**
     * 创建时间
     */
    private String writerTime;
    /**
     * 标签
     */
    private String label;
    /**
     * 内容
     */
    private String writeContests;
    /**
     * 父对象id
     */
    private Long parentObjectId;
    /**
     * 内容图片image
     */
    private String answerImage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getWriterName() {
        return writerName;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getAbstracts() {
        return abstracts;
    }

    public void setAbstracts(String abstracts) {
        this.abstracts = abstracts;
    }

    public String getWriterTime() {
        return writerTime;
    }

    public void setWriterTime(String writerTime) {
        this.writerTime = writerTime;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getWriteContests() {
        return writeContests;
    }

    public void setWriteContests(String writeContests) {
        this.writeContests = writeContests;
    }

    public Long getParentObjectId() {
        return parentObjectId;
    }

    public void setParentObjectId(Long parentObjectId) {
        this.parentObjectId = parentObjectId;
    }

    public String getAnswerImage() {
        return answerImage;
    }

    public void setAnswerImage(String answerImage) {
        this.answerImage = answerImage;
    }

    @Override
    public String toString() {
        return "Wall1{" +
                "id=" + id +
                ", openId=" + openId +
                ", writerName=" + writerName +
                ", picture=" + picture +
                ", abstracts=" + abstracts +
                ", writerTime=" + writerTime +
                ", label=" + label +
                ", writeContests=" + writeContests +
                ", parentObjectId=" + parentObjectId +
                ", answerImage=" + answerImage +
                "}";
    }
}


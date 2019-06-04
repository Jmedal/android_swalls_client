package com.example.swalls.modal;

/**
 * <p>
 * 社团活动公告
 * </p>
 *
 * @author stylefeng123
 * @since 2018-11-06
 */

public class College {

    private static final long serialVersionUID = 1L;

    /**
     * 标签
     */
    private Long id;
    /**
     * 社团活动主题
     */
    private String title;
    /**
     * 活动发布时间
     */
    private String time;
    /**
     * 所属社团
     */
    private String college;
    /**
     * 活动具体内容
     */
    private String contents;
    /**
     * 图片
     */
    private String images;
    /**
     * 发布者唯一标识
     */
    private String openId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    @Override
    public String toString() {
        return "College{" +
        "id=" + id +
        ", title=" + title +
        ", time=" + time +
        ", college=" + college +
        ", contents=" + contents +
        ", images=" + images +
        ", openId=" + openId +
        "}";
    }
}

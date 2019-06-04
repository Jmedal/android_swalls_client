package com.example.swalls.modal;

/**
 * <p>
 * 教务处公告
 * </p>
 *
 * @author stylefeng123
 * @since 2018-10-29
 */

public class Edu {

    private static final long serialVersionUID = 1L;

    /**
     * 标签
     */

    private Long id;
    /**
     * 教务处公告标题
     */
    private String eaTitle;
    /**
     * 教务处公告时间
     */
    private String eaTime;
    /**
     * 教务处公告内容
     */
    private String eaCont;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEaTitle() {
        return eaTitle;
    }

    public void setEaTitle(String eaTitle) {
        this.eaTitle = eaTitle;
    }

    public String getEaTime() {
        return eaTime;
    }

    public void setEaTime(String eaTime) {
        this.eaTime = eaTime;
    }

    public String getEaCont() {
        return eaCont;
    }

    public void setEaCont(String eaCont) {
        this.eaCont = eaCont;
    }

    @Override
    public String toString() {
        return "Edu{" +
        "id=" + id +
        ", eaTitle=" + eaTitle +
        ", eaTime=" + eaTime +
        ", eaCont=" + eaCont +
        "}";
    }
}

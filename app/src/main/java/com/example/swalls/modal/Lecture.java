package com.example.swalls.modal;


/**
 * <p>
 * 讲座信息
 * </p>
 *
 * @author stylefeng123
 * @since 2018-11-03
 */

public class Lecture {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;
    /**
     * 标题
     */
    private String leTitle;
    /**
     * 时间
     */
    private String leTime;
    /**
     * 内容
     */
    private String leCont;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLeTitle() {
        return leTitle;
    }

    public void setLeTitle(String leTitle) {
        this.leTitle = leTitle;
    }

    public String getLeTime() {
        return leTime;
    }

    public void setLeTime(String leTime) {
        this.leTime = leTime;
    }

    public String getLeCont() {
        return leCont;
    }

    public void setLeCont(String leCont) {
        this.leCont = leCont;
    }

    @Override
    public String toString() {
        return "Lecture{" +
        "id=" + id +
        ", leTitle=" + leTitle +
        ", leTime=" + leTime +
        ", leCont=" + leCont +
        "}";
    }
}

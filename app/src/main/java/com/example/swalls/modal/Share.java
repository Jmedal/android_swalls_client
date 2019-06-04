package com.example.swalls.modal;


/**
 * <p>
 * 文件共享表
 * </p>
 *
 * @author stylefeng123
 * @since 2018-11-27
 */
public class Share {

    private static final long serialVersionUID = 1L;

    /**
     * 标签
     */
    private Long id;
    /**
     * 文件标题
     */
    private String problemName;
    /**
     * 文件上传时间
     */
    private String problemTime;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件路径
     */
    private String filePath;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    public String getProblemTime() {
        return problemTime;
    }

    public void setProblemTime(String problemTime) {
        this.problemTime = problemTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "Share{" +
        "id=" + id +
        ", problemName=" + problemName +
        ", problemTime=" + problemTime +
        ", fileName=" + fileName +
        ", filePath=" + filePath +
        "}";
    }
}

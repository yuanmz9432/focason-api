package com.lemonico.common.bean;



import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Arrays;

/**
 * @className: MailBean
 * @description: Mail message
 * @date: 2020/02/03 09:24
 **/
@ApiModel(value = "メールメッセージ")
public class MailBean
{
    @ApiModelProperty(value = "メール受取人", required = true)
    private String[] recipient;
    @ApiModelProperty(value = "メールタイトル", required = true)
    private String subject;
    @ApiModelProperty(value = "メール内容", required = true)
    private String content;
    @ApiModelProperty(value = "附件地址")
    private String filePath;

    public String[] getRecipient() {
        return recipient;
    }

    public void setRecipient(String[] recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "MailBean{" +
            "recipient=" + Arrays.toString(recipient) +
            ", subject='" + subject + '\'' +
            ", content='" + content + '\'' +
            ", filePath='" + filePath + '\'' +
            '}';
    }
}

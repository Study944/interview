package com.zxc.interview.model.dto.file;

import java.io.Serializable;
import lombok.Data;

/**
 * 文件上传请求
 *
 * @    <a href="https://github.com/lizxc"> </a>
 * @  <a href="https://zxc.icu">  </a>
 */
@Data
public class UploadFileRequest implements Serializable {

    /**
     * 业务
     */
    private String biz;

    private static final long serialVersionUID = 1L;
}
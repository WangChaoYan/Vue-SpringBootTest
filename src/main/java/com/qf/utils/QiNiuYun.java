package com.qf.utils;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ResourceBundle;

/*

    七牛云
 */
@Component
public class QiNiuYun {
    String accessKey;   //访问密钥
    String secretKey;   //授权密钥
    String bucket;      //存储空间名称
    String path;        //外链域名
    public QiNiuYun() {
        ResourceBundle rb = ResourceBundle.getBundle("qiniu");
        accessKey = rb.getString("qiniu_accessKey");
        secretKey=rb.getString("qiniu_secretKey");
        bucket=rb.getString("qiniu_bucket");
        path=rb.getString("qiniu_url");
    }


    //七牛上传
    public String upload(MultipartFile multipartFile) {
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());
        System.out.println();
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = null;
        try {
            //byte[] uploadBytes = "hello qiniu cloud".getBytes("utf-8");
            byte[] bytes = multipartFile.getBytes();
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            try {
                System.out.println();
                System.out.println();
                System.out.println();
                Response response = uploadManager.put(bytes, key, upToken);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                if (putRet!=null){
                    return path+"/"+putRet.hash;
                }
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
                System.out.println();
                System.out.println();
                System.out.println();
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (Exception e){

        }
        return null;
    }
}

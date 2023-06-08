package com.work.controller;


import com.work.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

//文件的上传下载
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${work.path}")
    private String basePath;
    //文件上传
    @PostMapping("/upload")
    //MultipartFile file这个名字file必须与前端定义的name一致，即file
    public R<String> upload(MultipartFile file) throws IOException {
        //file是临时文件，必须转存到指定路径，否则本次请求完文件会删除
        log.info(file.toString());
        //原始文件名你
        String originalFilename = file.getOriginalFilename();
        //截取原始文件名的后缀格式
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用UUID重新生成文件名，防止文件名重复造成文件覆盖
        String fileName= UUID.randomUUID().toString()+suffix;

        //创建一个目录对象
        File dir=new File(basePath);
        //判断当前目录是否存在
        if(!dir.exists()){
            //目录不存在，创建
            dir.mkdirs();
        }
        //临时文件转存到指定路径
        file.transferTo(new File(basePath+fileName));
        return R.success(fileName);
    }

    //文件下载
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {
        //输入流读取文件内容
        FileInputStream fileInputStream=new FileInputStream(new File(basePath+name));
//        输出流把文件写回浏览器，在浏览器展示图片
        ServletOutputStream outputStream = response.getOutputStream();

        response.setContentType("image/jpeg");
        int len=0;
        byte[] bytes=new byte[1024];
        while ((len=fileInputStream.read(bytes))!=-1){
            outputStream.write(bytes,0,len);
            outputStream.flush();
        }
        //关闭资源
        outputStream.close();
        fileInputStream.close();

    }
}

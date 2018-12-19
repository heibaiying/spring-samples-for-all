package com.heibaiying.controller;


import com.heibaiying.utils.FileUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author : heibaiying
 * @description : 文件上传
 */

@Controller
public class FileController {

    @GetMapping("file")
    public String filePage() {
        return "file";
    }


    /***
     * 单文件上传
     */
    @PostMapping("upFile")
    public String upFile(MultipartFile file, HttpSession session) {
        FileUtil.saveFile(file, session.getServletContext().getRealPath("/image"));
        return "success";
    }

    /***
     * 多文件上传 多个文件用同一个名字
     */
    @PostMapping("upFiles")
    public String upFiles(@RequestParam(name = "file") MultipartFile[] files, HttpSession session) {
        for (MultipartFile file : files) {
            FileUtil.saveFile(file, session.getServletContext().getRealPath("images"));
        }
        return "success";
    }

    /***
     * 多文件上传方式2 分别为不同文件指定不同名字
     */
    @PostMapping("upFiles2")
    public String upFile(String extendParam,
                         @RequestParam(name = "file1") MultipartFile file1,
                         @RequestParam(name = "file2") MultipartFile file2, HttpSession session) {
        String realPath = session.getServletContext().getRealPath("images2");
        FileUtil.saveFile(file1, realPath);
        FileUtil.saveFile(file2, realPath);
        System.out.println("extendParam:" + extendParam);
        return "success";
    }


    @PostMapping("upFileForDownload")
    public String upFileForDownload(MultipartFile file, HttpSession session, Model model) throws UnsupportedEncodingException {
        String path = FileUtil.saveFile(file, session.getServletContext().getRealPath("/image"));
        model.addAttribute("filePath", URLEncoder.encode(path,"utf-8"));
        model.addAttribute("fileName", file.getOriginalFilename());
        return "fileDownload";
    }

    /***
     * 下载文件
     */
    @GetMapping("download")
    public ResponseEntity<byte[]> downloadFile(String filePath) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        File file = new File(filePath);
        // 解决文件名中文乱码
        String fileName=new String(file.getName().getBytes("UTF-8"),"iso-8859-1");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);

        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),
                headers, HttpStatus.CREATED);
    }

}

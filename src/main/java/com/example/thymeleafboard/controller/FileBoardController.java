package com.example.thymeleafboard.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.example.thymeleafboard.bean.FileBoardVO;
import com.example.thymeleafboard.bean.FileVO;
import com.example.thymeleafboard.service.FileBoardService;

@Controller
@RequestMapping("/fileBoard")
public class FileBoardController {

  @Autowired
  FileBoardService fBoardService;

  @RequestMapping("/list")
  private String readBoardList(Model model) {
    List<FileBoardVO> testList = new ArrayList<>();
    testList = fBoardService.readBoardList();
    model.addAttribute("testList", testList);
    return "/fileBoard/list";
  }

  @RequestMapping("/detail/{b_no}")
  private String boardDetail(@PathVariable("b_no") int b_no, Model model) {
    model.addAttribute("detail", fBoardService.boardDetail(b_no));

    if (fBoardService.fileDetail(b_no) == null) {
      return "fileBoard/detail";
    } else {
      model.addAttribute("file", fBoardService.fileDetail(b_no));
      return "fileBoard/detail";
    }
  }

  @RequestMapping("/insert")
  private String boardInsertForm(@ModelAttribute FileBoardVO board) {
    return "fileBoard/insert";
  }

  @RequestMapping("/insertProc")
  private String boardInsertProc(@ModelAttribute FileBoardVO board, @RequestPart MultipartFile files,
      HttpServletRequest request)
      throws IllegalStateException, IOException, Exception {

    if (files.isEmpty()) {
      fBoardService.boardInsert(board);
    } else {
      String fileName = files.getOriginalFilename();
      // 확장자
      String fileNameExtension = FilenameUtils.getExtension(fileName).toLowerCase();
      File destinationFile;
      String destinationFileName;
      String fileUrl = "E:\\WorkPlace\\thymeleaf-board\\public\\";

      do {
        destinationFileName = RandomStringUtils.randomAlphanumeric(32) + "." + fileNameExtension;
        destinationFile = new File(fileUrl + destinationFileName);
        System.out.println(destinationFile);
      } while (destinationFile.exists());

      destinationFile.getParentFile().mkdirs();
      files.transferTo(destinationFile);

      fBoardService.boardInsert(board);

      FileVO file = new FileVO();
      file.setB_no(board.getB_no());
      file.setFilename(destinationFileName);
      file.setFileoriginname(fileName);
      file.setFileurl(fileUrl);

      fBoardService.fileInsert(file);
    }
    return "forward:/fileBoard/list";
  }

  @RequestMapping("/update/{b_no}")
  private String boardUpdate(@PathVariable("b_no") int b_no, Model model) {
    model.addAttribute("detail", fBoardService.boardDetail(b_no));
    return "/fileBoard/update";
  }

  @RequestMapping("/updateProc")
  private String boardUpdateProc(@ModelAttribute FileBoardVO board) {
    fBoardService.boardUpdate(board);
    int bno = board.getB_no();
    String b_no = Integer.toString(bno);
    return "redirect:/fileBoard/detail/" + b_no;
  }

  @RequestMapping("/delete/{b_no}")
  private String boardDelete(@PathVariable("b_no") int b_no) {
    fBoardService.boardDelete(b_no);
    return "redirect:/fileBoard/list";
  }

  @RequestMapping("/fileDown/{b_no}")
  private void fileDown(@PathVariable("b_no") int b_no, HttpServletRequest request,
      HttpServletResponse response) throws UnsupportedEncodingException, Exception {

    request.setCharacterEncoding("UTF-8");
    FileVO fileVO = fBoardService.fileDetail(b_no);
    System.out.println(fileVO);
    // 파일 업로드 경로
    try {
      String fileUrl = fileVO.getFileurl();
      System.out.println(fileUrl);
      fileUrl += "/";
      String savePath = fileUrl;
      String fileName = fileVO.getFilename();

      // 실제 내보낼 파일명
      String originFileName = fileVO.getFileoriginname();
      InputStream in = null;
      OutputStream os = null;
      File file = null;
      Boolean skip = false;
      String client = "";

      // 파일을 읽어 스트림에 담기
      try {
        file = new File(savePath, fileName);
        in = new FileInputStream(file);
      } catch (FileNotFoundException fe) {
        skip = true;
      }

      client = request.getHeader("User-Agent");

      // 파일 다운로드 헤더 지정
      response.reset();
      response.setContentType("application/octet-stream");
      response.setHeader("Content-Description", "HTML Generated Data");
      if (!skip) {
        // IE
        if (client.indexOf("MSIE") != -1) {
          response.setHeader("Content-Disposition", "attachment; filename=\""
              + java.net.URLEncoder.encode(originFileName, "UTF-8").replaceAll("\\+", "\\ ") + "\"");
          // IE 11 이상
        } else if (client.indexOf("Trident") != -1) {
          response.setHeader("Content-Disposition", "attachment; filename=\""
              + java.net.URLEncoder.encode(originFileName, "UTF-8").replaceAll("\\+", "\\ ") + "\"");

          // 한글 파일명 처리
        } else {
          response.setHeader("Content-Disposition", "attachment; filename=\"" +
              new String(originFileName.getBytes("UTF-8"), "ISO8859_1") + "\"");
          response.setHeader("Content-Type", "application/octet-stream; charset=utf-8");
        }

        response.setHeader("Content-Length", "" + file.length());
        os = response.getOutputStream();
        byte b[] = new byte[(int) file.length()];
        int leng = 0;

        while ((leng = in.read(b)) > 0) {
          os.write(b, 0, leng);
        }
      } else {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<script> alert('파일을 찾을 수 없습니다.'); history.back(); </script>");
        out.flush();
      }
      if (in != null && os != null) {
        in.close();
        os.close();
      }

    } catch (Exception e) {
      System.out.println("ERROR : " + e.getStackTrace());
    }

  }
}
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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

  @Value("${resource.path}")
  private String resourcePath;

  @RequestMapping("/list")
  private String readBoardList(Model model) {

    List<Map<String, String>> testList = new ArrayList<>();
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
      throws Exception {

    fBoardService.boardInsert(board);
    if (!files.isEmpty()) {
      fileUpload(board, files);
    }
    return "forward:/fileBoard/list";
  }

  @RequestMapping("/update/{b_no}")
  private String boardUpdate(@PathVariable("b_no") int b_no, Model model) {
    model.addAttribute("detail", fBoardService.boardDetail(b_no));
    model.addAttribute("file", fBoardService.fileDetail(b_no));
    return "/fileBoard/update";
  }

  @RequestMapping("/updateProc")
  private String boardUpdateProc(@ModelAttribute FileBoardVO board, @RequestPart MultipartFile files)
      throws IllegalStateException, IOException {
    fBoardService.boardUpdate(board);
    if (!files.isEmpty())
      fileUpload(board, files);
    int bno = board.getB_no();
    String b_no = Integer.toString(bno);
    return "redirect:/fileBoard/detail/" + b_no;
  }

  @RequestMapping("/delete/{b_no}")
  private String boardDelete(@PathVariable("b_no") int b_no) {
    fBoardService.boardDelete(b_no);
    return "redirect:/fileBoard/list";
  }

  private void fileUpload(@ModelAttribute FileBoardVO board, @RequestPart MultipartFile files)
      throws IllegalStateException, IOException {
    String fileName = files.getOriginalFilename();
    // ?????????
    String fileNameExtension = FilenameUtils.getExtension(fileName).toLowerCase();
    File destinationFile;
    String destinationFileName;
    String fileUrl = resourcePath.split("file:///")[1];

    do {
      destinationFileName = RandomStringUtils.randomAlphanumeric(32) + "." + fileNameExtension;
      destinationFile = new File(fileUrl + destinationFileName);
      // ??????. ??????????????? ????????? ??????????????? ????????????????
    } while (destinationFile.exists());

    destinationFile.getParentFile().mkdirs();
    files.transferTo(destinationFile);

    FileVO file = new FileVO();
    file.setB_no(board.getB_no());
    file.setFilename(destinationFileName);
    file.setFileoriginname(fileName);
    file.setFileurl(fileUrl);

    fBoardService.fileInsert(file);
  }

  @RequestMapping("/fileDown/{b_no}")
  private void fileDown(@PathVariable("b_no") int b_no, HttpServletRequest request,
      HttpServletResponse response) throws UnsupportedEncodingException, Exception {

    request.setCharacterEncoding("UTF-8");
    FileVO fileVO = fBoardService.fileDetail(b_no);
    System.out.println(fileVO);
    // ?????? ????????? ??????
    try {
      String fileUrl = fileVO.getFileurl();
      fileUrl += "/";
      String savePath = fileUrl;
      String fileName = fileVO.getFilename();

      // ?????? ????????? ?????????
      String originFileName = fileVO.getFileoriginname();
      InputStream in = null;
      OutputStream os = null;
      File file = null;
      Boolean skip = false;
      String client = "";

      // ????????? ?????? ???????????? ??????
      try {
        file = new File(savePath, fileName);
        in = new FileInputStream(file);
      } catch (FileNotFoundException fe) {
        skip = true;
      }

      client = request.getHeader("User-Agent");

      // ?????? ???????????? ?????? ??????
      response.reset();
      response.setContentType("application/octet-stream");
      response.setHeader("Content-Description", "HTML Generated Data");
      if (!skip) {
        // IE
        if (client.indexOf("MSIE") != -1) {
          response.setHeader("Content-Disposition", "attachment; filename=\""
              + java.net.URLEncoder.encode(originFileName, "UTF-8").replaceAll("\\+", "\\ ") + "\"");
          // IE 11 ??????
        } else if (client.indexOf("Trident") != -1) {
          response.setHeader("Content-Disposition", "attachment; filename=\""
              + java.net.URLEncoder.encode(originFileName, "UTF-8").replaceAll("\\+", "\\ ") + "\"");

          // ?????? ????????? ??????
        } else {
          response.setHeader("Content-Disposition", "attachment; filename=\"" +
              new String(originFileName.getBytes("UTF-8"), "ISO8859_1") + "\"");
          response.setHeader("Content-Type", "application/octet-stream; charset=utf-8");
        }

        response.setHeader("Content-Length", "" + file.length());
        os = response.getOutputStream();
        byte b[] = new byte[(int) file.length()];
        int leng = 0;

        // ??????. os??? ???????????? b[] ???????????? ????????? ?????????????
        while ((leng = in.read(b)) > 0) {
          os.write(b, 0, leng);
        }
      } else {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<script> alert('????????? ?????? ??? ????????????.'); history.back(); </script>");
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

  @RequestMapping("/deleteFile/{b_no}")
  public String deleteFile(@PathVariable("b_no") int b_no) {
    FileVO oldFile = fBoardService.fileDetail(b_no);
    System.out.println(oldFile.toString());
    try {
      File newFile = new File(oldFile.getFileurl() + oldFile.getFilename());
      System.out.println(newFile.toString());
      if (newFile.exists()) {
        newFile.delete();
        fBoardService.deleteFile(b_no);
      }
    } catch (NullPointerException e) {
      System.out.println(e);
    }
    return "redirect:/fileBoard/update/" + b_no;
  }

}
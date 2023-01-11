package com.example.thymeleafboard.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.example.thymeleafboard.bean.FileBoardVO;
import com.example.thymeleafboard.bean.FileVO;
import com.example.thymeleafboard.service.FileBoardService;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/fileBoard")
public class FileBoardController {

  @Autowired
  FileBoardService fBoardService;

  @GetMapping("/list")
  private String readBoardList(Model model, HttpServletRequest request) {
    List<FileBoardVO> testList = new ArrayList<>();
    testList = fBoardService.readBoardList();
    model.addAttribute("testList", testList);
    return "/fileBoard/list";
  }

  @GetMapping("/detail/{b_no}")
  private String boardDetail(@PathVariable("b_no") int b_no, Model model) {
    model.addAttribute("detail", fBoardService.boardDetail(b_no));
    return "fileBoard/detail";
  }

  @PostMapping("/insert")
  private String boardInsertForm(@ModelAttribute FileBoardVO board) {
    return "fileBoard/insert";
  }

  @PostMapping("/insertProc")
  private String boardInsertProc(@ModelAttribute FileBoardVO board, @RequestPart MultipartFile files,
      HttpServletRequest request) throws IllegalStateException, IOException, Exception {

    if (files.isEmpty()) {
      fBoardService.boardInsert(board);
    } else {
      String fileName = files.getOriginalFilename();
      String fileNameExtension = FilenameUtils.getExtension(fileName).toLowerCase();
      File destinationFile;
      String destinationFileName;
      String fileUrl = "업로드한 파일을 저장할 절대경로를 넣어주세요";

      do {
        destinationFileName = RandomStringUtils.randomAlphanumeric(32) + "." + fileNameExtension;
        destinationFile = new File(fileUrl + destinationFileName);
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

  @PatchMapping("/update/{b_no}")
  private String boardUpdate(@PathVariable("b_no") int b_no, Model model) {
    model.addAttribute("detail", fBoardService.boardDetail(b_no));
    return "fileBoard/update";
  }

  @PatchMapping("/updateProc")
  private String boardUpdateProc(@ModelAttribute FileBoardVO board) {
    fBoardService.boardUpdate(board);
    int bno = board.getB_no();
    String b_no = Integer.toString(bno);
    return "redirect:/fileBoard/detail/" + b_no;
  }

  @DeleteMapping("/delete/{b_no}")
  private String boardDelete(@PathVariable("b_no") int b_no) {
    fBoardService.boardDelete(b_no);
    return "redirect:/fileBoard/list";
  }

}

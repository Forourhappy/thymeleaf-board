package com.example.thymeleafboard.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

import com.example.thymeleafboard.bean.FileBoardVO;
import com.example.thymeleafboard.service.FileBoardService;

@Controller
@RequestMapping("/fileBoard")
public class FileBoardController {

  @Autowired
  FileBoardService fBoardService;

  @GetMapping("/")
  private String readBoardList(Model model, HttpServletRequest request) {
    List<FileBoardVO> testList = new ArrayList<>();
    testList = fBoardService.readBoardList();
    model.addAttribute("testList", testList);
    return "posts";
  }

  @GetMapping("/{b_no}")
  private String boardDetail(@PathVariable("b_no") int b_no, Model model) {
    model.addAttribute("detail", fBoardService.boardDetail(b_no));
    return "posts/detail";
  }

  @PostMapping("/insert")
  private String boardInsertForm(@ModelAttribute FileBoardVO board) {
    return "posts";
  }

  @PostMapping("insertProc")
  private String boardInsertProc(@ModelAttribute FileBoardVO board, HttpServletRequest request) {
    fBoardService.boardInsert(board);
    return "forward:/fileBoard/list";
  }

  @PatchMapping("/{b_no}")
  private String boardUpdate(@PathVariable("b_no") int b_no, Model model) {
    model.addAttribute("detail", fBoardService.boardDetail(b_no));
    return "/update";
  }

  @PatchMapping("/updateProc")
  private String boardUpdateProc(@ModelAttribute FileBoardVO board) {
    fBoardService.boardUpdate(board);
    int bno = board.getB_no();
    String b_no = Integer.toString(bno);
    return "redirect:/detail/" + b_no;
  }

  @DeleteMapping("/{b_no}")
  private String boardDelete(@PathVariable("b_no") int b_no) {
    fBoardService.boardDelete(b_no);
    return "redirect:/list";
  }
}

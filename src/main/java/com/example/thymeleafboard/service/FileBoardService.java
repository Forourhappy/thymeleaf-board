package com.example.thymeleafboard.service;

import java.util.List;

import com.example.thymeleafboard.bean.FileBoardVO;
import com.example.thymeleafboard.bean.FileVO;

public interface FileBoardService {

  List<FileBoardVO> readBoardList();

  FileBoardVO boardDetail(int b_no);

  int boardInsert(FileBoardVO fileBoard);

  int boardUpdate(FileBoardVO fileBoard);

  int boardDelete(int b_no);

  int fileInsert(FileVO file);
}

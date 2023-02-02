package com.example.thymeleafboard.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.thymeleafboard.bean.FileBoardVO;
import com.example.thymeleafboard.bean.FileVO;

@Service
public interface FileBoardService {

  List<Map<String, String>> readBoardList();

  FileBoardVO boardDetail(int b_no);

  int boardInsert(FileBoardVO fileBoard);

  int boardUpdate(FileBoardVO fileBoard);

  int boardDelete(int b_no);

  int fileInsert(FileVO file);

  FileVO fileDetail(int b_no);

  void deleteFile(int b_no);

  List<FileVO> fileList();
}

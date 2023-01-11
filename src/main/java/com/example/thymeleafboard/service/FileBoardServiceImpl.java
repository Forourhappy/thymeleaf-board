package com.example.thymeleafboard.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.thymeleafboard.bean.FileBoardVO;
import com.example.thymeleafboard.bean.FileVO;
import com.example.thymeleafboard.mapper.FileBoardMapper;

@Service
public class FileBoardServiceImpl implements FileBoardService {
  @Autowired
  FileBoardMapper fileBoardMapper;

  @Override
  public List<FileBoardVO> readBoardList() {
    return fileBoardMapper.readBoardList();
  }

  @Override
  public FileBoardVO boardDetail(int b_no) {
    return fileBoardMapper.boardDetail(b_no);
  }

  @Override
  public int boardInsert(FileBoardVO fileBoard) {
    return fileBoardMapper.boardInsert(fileBoard);
  }

  @Override
  public int boardUpdate(FileBoardVO fileBoard) {
    return fileBoardMapper.boardUpdate(fileBoard);
  }

  @Override
  public int boardDelete(int b_no) {
    return fileBoardMapper.boardDelete(b_no);
  }

  @Override
  public int fileInsert(FileVO file) {
    return fileBoardMapper.fileInsert(file);
  }
}

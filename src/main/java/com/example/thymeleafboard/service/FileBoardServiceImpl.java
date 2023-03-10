package com.example.thymeleafboard.service;

import java.util.List;
import java.util.Map;

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
  public List<Map<String, String>> readBoardList() {
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

  @Override
  public FileVO fileDetail(int b_no) {
    return fileBoardMapper.fileDetail(b_no);
  }

  @Override
  public void deleteFile(int b_no) {
    fileBoardMapper.deleteFile(b_no);
  }

  @Override
  public List<FileVO> fileList() {
    return fileBoardMapper.fileList();
  }
}

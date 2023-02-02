package com.example.thymeleafboard.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.example.thymeleafboard.bean.FileBoardVO;
import com.example.thymeleafboard.bean.FileVO;

@Mapper
public interface FileBoardMapper {

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

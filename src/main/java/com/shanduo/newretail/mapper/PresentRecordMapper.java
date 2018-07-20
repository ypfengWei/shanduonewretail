package com.shanduo.newretail.mapper;

import java.util.List;

import com.shanduo.newretail.entity.PresentRecord;

public interface PresentRecordMapper {
    int deleteByPrimaryKey(String id);

    int insert(PresentRecord record);

    int insertSelective(PresentRecord record);

    PresentRecord selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PresentRecord record);

    int updateByPrimaryKey(PresentRecord record);
    
    PresentRecord getPresentRecord(String id, String state);
    
    int countPresentRecord(String state);
    
    List<PresentRecord> listPresentRecord(String state, Integer pageNum, Integer pageSize);
    
    int countUserPresentRecord(String userId);
    
    List<PresentRecord> listUserPresentRecord(String userId, Integer pageNum, Integer pageSize);
}
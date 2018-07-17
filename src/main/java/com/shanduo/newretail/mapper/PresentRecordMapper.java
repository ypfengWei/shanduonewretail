package com.shanduo.newretail.mapper;

import com.shanduo.newretail.entity.PresentRecord;

public interface PresentRecordMapper {
    int deleteByPrimaryKey(String id);

    int insert(PresentRecord record);

    int insertSelective(PresentRecord record);

    PresentRecord selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PresentRecord record);

    int updateByPrimaryKey(PresentRecord record);
}
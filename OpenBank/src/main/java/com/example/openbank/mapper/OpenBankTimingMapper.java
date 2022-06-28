package com.example.openbank.mapper;

import com.example.openbank.dao.OpenBankScheduledDao;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpenBankTimingMapper {
    @Select("select ent_id,out_application_id from open_bank_timing")
    List<OpenBankScheduledDao> queryAll();

    @Select("select ent_id,out_application_id from open_bank_timing where out_application_id=#{out_application_id}")
    OpenBankScheduledDao queryByOutId(String out_application_id);

    @Insert("insert into open_bank_timing(ent_id,out_application_id) values(#{ent_id},#{out_application_id})")
    void insertOpenBankScheduledDao(OpenBankScheduledDao openBankScheduledDao);

    @Delete("delete from open_bank_timing where out_application_id = #{out_application_id}")
    void deleteOpenBankScheduledDao(String out_application_id);
}

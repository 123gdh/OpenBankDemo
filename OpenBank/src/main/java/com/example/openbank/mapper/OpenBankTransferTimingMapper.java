package com.example.openbank.mapper;

import com.example.openbank.dao.OpenBankTransferTimingDao;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpenBankTransferTimingMapper {
    @Select("select out_transfer_id,ent_id from open_bank_transfer_timing")
    List<OpenBankTransferTimingDao> queryAll();

    @Select("select out_transfer_id,ent_id from open_bank_transfer_timing where out_transfer_id = #{out_transfer_id}")
    OpenBankTransferTimingDao queryByOutId(String out_transfer_id);

    @Insert("insert into open_bank_transfer_timing(out_transfer_id,ent_id) values(#{out_transfer_id},#{ent_id})")
    void insertOpenBankTransferTiming(OpenBankTransferTimingDao openBankTransferTimingDao);

    @Delete("delete from open_bank_transfer_timing where out_transfer_id = #{out_transfer_id}")
    void deleteByOutId(String out_transfer_id);
}

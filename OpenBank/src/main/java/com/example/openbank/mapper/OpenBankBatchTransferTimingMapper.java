package com.example.openbank.mapper;

import com.example.openbank.dao.OpenBankBatchTransferTimingDao;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpenBankBatchTransferTimingMapper {
    @Select("select out_batch_transfer_id,ent_id from open_bank_batch_transfer_timing;")
    List<OpenBankBatchTransferTimingDao> queryAll();

    @Insert("insert into open_bank_batch_transfer_timing(out_batch_transfer_id,ent_id) values(#{out_batch_transfer_id},#{ent_id})")
    void insertOpenBankBatchTransferTiming(OpenBankBatchTransferTimingDao openBankBatchTransferTimingDao);

    @Delete("delete from open_bank_batch_transfer_timing where out_batch_transfer_id = #{out_batch_transfer_id}")
    void deleteOpenBankBatchTransferTiming(String out_batch_transfer_id);
}

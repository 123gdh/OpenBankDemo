package com.example.openbank.mapper;

import com.example.openbank.dao.OpenBankBatchTransferDao;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface OpenBankBatchTransferMapper {
    @Insert("insert into open_bank_batch_transfer(batch_transfer_id,out_batch_transfer_id,total_amount,currency,total_num,payer_id,batch_transfer_status,transfers,batch_memo,attachment,goods_name,goods_detail,succeeded_transfer_num,private_transfer_type,pc_guide_url,mobile_guide_url) values(#{batch_transfer_id},#{out_batch_transfer_id},#{total_amount},#{currency},#{total_num},#{payer_id},#{batch_transfer_status},#{transfers},#{batch_memo},#{attachment},#{goods_name},#{goods_detail},#{succeeded_transfer_num},#{private_transfer_type},#{pc_guide_url},#{mobile_guide_url})")
    void insertOpenBankBatchTransfer(OpenBankBatchTransferDao openBankBatchTransferDao);

    @Select("select batch_transfer_id,out_batch_transfer_id,total_amount,currency,total_num,payer_id,batch_transfer_status,transfers,batch_memo,attachment,goods_name,goods_detail,succeeded_transfer_num,private_transfer_type,pc_guide_url,mobile_guide_url from open_bank_batch_transfer where out_batch_transfer_id = #{out_batch_transfer_id}")
    OpenBankBatchTransferDao queryOpenBankBatchTransferDao(String out_batch_transfer_id);

    @Update("update open_bank_batch_transfer set batch_transfer_status = #{batch_transfer_status}  where out_batch_transfer_id = #{out_batch_transfer_id}")
    void updateStatusByOutId(String batch_transfer_status,String out_batch_transfer_id);

    @Update("update open_bank_batch_transfer set batch_transfer_id = #{batch_transfer_id}  where out_batch_transfer_id = #{out_batch_transfer_id}")
    void updateBatchTransferId(String batch_transfer_id,String out_batch_transfer_id);
}

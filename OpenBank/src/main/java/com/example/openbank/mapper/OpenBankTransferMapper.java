package com.example.openbank.mapper;

import com.example.openbank.dao.OpenBankTransferDao;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpenBankTransferMapper {
    //demo以 * 代表所有参数，在正式环境为了提高性能，请按合格的sql语法将属性名罗列出来
    @Select("select transfer_id,out_transfer_id,amount,currency,payer_id,payee_id,transfer_status,memo,attachment,goods_name,goods_detail,receipt_status,not_supported_reason,receipt_eta,pc_guide_url,mobile_guide_url,failed_type,failed_detail from open_bank_transfer where out_transfer_id = #{out_transfer_id}")
    public OpenBankTransferDao queryByOutId(String out_transfer_id);

    @Insert("insert into open_bank_transfer(transfer_id,out_transfer_id,amount,currency,payer_id,payee_id,transfer_status,memo,attachment,goods_name,goods_detail,receipt_status,not_supported_reason,receipt_eta,pc_guide_url,mobile_guide_url,failed_type,failed_detail) values(#{transfer_id},#{out_transfer_id},#{amount},#{currency},#{payer_id},#{payee_id},#{transfer_status},#{memo},#{attachment},#{goods_name},#{goods_detail},#{receipt_status},#{not_supported_reason},#{receipt_eta},#{pc_guide_url},#{mobile_guide_url},#{failed_type},#{failed_detail})")
    public void insertOpenBankTransfer(OpenBankTransferDao openBankTransferDao);

    @Update("update open_bank_transfer set transfer_status = #{transfer_status} where out_transfer_id = #{out_transfer_id}")
    public void updateStatusByOutId(String transfer_status,String out_transfer_id);
}

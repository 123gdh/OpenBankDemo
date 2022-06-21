package com.example.openbank.mapper;

import com.example.openbank.dao.TransfersDao;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransfersMapper {
    @Insert("insert into transfers(transfer_id,out_transfer_detail_id,amount,memo,payee_id,receipt_status,not_supported_reason,receipt_eta,failed_type,failed_detail,transfer_status) values(#{transfer_id},#{out_transfer_detail_id},#{amount},#{memo},#{payee_id},#{receipt_status},#{not_supported_reason},#{receipt_eta},#{failed_type},#{failed_detail},#{transfer_status})")
    public void insertTransfers(TransfersDao transfersDao);

    @Select("select transfer_id,out_transfer_detail_id,amount,memo,payee_id,receipt_status,not_supported_reason,receipt_eta,failed_type,failed_detail,transfer_status from transfers where transfer_id = #{transfer_id}")
    public List<TransfersDao> queryByTransferId(String transfer_id);


    @Select("update transfers set transfer_status = #{transfer_status} where out_transfer_detail_id = #{out_transfer_detail_id}")
    public void updateTransfersStatus(String transfer_status, String out_transfer_detail_id);
}

package com.example.openbank.mapper;

import com.example.openbank.dao.PayerDao;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayerMapper {

    @Insert("insert into payer(payer_id,ent_id,ent_name,ent_acct_id,bank_account_number_last4,ebank_no) values(#{payer_id},#{ent_id},#{ent_name},#{ent_acct_id},#{bank_account_number_last4},#{ebank_no})")
    void insertPayer(PayerDao payerDao);

    @Select("select payer_id,ent_id,ent_name,ent_acct_id,bank_account_number_last4,ebank_no from payer")
    List<PayerDao> queryAll();

    @Select("select payer_id,ent_id,ent_name,ent_acct_id,bank_account_number_last4,ebank_no from payer where payer_id = #{payer_id}")
    PayerDao queryByPayerId(String payer_id);

    @Select("select payer_id,ent_id,ent_name,ent_acct_id,bank_account_number_last4,ebank_no from payer where payer_id = (select payer_id from open_bank_transfer where out_transfer_id = #{out_transfer_id})")
    PayerDao queryEntIdByOutTransferId(String out_transfer_id);

    @Select("select payer_id,ent_id,ent_name,ent_acct_id,bank_account_number_last4,ebank_no from payer where payer_id = (select payer_id from open_bank_batch_transfer where out_batch_transfer_id = #{out_batch_transfer_id})")
    PayerDao queryEntIdByOutBatchTransferId(String out_batch_transfer_id);
}

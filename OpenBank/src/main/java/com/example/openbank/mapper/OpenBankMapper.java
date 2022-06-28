package com.example.openbank.mapper;

import com.example.openbank.dao.OpenBankDao;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface OpenBankMapper {
    @Update("update openbank set status = #{status} where out_application_id=#{out_application_id}")
    void updateSignState(String status,String out_application_id);

    @Update("update openbank set status = #{status},application_id=#{application_id} where out_application_id=#{out_application_id}")
    void updateStateAndApplicationId(String status, String application_id, String out_application_id);

    @Update("update openbank set status = #{status}，ent_acct_id=#{ent_acct_id} where out_application_id=#{out_application_id}")
    void updateEntAcctId(String status,String ent_acct_id,String out_application_id);

    @Select("select out_application_id,bank_account_name,bank_account_number,bank_abbreviation,application_id,ent_id,status,application_type,sign_type,bank_account_number_last4,bank_name,failed_type,failed_detail,error_code,error_state,error_msg,create_date from openbank where bank_account_name = #{bank_account_name} and bank_account_number = #{bank_account_number} and bank_abbreviation = #{bank_abbreviation} order by create_date desc limit 1")
    OpenBankDao queryOpenBank(String bank_account_name, String bank_account_number, String bank_abbreviation);

    @Select("select out_application_id,bank_account_name,bank_account_number,bank_abbreviation,application_id,ent_id,status,application_type,sign_type,bank_account_number_last4,bank_name,failed_type,failed_detail,error_code,error_state,error_msg,create_date from openbank where out_application_id = #{out_application_id}")
    OpenBankDao queryOpenBankByOutApplicationId(String out_application_id);

    @Insert("insert into openbank(out_application_id,bank_account_name,bank_account_number,bank_abbreviation,application_id,ent_id,status,application_type,sign_type,bank_account_number_last4,bank_name,failed_type,failed_detail,error_code,error_state,error_msg,create_date) values(#{out_application_id},#{bank_account_name},#{bank_account_number},#{bank_abbreviation},#{application_id},#{ent_id},#{status},#{application_type},#{sign_type},#{bank_account_number_last4},#{bank_name},#{failed_type},#{failed_detail},#{error_code},#{error_state},#{error_msg},#{create_date})")
    void insertOpenBnak(OpenBankDao productOpenDao);
}

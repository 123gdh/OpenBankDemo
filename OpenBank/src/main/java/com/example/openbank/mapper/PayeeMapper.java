package com.example.openbank.mapper;

import com.example.openbank.dao.PayeeDao;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayeeMapper {
    @Select("select out_payee_id,bank_account_name,bank_account_number,bank_name,bank_branch_name,bank_branch_id from payee")
    List<PayeeDao> queryAll();

    @Select("select out_payee_id,bank_account_name,bank_account_number,bank_name,bank_branch_name,bank_branch_id from payee where out_payee_id = #{out_payee_id}")
    PayeeDao queryByOutPayeeId(String out_payee_id);

    @Insert("insert into payee(out_payee_id,bank_account_name,bank_account_number,bank_name,bank_branch_name,bank_branch_id) values(#{out_payee_id},#{bank_account_name},#{bank_account_number},#{bank_name},#{bank_branch_name},#{bank_branch_id})")
    void insertPayee(PayeeDao payeeDao);
}

package com.example.openbank.mapper;

import com.example.openbank.dao.ProductOpenDao;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface GoProductMapper {


    @Insert("insert into product(out_request_no,request_no,status,unified_social_credit_code,merchant_name,product_name,create_date) values(#{out_request_no},#{request_no},#{status},#{unified_social_credit_code},#{merchant_name},#{product_name},#{create_date})")
    void insertProduct(ProductOpenDao productOpenDao);

    @Select("select out_request_no,request_no,status,unified_social_credit_code,merchant_name,product_name,create_date from product where unified_social_credit_code = #{unified_social_credit_code} and product_name = #{product_name} order by create_date desc limit 1")
    ProductOpenDao queryProduct(String unified_social_credit_code,String product_name);

    @Select("select out_request_no,request_no,status,unified_social_credit_code,merchant_name,product_name,create_date from product where status in('PROCESSING','INIT') and create_date <= #{create_date}")
    List<ProductOpenDao> queryOverdueApplication(Date date);

    @Select("select out_request_no,request_no,status,unified_social_credit_code,merchant_name,product_name,create_date from product where out_request_no = #{out_request_no} limit 1")
    ProductOpenDao queryByOutRequestNo(String out_request_no);

    @Update("update product set status = #{status} where unified_social_credit_code=#{unified_social_credit_code} and product_name = #{product_name}")
    void updateStateByUnifiedPname(String status,String unified_social_credit_code,String product_name);

    @Update("update product set status = #{status},request_no = #{request_no} where unified_social_credit_code=#{unified_social_credit_code} and product_name = #{product_name}")
    void updateStateAndRequestNoByUnifiedPname(String status,String request_no,String unified_social_credit_code,String product_name);

    @Update("update product set status = #{status} where out_request_no = #{out_request_no}")
    void updateStateByOutRequestNo(String status,String out_request_no);
}

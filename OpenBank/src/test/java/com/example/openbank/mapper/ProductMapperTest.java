package com.example.openbank.mapper;

import com.example.openbank.dao.ProductOpenDao;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Component
public interface ProductMapperTest {
    @Insert("insert into product(out_request_no,request_no,status,unified_social_credit_code,merchant_name,product_name,createDate) values(#{out_request_no},#{request_no},#{status},#{unified_social_credit_code},#{merchant_name},#{product_name},#{createDate})")
    public void insertProduct(ProductOpenDao productOpenDao);

    @Select("select out_request_no,request_no,status,unified_social_credit_code,merchant_name,product_name,createDate from product where status in('PROCESSING','INIT') and createDate > #{createDate}")
    public List<ProductOpenDao> queryOverdueApplication(Date date);

    @Update("update product set request_no = #{request_no} where out_request_no = #{out_request_no}")
    public void updateProductByoutRequestNo(String request_no,String out_request_no);
}

package com.example.openbank.mapper;

import com.example.openbank.dao.ProductTimingDao;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductTimingMapper {
    @Select("select out_request_no from product_timing")
    public List<ProductTimingDao> queryAll();

    @Select("select out_request_no from product_timing where out_request_no = #{out_request_no}")
    public ProductTimingDao queryByOutRequestNo(String out_request_no);

    @Insert("insert into product_timing(out_request_no) values(#{out_request_no})")
    public void insertProductTiming(ProductTimingDao productTimingDao);

    @Delete("delete from product_timing where out_request_no = #{out_request_no}")
    public void deleteProductTiming(String out_request_no);
}

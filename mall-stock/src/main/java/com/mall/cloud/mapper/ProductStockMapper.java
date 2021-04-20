package com.mall.cloud.mapper;

import com.mall.cloud.dto.ProductStock;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface ProductStockMapper {

    @Select(" select * from product_stock where productId = #{productId}")
    ProductStock findById(@Param("productId") Integer productId);


    @Update("<script> " +
            "update product_stock " +
            " <set> "+
                "<if test='stock01!=null'>,stock01 = #{stock01} </if> " +
                "<if test='stock02!=null'>,stock02 = #{stock02} </if> " +
                "<if test='stock03!=null'>,stock03 = #{stock03} </if> " +
                "<if test='stock04!=null'>,stock04 = #{stock04} </if> " +
                "<if test='stock05!=null'>,stock05 = #{stock05} </if> " +
                "<if test='stock06!=null'>,stock06 = #{stock06} </if> " +
                "<if test='stock07!=null'>,stock07 = #{stock07} </if> " +
                "<if test='stock08!=null'>,stock08 = #{stock08} </if> " +
                "<if test='stock09!=null'>,stock09 = #{stock09} </if> " +
                "<if test='stock10!=null'>,stock10 = #{stock10} </if> " +
            " </set> "+
            "where id = #{id} " +
            "</script>")
    void update(ProductStock productStock);
}

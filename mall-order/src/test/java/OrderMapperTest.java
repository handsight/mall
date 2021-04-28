//import com.mall.cloud.OrderApplication;
//import com.mall.cloud.mapper.OrderMapper;
//import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = {OrderApplication.class})
//public class OrderMapperTest {
//
//    @Autowired
//    private OrderMapper orderMapper;
//
//    @Test
//    public void testInsertOrder(){
//        for(int i=1;i<20;i++){
//            orderMapper.insertOrder(new BigDecimal(i),4L,"SUCCESS");
//        }
//    }
//
//    @Test
//    public void testSelectOrderbyIds(){
//        List<Long> ids = new ArrayList<>();
//        ids.add(594189997796491265L);
//        ids.add(594189999008645121L);
//        List<Map> maps = orderMapper.selectOrderbyIds(ids);
//        System.out.println(maps);
//    }
//}

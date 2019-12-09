import com.example.wx_pay.model.order.Order;
import org.apache.ibatis.*;

import java.util.List;

@Mapper
public interface OrderMapper {

    @Select("SELECT id, userId, state, price, create_time, update_time FROM `order`")
    public List<Order> getOrder();

    @Update("UPDATE `order` SET state = #{state} WHERE id = #{outTradeNo}")
    public boolean updateOrderState(@Param("state")Integer state,@Param("outTradeNo")String outTradeNo);
}

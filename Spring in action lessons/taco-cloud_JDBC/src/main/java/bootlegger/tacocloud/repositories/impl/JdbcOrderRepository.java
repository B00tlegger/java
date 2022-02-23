package bootlegger.tacocloud.repositories.impl;

import bootlegger.tacocloud.model.Order;
import bootlegger.tacocloud.model.Taco;
import bootlegger.tacocloud.repositories.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Repository
public class JdbcOrderRepository implements OrderRepository {
    private SimpleJdbcInsert orderInsert;
    private SimpleJdbcInsert tacoInsert;
    private ObjectMapper objectMapper;

    @Autowired
    public JdbcOrderRepository (JdbcTemplate jdbcTemplate) {
        orderInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("taco_order").usingGeneratedKeyColumns("id");
        tacoInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("taco_order_tacos");
        objectMapper = new ObjectMapper();
    }

    @Override
    public Order save (Order order) {
        order.setPlacedAt(new Date());
        order.setId(saveOrderDetails(order));
        for(Taco taco : order.getTacos()) {
            saveTacosForOrder(taco, order.getId());
        }
        return order;
    }

    private long saveOrderDetails (Order order) {
        @SuppressWarnings("unchecked")
        Map<String, Object> values = objectMapper.convertValue(order, Map.class);
        values.put("placedAt", order.getPlacedAt());
        long orderId = orderInsert.executeAndReturnKeyHolder(values).getKey().longValue();
        return orderId;
    }

    private void saveTacosForOrder (Taco taco, long orderId) {
        Map<String, Object> values = new HashMap<>();
        values.put("taco_order", orderId);
        values.put("taco", taco.getId());
        tacoInsert.execute(values);
    }
}

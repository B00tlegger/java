package bootlegger.tacocloud.repositories;

import bootlegger.tacocloud.model.Order;

public interface OrderRepository {
    Order save(Order order);
}

package bootlegger.tacocloud.controllers;

import bootlegger.tacocloud.model.Order;
import bootlegger.tacocloud.repositories.OrderRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/orders", produces = "application/json")
@CrossOrigin(origins = "*")
public class OrderController {
    private OrderRepository orderRepository;

    public OrderController (OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Order postOrder (@RequestBody Order order) {
        return orderRepository.save(order);
    }

    @GetMapping
    public Iterable<Order> allOrders () {
        return orderRepository.findAll();
    }

    @PutMapping("/{orderId}")
    public Order putOrder(@RequestBody Order order){
        return orderRepository.save(order);
    }

    @PatchMapping("/{orderId}")
    public Order patchOrder(@PathVariable Long orderId, @RequestBody Order order){
        Order foundOrder = orderRepository.findById(orderId).get();
        if(order.getDeliveryZip() != null){
            foundOrder.setDeliveryZip(order.getDeliveryZip());
        }
        if(order.getDeliveryState() != null){
            foundOrder.setDeliveryState(order.getDeliveryState());
        }
        if(order.getDeliveryCity() != null){
            foundOrder.setDeliveryCity(order.getDeliveryCity());
        }
        if(order.getDeliveryStreet() != null){
            foundOrder.setDeliveryStreet(order.getDeliveryStreet());
        }
        if(order.getDeliveryName() != null){
            foundOrder.setDeliveryName(order.getDeliveryName());
        }
        if(order.getCcCVV() != null){
            foundOrder.setCcCVV(order.getCcCVV());
        }
        if(order.getCcNumber() != null){
            foundOrder.setCcNumber(order.getCcNumber());
        }
        if(order.getCcExpiration() != null){
            foundOrder.setCcExpiration(order.getCcExpiration());
        }
        return orderRepository.save(foundOrder);
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable long orderId){
        try{
            orderRepository.deleteById(orderId);
        }catch(EmptyResultDataAccessException exception){}

    }
}

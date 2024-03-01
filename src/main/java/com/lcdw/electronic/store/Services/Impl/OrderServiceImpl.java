package com.lcdw.electronic.store.Services.Impl;

import com.lcdw.electronic.store.Entities.*;
import com.lcdw.electronic.store.Exception.BadApiRequest;
import com.lcdw.electronic.store.Exception.ResourceNotFoundException;
import com.lcdw.electronic.store.Helper.Helper;
import com.lcdw.electronic.store.Repository.CartRepository;
import com.lcdw.electronic.store.Repository.OrderRepository;
import com.lcdw.electronic.store.Repository.UserRepository;
import com.lcdw.electronic.store.Services.OrderServices;
import com.lcdw.electronic.store.dtos.OrderDto;
import com.lcdw.electronic.store.dtos.PageableResponse;
import com.lcdw.electronic.store.dtos.ProductDto;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderServices {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ModelMapper mapper;

    private Logger logger= LoggerFactory.getLogger(OrderServiceImpl.class);

    @Override
    public OrderDto createOrder(OrderDto orderDto, String userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found or Incorrect userId !!!"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new BadApiRequest("Cart of given user is not been created , Kindly create your Cart First !!!"));
        List<CartItem> cartItems= cart.getItems();
        if(cartItems==null || cartItems.isEmpty())
        {
            throw new BadApiRequest("For Creating Order , you need to add items to your Cart!!!");
        }
        else
        {
            Order order = Order.builder()
                    .billingName(orderDto.getBillingName())
                    .billingPhone(orderDto.getBillingPhone())
                    .billingAddress(orderDto.getBillingAddress())
                    .orderedDate(new Date())
                    .deliveredDate(null)
                    .paymentStatus(orderDto.getPaymentStatus())
                    .orderStatus(orderDto.getOrderStatus())
                    .orderId(UUID.randomUUID().toString())
                    .user(user)
                    .build();

            //order amount ,item

            AtomicReference<Integer> addTotal=new AtomicReference<>(0);
            AtomicReference<Integer> addTotalDiscount= new AtomicReference<>(0);


            List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {

                double M= cartItem.getProduct().getPrice();
                double S=cartItem.getProduct().getDiscountedPrice();
                double discount=M-S;
                OrderItem orderItem = OrderItem.builder()
                        .quantity(cartItem.getQuantity())
                        .product(cartItem.getProduct())
                        .totalPrice(cartItem.getQuantity() * cartItem.getProduct().getPrice())
                        .discountedPrice(cartItem.getQuantity() * cartItem.getProduct().getDiscountedPrice())
                        .discountPercentage((discount / M) * 100)
                        .title(cartItem.getProduct().getTitle())
                        .order(order)
                        .build();

                addTotal.set(addTotal.get() + orderItem.getTotalPrice());
                addTotalDiscount.set(addTotalDiscount.get() + orderItem.getDiscountedPrice());
                return orderItem;
            }).collect(Collectors.toList());
                order.setOrderItems(orderItems);
                order.setTotalBill(addTotalDiscount.get());
            double M1=  addTotal.get();
            double S1=addTotalDiscount.get();
            double discount1=M1-S1;
            order.setTotalDiscountPercentage((discount1 / M1) * 100);

            Order savedOrder = orderRepository.save(order);

            return mapper.map(savedOrder,OrderDto.class);
        }
    }

    @Override
    public void removeOrder(String userId, String orderId)
    {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found or incorrect User !!"));
        orderRepository.findByUser(user).orElseThrow(()->new ResourceNotFoundException("No Order has been Created for this User"));
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Incorrect orderId or no such orderId is present !!!"));
        List<OrderItem> orderItems=order.getOrderItems();
        if(orderItems.isEmpty() || orderItems==null)
        {
            throw new BadApiRequest("Currently you have no OrderItems to delete !!!");
        }
        else
        {
            orderRepository.deleteById(orderId);
        }

    }

    @Override
    public List<OrderDto> getOrdersOfUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User is incorrect or not present !!!"));
        Optional<Order> orders = orderRepository.findByUser(user);
        List<OrderDto> orderDtos = orders.stream().map(order -> mapper.map(order, OrderDto.class)).collect(Collectors.toList());
        return orderDtos;
    }

    @Override
    public PageableResponse<OrderDto> getOrders(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc"))?(Sort.by(sortBy).descending()):(Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<Order> page = orderRepository.findAll(pageable);
        PageableResponse<OrderDto> response= Helper.getPageableResponse(page,OrderDto.class);
        return response;
    }

    @Override
    public void placeOrder(String userId, String orderId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found or incorrect User !!"));
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Incorrect orderId or no such orderId is present !!!"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Given User has no Cart Items !!!"));
       // List<OrderItem> orderItems=order.getOrderItems();
        List<OrderItem>orderItems=order.getOrderItems();
        if(orderItems.isEmpty() || orderItems==null)
        {
            throw new BadApiRequest("First Place an order Before Placing !!!");
        }
        else {
          if(order.getPaymentStatus().equalsIgnoreCase("Paid") || order.getPaymentStatus().equalsIgnoreCase("DELIVERED"))
          {
              throw new BadApiRequest("Your Order is been already Paid  !!!");
          }
          else
          {
              order.setOrderStatus("Placed");
              order.setPaymentStatus("PAID");
              List<CartItem> cartItems=cart.getItems();
              cartItems.clear();
              cartRepository.save(cart);
              orderRepository.save(order);
          }
        }
    }

    @Override
    public void deliveredOrder(String orderId)
    {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Incorrect orderId or no such orderId is present !!!"));
        List<OrderItem>orderItems=order.getOrderItems();
       if((order.getPaymentStatus().equalsIgnoreCase("Paid"))==false  && order.getOrderStatus().equalsIgnoreCase("Placed")==false)
        {

            throw new BadApiRequest("Payment status or order Status is yet to be completed !!!");
    } else if((order.getPaymentStatus().equalsIgnoreCase("PAID"))  && (order.getOrderStatus().equalsIgnoreCase("Placed")) )
            {
                logger.info("Inside this Loop !!!!");
                order.setOrderStatus("DELIVERED");
                order.setDeliveredDate(new Date());
                orderItems.clear();
                orderRepository.save(order);
            } else if (order.getOrderStatus().equalsIgnoreCase("DELIVERED"))
        {
            throw new BadApiRequest("This Order is already been delivered !!!");
       }

    }

}

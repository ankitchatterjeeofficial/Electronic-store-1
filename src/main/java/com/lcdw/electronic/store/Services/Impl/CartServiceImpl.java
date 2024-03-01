package com.lcdw.electronic.store.Services.Impl;

import com.lcdw.electronic.store.Controllers.ProductController;
import com.lcdw.electronic.store.Entities.Cart;
import com.lcdw.electronic.store.Entities.CartItem;
import com.lcdw.electronic.store.Entities.Product;
import com.lcdw.electronic.store.Entities.User;
import com.lcdw.electronic.store.Exception.BadApiRequest;
import com.lcdw.electronic.store.Exception.ResourceNotFoundException;
import com.lcdw.electronic.store.Helper.Helper;
import com.lcdw.electronic.store.Repository.CartItemRepository;
import com.lcdw.electronic.store.Repository.CartRepository;
import com.lcdw.electronic.store.Repository.ProductRepository;
import com.lcdw.electronic.store.Repository.UserRepository;
import com.lcdw.electronic.store.Services.CartServices;
import com.lcdw.electronic.store.dtos.AddItemToRequest;
import com.lcdw.electronic.store.dtos.CartDto;
import com.lcdw.electronic.store.dtos.PageableResponse;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartServices {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ModelMapper mapper;

    private Logger logger= LoggerFactory.getLogger(ProductController.class);

    @Override
    public CartDto additemToCart(String userId, AddItemToRequest request) {

        int quantity = request.getQuantity();
        String productId = request.getProductId();

        if (quantity <= 0) {
            throw new BadApiRequest("Kindly provide a positive Quantity !!!");
        }


            //fetch the Product
            Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product Id is not present in store !!!"));
            //fetch the user
            User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User is not available in the store database !!"));


            if (product.isStock() == false) {
                throw new BadApiRequest("Product is Out Of Stock !!!!");
            }
            else
            {

            Cart cart = null;

           // logger.info("give User {} ",cartRepository.findByUser(user));

            //check cart is present or not if not present create cart
            try {
                cart = cartRepository.findByUser(user).get();
               // logger.info("give User {} ", cartRepository.findByUser(user).toString());
            } catch (NoSuchElementException ex) {
                cart = new Cart();
                cart.setCartId(UUID.randomUUID().toString());
                cart.setCreatedAt(new Date());
            }

            //perform cart operation

            //if cart item already present then update
            AtomicReference<Boolean> updated = new AtomicReference<>(false);
          //  logger.info("Cart items {}  cart values {} ", cart.getItems(), cart.getCartId());
            List<CartItem> items = cart.getItems();
            items = items.stream().map(item -> {

                if (item.getProduct().getProductId().equals(productId)) {
                    //item already present in cart
                    item.setQuantity(quantity);
                    item.setTotalPrice(quantity * product.getPrice());
                    item.setDiscountedPrice(quantity * product.getDiscountedPrice());

                    //( (  ( (product.getPrice() ) -(product.getDiscountedPrice() )  )/ ((product.getPrice()) ) *100
                    double M = product.getPrice();
                    double S = product.getDiscountedPrice();
                    double discount = M - S;
                    item.setDiscountedPercentage((discount / M) * 100);
                    updated.set(true);
                }
                return item;
            }).collect(Collectors.toList());

            //save UpdatedItems to Cart if present
            // cart.setItems(updatedItems);

            //create Items if cart items are not present
            if (!updated.get()) {
                double M = product.getPrice();
                double S = product.getDiscountedPrice();
                double discount = M - S;

                CartItem cartItem = CartItem.builder()
                        .quantity(quantity)
                        .totalPrice(quantity * product.getPrice())
                        .discountedPrice(quantity * product.getDiscountedPrice())
                        .discountedPercentage((discount / M) * 100)
                        .cart(cart)
                        .product(product)
                        .build();

                cart.getItems().add(cartItem);
            }

            //if user is new assign the user
            cart.setUser(user);
            Cart updateCart = cartRepository.save(cart);

            return mapper.map(updateCart, CartDto.class);
        }

    }
    @Override
    public void removeItemFromCart(String userId, int cartItemId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User is not available in the store database !!"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new BadApiRequest("Cart of given user is not found !!!"));
        List<CartItem> items= cart.getItems();
        if(items==null || items.isEmpty())
        {
            throw new BadApiRequest("Cart is already empty for User , Nothing to delete !!!");
        }
        else {
            CartItem cartItemNotFound = cartItemRepository.findById(cartItemId).orElseThrow(() -> new ResourceNotFoundException("Cart Item not Found or Incorrect cartItem id is provided !!!"));
           // logger.info("Get Cart Item ID {} ",cartItemRepository.findById(cartItemId).toString());
            cartItemRepository.remove(cartItemId);


        }
    }

    @Override
    public void clearCart(String userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User is not available in the store database !!"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart of given user is not been created , create the cart first !!!!!!"));
        List<CartItem> items = cart.getItems();
        if(items==null || items.isEmpty())
        {
            throw new BadApiRequest("Cart is already cleared for the given user. No items in the Cart !!!");
        }
        else {
            items.clear();
            cartRepository.save(cart);
        }
    }

    @Override
    public CartDto getCartByUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User is not available in the store database !!"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart of given user is not been created , create the cart first !!!"));
        List<CartItem> items = cart.getItems();
        if(items==null || items.isEmpty())
        {
            throw new BadApiRequest("Cart is present but no item present in the cart to display !!!");
        }
        else {
            return mapper.map(cart, CartDto.class);
        }
    }

    @Override
    public PageableResponse<CartDto> getAllCart(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc"))?(Sort.by(sortBy).descending()):(Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<Cart> page = cartRepository.findAll(pageable);
        PageableResponse<CartDto> response = Helper.getPageableResponse(page, CartDto.class);
        return response;
    }


}

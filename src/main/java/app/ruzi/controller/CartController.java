package app.ruzi.controller;

import app.ruzi.configuration.annotation.auth.CustomAuthRole;
import app.ruzi.configuration.annotation.auth.MethodInfo;
import app.ruzi.configuration.messaging.HandlerService;
import app.ruzi.configuration.messaging.MessageResponse;
import app.ruzi.service.app.cart.CartService;
import app.ruzi.service.payload.app.AddCartItemDto;
import app.ruzi.service.payload.app.CreateCartDto;
import app.ruzi.service.payload.app.UpdateCartItemDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/route-cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final HandlerService handlerService;

    @PostMapping("/create")
    @CustomAuthRole(roles = {"ROLE_CART_CREATE"})
    @MethodInfo(methodName = "create-card-session")
    public ResponseEntity<?> create(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @Valid @RequestBody(required = false) CreateCartDto dto,
            BindingResult bindingResult
    ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> cartService.createSession(dto),
                bindingResult,
                langType
        );
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

    @PostMapping("/add-item")
    @CustomAuthRole(roles = {"ROLE_CART_CREATE"})
    @MethodInfo(methodName = "add-item-to-card")
    public ResponseEntity<?> addItem(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @RequestBody AddCartItemDto dto) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> cartService.addItem(dto),
                langType
        );
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);

    }

    @PostMapping("/update-item")
    @CustomAuthRole(roles = {"ROLE_CART_CREATE"})
    @MethodInfo(methodName = "update-item-quantity")
    public ResponseEntity<?> updateItem(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @Valid @RequestBody UpdateCartItemDto dto
    ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> cartService.updateItemQuantity(dto),
                langType
        );
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

    @GetMapping("/get-item/{cartSessionId}")
    @CustomAuthRole(roles = {"ROLE_CART_CREATE"})
    @MethodInfo(methodName = "get-item-quantity")
    public ResponseEntity<?> getItem(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @PathVariable("cartSessionId") String cartSessionId
    ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> cartService.getItemsBySessionId(cartSessionId),
                langType
        );
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

}


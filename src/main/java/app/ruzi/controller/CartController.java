package app.ruzi.controller;

import app.ruzi.configuration.annotation.auth.CustomAuthRole;
import app.ruzi.configuration.annotation.auth.MethodInfo;
import app.ruzi.configuration.messaging.HandlerService;
import app.ruzi.configuration.messaging.MessageResponse;
import app.ruzi.service.app.cart.CartService;
import app.ruzi.service.payload.app.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/route-cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final HandlerService handlerService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_CART_CREATE')")
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
    @PreAuthorize("hasAuthority('ROLE_CART_CREATE')")
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
    @PreAuthorize("hasAuthority('ROLE_CART_CREATE')")
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

    @PatchMapping("/update-item-price")
    @PreAuthorize("hasAuthority('ROLE_CART_CREATE')")
    @MethodInfo(methodName = "update-item-price")
    public ResponseEntity<?> updateItemPrice(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @Valid @RequestBody UpdateCartItemPriceDto dto
    ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> cartService.updateItemPrice(dto),
                langType
        );
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }


    @GetMapping("/get-item/{cartSessionId}")
    @PreAuthorize("hasAuthority('ROLE_CART_CREATE')")
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

    @DeleteMapping("/delete-item/{cartItemId}")
    @PreAuthorize("hasAuthority('ROLE_CART_CREATE')")
    @MethodInfo(methodName = "delete-cart-item")
    public ResponseEntity<?> deleteItem(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @PathVariable("cartItemId") String cartItemId
    ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> cartService.deleteItem(cartItemId),
                langType
        );
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

    @DeleteMapping("/delete-cart/{cartSessionId}")
    @PreAuthorize("hasAuthority('ROLE_CART_CREATE')")
    @MethodInfo(methodName = "delete-cart-session")
    public ResponseEntity<?> deleteCart(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @PathVariable("cartSessionId") String cartSessionId
    ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> cartService.deleteCart(cartSessionId),
                langType
        );
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

    @DeleteMapping("/cancel-cart/{cartSessionId}")
    @PreAuthorize("hasAuthority('ROLE_CART_CREATE')")
    @MethodInfo(methodName = "cancel-cart-session")
    public ResponseEntity<?> cancelCart(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @PathVariable("cartSessionId") String cartSessionId
    ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> cartService.cancelCart(cartSessionId),
                langType
        );
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

    @PatchMapping("/add-customer-referrer")
    @PreAuthorize("hasAuthority('ROLE_CART_CREATE')")
    @MethodInfo(methodName = "add-item-to-card")
    public ResponseEntity<?> addItem(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @RequestBody AddCustomerReferrerToCartDto dto) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> cartService.addCusRef(dto),
                langType
        );
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);

    }

    @DeleteMapping("/remove-customer-referrer/{cardSessionId}/{type}")
    @PreAuthorize("hasAuthority('ROLE_CART_CREATE')")
    @MethodInfo(methodName = "delete-cart-item")
    public ResponseEntity<?> deleteCusRef(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @PathVariable("cardSessionId") String cardSessionId,
            @PathVariable("type") String type
    ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> cartService.removeCusRef(new RemoveCustomerReferrerToCartDto(cardSessionId, type)),
                langType
        );
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

}


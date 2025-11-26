package app.ruzi.controller;

import app.ruzi.configuration.annotation.auth.MethodInfo;
import app.ruzi.configuration.messaging.HandlerService;
import app.ruzi.configuration.messaging.MessageResponse;
import app.ruzi.entity.app.CartItem;
import app.ruzi.entity.app.CartSession;
import app.ruzi.service.app.cart.CartPaymentService;
import app.ruzi.service.app.cart.CartService;
import app.ruzi.service.app.cart.PrinterService;
import app.ruzi.service.app.checkout.CheckoutService;
import app.ruzi.service.payload.app.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/route-cart")
@RequiredArgsConstructor
public class CartController {

    private final PrinterService printerService;
    private final CartService cartService;
    private final CheckoutService checkoutService;
    private final CartPaymentService paymentService;
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

    @DeleteMapping("/remove-customer-referrer/{cartSessionId}/{type}")
    @PreAuthorize("hasAuthority('ROLE_CART_CREATE')")
    @MethodInfo(methodName = "delete-cart-item")
    public ResponseEntity<?> deleteCusRef(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @PathVariable("cartSessionId") String cartSessionId,
            @PathVariable("type") String type
    ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> cartService.removeCusRef(new RemoveCustomerReferrerToCartDto(cartSessionId, type)),
                langType
        );
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasAuthority('ROLE_CART_CREATE')")
    @MethodInfo(methodName = "checkout-cart-session")
    public ResponseEntity<?> checkout(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @Valid @RequestBody CheckoutDto dto,
            BindingResult bindingResult
    ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> checkoutService.checkout(dto),
                bindingResult,
                langType
        );

        return ResponseEntity
                .status(messageResponse.getStatus())
                .body(messageResponse);
    }


    @GetMapping("/payments/{cartSessionId}")
    @PreAuthorize("hasAuthority('ROLE_CART_READ')")
    public ResponseEntity<?> getPayments(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @PathVariable String cartSessionId
    ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> paymentService.getPayments(cartSessionId),
                langType
        );
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }


    @PostMapping("/print-receipt/{cartId}")
    public ResponseEntity<?> print(@PathVariable String cartId,
                                   @RequestParam String printerIp) throws Exception {

        // Faqat ID ni matn qilib yuboramiz
        String text = "CART SESSION ID:\n" + cartId + "\n";

        printerService.printSimple(printerIp, 9100, text);

        return ResponseEntity.ok("OK");
    }

}


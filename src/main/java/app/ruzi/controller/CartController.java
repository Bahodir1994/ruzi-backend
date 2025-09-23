//package app.ruzi.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/carts")
//@RequiredArgsConstructor
//public class CartController {
//
//    private final CartService cartService;
//
//    @GetMapping
//    public ResponseEntity<List<CartDto>> getAll() { return ResponseEntity.ok().build(); }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<CartDto> getById(@PathVariable Long id) { return ResponseEntity.ok().build(); }
//
//    @PostMapping
//    public ResponseEntity<CartDto> create(@RequestBody CartDto dto) { return ResponseEntity.ok().build(); }
//
//    @PostMapping("/{id}/items")
//    public ResponseEntity<CartItemDto> addItem(@PathVariable Long id, @RequestBody CartItemDto dto) { return ResponseEntity.ok().build(); }
//
//    @PatchMapping("/{id}/items/{itemId}")
//    public ResponseEntity<CartItemDto> updateItem(@PathVariable Long id, @PathVariable Long itemId, @RequestBody CartItemDto dto) { return ResponseEntity.ok().build(); }
//
//    @DeleteMapping("/{id}/items/{itemId}")
//    public ResponseEntity<Void> deleteItem(@PathVariable Long id, @PathVariable Long itemId) { return ResponseEntity.noContent().build(); }
//
//    @PostMapping("/{id}/checkout")
//    public ResponseEntity<Void> checkout(@PathVariable Long id) { return ResponseEntity.ok().build(); }
//
//    @PostMapping("/{id}/cancel")
//    public ResponseEntity<Void> cancel(@PathVariable Long id) { return ResponseEntity.ok().build(); }
//}
//

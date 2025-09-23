//package app.ruzi.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/products")
//@RequiredArgsConstructor
//public class ProductController {
//
//    private final ProductService productService;
//
//    @GetMapping
//    public ResponseEntity<List<ProductDto>> getAll() { return ResponseEntity.ok().build(); }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<ProductDto> getById(@PathVariable Long id) { return ResponseEntity.ok().build(); }
//
//    @PostMapping
//    public ResponseEntity<ProductDto> create(@RequestBody ProductDto dto) { return ResponseEntity.ok().build(); }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<ProductDto> update(@PathVariable Long id, @RequestBody ProductDto dto) { return ResponseEntity.ok().build(); }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) { return ResponseEntity.noContent().build(); }
//}
//

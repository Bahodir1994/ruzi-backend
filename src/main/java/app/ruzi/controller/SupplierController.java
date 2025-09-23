//package app.ruzi.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/suppliers")
//@RequiredArgsConstructor
//public class SupplierController {
//
//    private final SupplierService supplierService;
//
//    @GetMapping
//    public ResponseEntity<List<SupplierDto>> getAll() { return ResponseEntity.ok().build(); }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<SupplierDto> getById(@PathVariable Long id) { return ResponseEntity.ok().build(); }
//
//    @PostMapping
//    public ResponseEntity<SupplierDto> create(@RequestBody SupplierDto dto) { return ResponseEntity.ok().build(); }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<SupplierDto> update(@PathVariable Long id, @RequestBody SupplierDto dto) { return ResponseEntity.ok().build(); }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) { return ResponseEntity.noContent().build(); }
//}

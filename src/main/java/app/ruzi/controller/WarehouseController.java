//package app.ruzi.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/warehouses")
//@RequiredArgsConstructor
//public class WarehouseController {
//
//    private final WarehouseService warehouseService;
//
//    @GetMapping
//    public ResponseEntity<List<WarehouseDto>> getAll() { return ResponseEntity.ok().build(); }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<WarehouseDto> getById(@PathVariable Long id) { return ResponseEntity.ok().build(); }
//
//    @PostMapping
//    public ResponseEntity<WarehouseDto> create(@RequestBody WarehouseDto dto) { return ResponseEntity.ok().build(); }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<WarehouseDto> update(@PathVariable Long id, @RequestBody WarehouseDto dto) { return ResponseEntity.ok().build(); }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) { return ResponseEntity.noContent().build(); }
//}
//

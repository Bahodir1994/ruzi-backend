//package app.ruzi.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/stocks")
//@RequiredArgsConstructor
//public class StockController {
//
//    private final StockService stockService;
//
//    @GetMapping
//    public ResponseEntity<List<StockDto>> getAll() { return ResponseEntity.ok().build(); }
//
//    @GetMapping("/{productId}")
//    public ResponseEntity<List<StockDto>> getByProduct(@PathVariable Long productId) { return ResponseEntity.ok().build(); }
//
//    @GetMapping("/warehouse/{warehouseId}")
//    public ResponseEntity<List<StockDto>> getByWarehouse(@PathVariable Long warehouseId) { return ResponseEntity.ok().build(); }
//
//    @PatchMapping("/{id}/adjust")
//    public ResponseEntity<StockDto> adjust(@PathVariable Long id, @RequestBody StockAdjustDto dto) { return ResponseEntity.ok().build(); }
//}
//

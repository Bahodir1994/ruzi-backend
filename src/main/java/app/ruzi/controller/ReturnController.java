//package app.ruzi.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/returns")
//@RequiredArgsConstructor
//public class ReturnController {
//
//    private final ReturnService returnService;
//
//    @GetMapping
//    public ResponseEntity<List<ReturnOrderDto>> getAll() { return ResponseEntity.ok().build(); }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<ReturnOrderDto> getById(@PathVariable Long id) { return ResponseEntity.ok().build(); }
//
//    @PostMapping
//    public ResponseEntity<ReturnOrderDto> create(@RequestBody ReturnOrderDto dto) { return ResponseEntity.ok().build(); }
//
//    @PostMapping("/{id}/items")
//    public ResponseEntity<ReturnItemDto> addItem(@PathVariable Long id, @RequestBody ReturnItemDto dto) { return ResponseEntity.ok().build(); }
//
//    @PostMapping("/{id}/process")
//    public ResponseEntity<Void> process(@PathVariable Long id) { return ResponseEntity.ok().build(); }
//}
//

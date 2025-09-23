//package app.ruzi.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/employees")
//@RequiredArgsConstructor
//public class EmployeeController {
//
//    private final EmployeeService employeeService;
//
//    @GetMapping
//    public ResponseEntity<List<EmployeeDto>> getAll() { return ResponseEntity.ok().build(); }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<EmployeeDto> getById(@PathVariable Long id) { return ResponseEntity.ok().build(); }
//
//    @PostMapping
//    public ResponseEntity<EmployeeDto> create(@RequestBody EmployeeDto dto) { return ResponseEntity.ok().build(); }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<EmployeeDto> update(@PathVariable Long id, @RequestBody EmployeeDto dto) { return ResponseEntity.ok().build(); }
//
//    @PatchMapping("/{id}/status")
//    public ResponseEntity<Void> changeStatus(@PathVariable Long id, @RequestParam String status) { return ResponseEntity.ok().build(); }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) { return ResponseEntity.noContent().build(); }
//}
//

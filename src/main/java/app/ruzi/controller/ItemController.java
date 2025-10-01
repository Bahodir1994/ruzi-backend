package app.ruzi.controller;

import app.ruzi.configuration.messaging.HandlerService;
import app.ruzi.configuration.messaging.MessageResponse;
import app.ruzi.entity.app.Item;
import app.ruzi.service.app.item.ItemService;
import app.ruzi.service.payload.app.ItemDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/route-item")
@RequiredArgsConstructor
public class ItemController {

    private final HandlerService handlerService;
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestParam("Accept-language") String langType,
            @Valid @RequestBody ItemDto itemDto,
            BindingResult bindingResult
    ) {

        MessageResponse messageResponse = handlerService.handleRequest(
                () -> itemService.create(itemDto),
                bindingResult,
                langType
        );

        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }


    @PostMapping("/data-table-main")
    public ResponseEntity<Object> read_table_data(@RequestBody @Valid DataTablesInput dataTablesInput) {
        DataTablesOutput<Item> privilegeDataTablesOutput = itemService.readTableProduct(dataTablesInput);
        return new ResponseEntity<>(privilegeDataTablesOutput, HttpStatus.OK);
    }

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
}


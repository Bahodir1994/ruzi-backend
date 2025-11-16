package app.ruzi.controller;

import app.ruzi.configuration.annotation.auth.MethodInfo;
import app.ruzi.configuration.messaging.HandlerService;
import app.ruzi.configuration.messaging.MessageResponse;
import app.ruzi.entity.app.Item;
import app.ruzi.service.app.item.ItemService;
import app.ruzi.service.payload.app.ItemRequestDto;
import app.ruzi.service.payload.app.ItemRequestSimpleDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/route-item")
@RequiredArgsConstructor
public class ItemController {

    private final HandlerService handlerService;
    private final ItemService itemService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_ITEM_CREATE')")
    public ResponseEntity<Object> create(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @Valid @RequestBody ItemRequestDto itemRequestDto,
            BindingResult bindingResult
    ) {

        MessageResponse messageResponse = handlerService.handleRequest(
                () -> itemService.create(itemRequestDto),
                bindingResult,
                langType
        );

        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

    @PostMapping("/create-simple")
    @PreAuthorize("hasAuthority('ROLE_ITEM_CREATE')")
    public ResponseEntity<Object> create_simple(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @Valid @RequestBody ItemRequestSimpleDto itemRequestDto,
            BindingResult bindingResult
    ) {

        MessageResponse messageResponse = handlerService.handleRequest(
                () -> itemService.create_simple(itemRequestDto),
                bindingResult,
                langType
        );

        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ITEM_DELETE')")
    public ResponseEntity<?> deleteOne(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @PathVariable String id
    ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> itemService.delete(List.of(id)),
                langType
        );
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteMany(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @RequestBody Map<String, List<String>> req
    ) {
        List<String> ids = req.get("ids");

        MessageResponse messageResponse = handlerService.handleRequest(
                () -> itemService.delete(ids),
                langType
        );
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

    @PostMapping("/data-table-main")
    @PreAuthorize("hasAuthority('ROLE_ITEM_READ')")
    public ResponseEntity<Object> read_table_data(@RequestBody @Valid DataTablesInput dataTablesInput) {
        DataTablesOutput<Item> privilegeDataTablesOutput = itemService.readTableItem(dataTablesInput);
        return new ResponseEntity<>(privilegeDataTablesOutput, HttpStatus.OK);
    }
}


package app.ruzi.controller;

import app.ruzi.configuration.annotation.auth.MethodInfo;
import app.ruzi.configuration.messaging.HandlerService;
import app.ruzi.configuration.messaging.MessageResponse;
import app.ruzi.entity.app.Unit;
import app.ruzi.service.app.unit.UnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/route-unit")
@RequiredArgsConstructor
public class UnitController {

    private final HandlerService handlerService;
    private final UnitService unitService;

    @PostMapping("/data-table-main")
    @PreAuthorize("hasAuthority('ROLE_CAT_READ')")
    @MethodInfo(methodName = "read-unit-table")
    public ResponseEntity<Object> read_table_data(@RequestBody @Valid DataTablesInput dataTablesInput) {
        DataTablesOutput<Unit> privilegeDataTablesOutput = unitService.getUnit(dataTablesInput);
        return new ResponseEntity<>(privilegeDataTablesOutput, HttpStatus.OK);
    }

    @GetMapping("/data-list-main")
    @PreAuthorize("hasAuthority('ROLE_CAT_READ')")
    @MethodInfo(methodName = "read-unit-list")
    public ResponseEntity<Object> read_list_data(
            @RequestHeader(value = "Accept-Language", required = false) String langType) {
        MessageResponse messageResponse = handlerService.handleRequest(
                unitService::getUnitList,
                langType
        );
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }
}

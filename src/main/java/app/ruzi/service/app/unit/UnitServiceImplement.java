package app.ruzi.service.app.unit;

import app.ruzi.entity.app.Unit;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import java.util.List;

public interface UnitServiceImplement {

    DataTablesOutput<Unit> getUnit(DataTablesInput dataTablesInput);

    List<Unit> getUnitList();

}

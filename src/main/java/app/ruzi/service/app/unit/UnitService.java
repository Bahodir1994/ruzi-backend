package app.ruzi.service.app.unit;

import app.ruzi.entity.app.Unit;
import app.ruzi.repository.app.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitService implements UnitServiceImplement {
    private final UnitRepository unitRepository;

    @Override
    public DataTablesOutput<Unit> getUnit(DataTablesInput dataTablesInput) {
        return unitRepository.findAll(dataTablesInput);
    }

    @Override
    public List<Unit> getUnitList() {
        return unitRepository.findAll();
    }

}

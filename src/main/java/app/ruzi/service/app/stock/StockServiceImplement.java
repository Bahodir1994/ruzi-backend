package app.ruzi.service.app.stock;

import app.ruzi.entity.app.Stock;
import app.ruzi.service.payload.app.StockViewDto;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import java.util.List;

public interface StockServiceImplement {

    DataTablesOutput<StockViewDto> getStock(DataTablesInput dataTablesInput);

    List<Stock> getStockList();

}

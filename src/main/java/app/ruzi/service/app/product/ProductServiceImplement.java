package app.ruzi.service.app.product;

import app.ruzi.entity.app.Item;
import app.ruzi.service.payload.app.ProductDto;
import app.ruzi.service.payload.app.ProductRequestDto;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import java.util.List;

public interface ProductServiceImplement {
    /* Method*/
    void create(ProductRequestDto productRequestDto);

    ProductDto read(ProductRequestDto productRequestDto);

    void delete(List<String> idList);

    void update(ProductRequestDto productRequestDto);

    /* Tables */
    DataTablesOutput<Item> readTableProduct(DataTablesInput dataTablesInput);

}

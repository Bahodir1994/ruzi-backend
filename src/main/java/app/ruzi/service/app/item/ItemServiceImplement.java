package app.ruzi.service.app.item;

import app.ruzi.entity.app.Item;
import app.ruzi.service.payload.app.ItemDto;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import java.util.List;

public interface ItemServiceImplement {
    /* Method*/
    void create(ItemDto itemDto);

    ItemDto read(ItemDto itemDto);

    void delete(List<String> idList);

    void update(ItemDto itemDto);

    /* Tables */
    DataTablesOutput<Item> readTableProduct(DataTablesInput dataTablesInput);

}

package app.ruzi.service.app.item;

import app.ruzi.entity.app.Item;
import app.ruzi.service.payload.app.ItemDto;
import app.ruzi.service.payload.app.ItemRequestDto;
import app.ruzi.service.payload.app.ItemRequestSimpleDto;
import app.ruzi.service.payload.tasks.DocumentRequestDto;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import java.util.List;

public interface ItemServiceImplement {
    /* Method*/
    void create(ItemRequestDto itemRequestDto);

    void create_simple(ItemRequestSimpleDto itemRequestDto);

    void create_by_xlsx(DocumentRequestDto documentRequestDto);

    ItemDto read(ItemDto itemDto);

    void delete(List<String> idList);

    void update(ItemDto itemDto);

    List<Item> search(String query);

    /* Tables */
    DataTablesOutput<Item> readTableItem(DataTablesInput dataTablesInput);

}

package app.ruzi.service.app.item;

import app.ruzi.entity.app.Item;
import app.ruzi.repository.app.ItemRepository;
import app.ruzi.service.mappers.ItemMapper;
import app.ruzi.service.payload.app.ItemRequestDto;
import app.ruzi.service.payload.app.ItemDto;
import app.ruzi.service.payload.app.ItemRequestSimpleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService implements ItemServiceImplement {
    private final ItemRepository itemRepository;
    private final ItemCodeGeneratorService generator;
    private final BarcodeGeneratorService barcodeGenerator;

    @Override
    @Transactional
    public void create(ItemRequestDto itemRequestDto) {
        Item productEntity = ItemMapper.INSTANCE.toEntity(itemRequestDto);
        itemRepository.save(productEntity);
    }

    @Override
    public void create_simple(ItemRequestSimpleDto itemRequestDto) {
        Item productEntity = ItemMapper.INSTANCE.toEntitySimple(itemRequestDto);

        String code = generator.generatePluCode();
        productEntity.setCode(code);

        String sku = generator.generateSkuCode();
        productEntity.setSkuCode(sku);
        productEntity.setInternalSkuNumber(extractNumber(sku));

        productEntity.setBarcode(barcodeGenerator.generateBarcode());

        itemRepository.save(productEntity);
    }

    @Override
    public ItemDto read(ItemDto itemDto) {
        Optional<Item> productEntity = itemRepository.findById(itemDto.getId());
        return productEntity.map(ItemMapper.INSTANCE::toDto).orElse(null);
    }

    @Override
    public void delete(List<String> idList) {
        itemRepository.deleteAllByIdList(idList);
    }

    @Override
    public void update(ItemDto itemDto) {
        itemRepository.findById(itemDto.getId()).ifPresent(product -> {
            ItemMapper.INSTANCE.partialUpdate(itemDto, product);
            itemRepository.save(product);
        });
    }

    @Override
    public DataTablesOutput<Item> readTableProduct(DataTablesInput dataTablesInput) {
        return itemRepository.findAll(dataTablesInput);
    }

    private Integer extractNumber(String sku) {
        return Integer.valueOf(sku.substring(sku.lastIndexOf("-") + 1));
    }
}

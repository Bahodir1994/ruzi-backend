package app.ruzi.service.app.item;

import app.ruzi.configuration.exception.CustomValidationException;
import app.ruzi.entity.app.Item;
import app.ruzi.repository.app.ItemRepository;
import app.ruzi.repository.app.PurchaseOrderItemRepository;
import app.ruzi.service.mappers.ItemMapper;
import app.ruzi.service.payload.app.ItemDto;
import app.ruzi.service.payload.app.ItemRequestDto;
import app.ruzi.service.payload.app.ItemRequestSimpleDto;
import app.ruzi.service.payload.app.ItemXlsxRequestDto;
import app.ruzi.service.payload.tasks.DocumentRequestDto;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService implements ItemServiceImplement {
    @Autowired
    private Validator validator;

    private final ItemRepository itemRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;

    private final ItemCodeGeneratorService generator;
    private final BarcodeGeneratorService barcodeGenerator;

    @Override
    @Transactional
    public void create(ItemRequestDto itemRequestDto) {
        Item productEntity = ItemMapper.INSTANCE.toEntity(itemRequestDto);
        itemRepository.save(productEntity);
    }

    @Override
    @Transactional
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
    @Transactional
    public void create_by_xlsx(DocumentRequestDto request) {

        MultipartFile file = request.getMultipartFile();
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ERROR0002");
        }

        ItemListener listener = new ItemListener(
                dtos -> {
                    var entities = dtos.stream()
                            .map(ItemMapper.INSTANCE::toEntityXlsx)
                            .toList();
                    itemRepository.saveAll(entities);
                },
                validator
        );

        try (InputStream is = file.getInputStream()) {

            EasyExcel.read(is, ItemXlsxRequestDto.class, listener)
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet(0)
                    .doRead();

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ERROR0003");
        }

        if (!listener.getValidationErrors().isEmpty()) {
            throw new CustomValidationException(
                    listener.getValidationErrors(),
                    "ERROR0004",
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto read(ItemDto itemDto) {
        Optional<Item> productEntity = itemRepository.findById(itemDto.getId());
        return productEntity.map(ItemMapper.INSTANCE::toDto).orElse(null);
    }

    @Override
    @Transactional
    public void delete(List<String> idList) {

        List<String> usedItemIds = purchaseOrderItemRepository.findUsedItemIds(idList);

        List<String> notUsedItems = idList.stream()
                .filter(id -> !usedItemIds.contains(id))
                .toList();

        itemRepository.deleteAllByIdList(notUsedItems);

        if (!usedItemIds.isEmpty()) {
            itemRepository.softDelete(usedItemIds);
        }
    }

    @Override
    @Transactional
    public void update(ItemDto itemDto) {
        itemRepository.findById(itemDto.getId()).ifPresent(product -> {
            ItemMapper.INSTANCE.partialUpdate(itemDto, product);
            itemRepository.save(product);
        });
    }

    @Override
    public List<Item> search(String query) {
        return itemRepository.searchItem(query);
    }

    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<Item> readTableItem(DataTablesInput input) {
        Specification<Item> spec = (root, query, cb) ->
                cb.isFalse(root.get("isDeleted"));

        return itemRepository.findAll(input, spec);
    }


    private Integer extractNumber(String sku) {
        return Integer.valueOf(sku.substring(sku.lastIndexOf("-") + 1));
    }
}

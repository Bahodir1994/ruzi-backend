package app.ruzi.service.app.product;

import app.ruzi.configuration.datatables.DateRangeSpecification;
import app.ruzi.configuration.datatables.DynamicFilterSpecification;
import app.ruzi.entity.app.Product;
import app.ruzi.repository.app.ProductRepository;
import app.ruzi.service.mappers.ProductMapper;
import app.ruzi.service.payload.app.ProductDto;
import app.ruzi.service.payload.app.ProductRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements ProductServiceImplement{
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void create(ProductRequestDto productRequestDto) {
        Product productEntity = ProductMapper.INSTANCE.toEntity(productRequestDto);
        productRepository.save(productEntity);
    }

    @Override
    public ProductDto read(ProductRequestDto productRequestDto) {
        Optional<Product> productEntity = productRepository.findById(productRequestDto.getId());
        return productEntity.map(ProductMapper.INSTANCE::toDto).orElse(null);
    }

    @Override
    public void delete(List<String> idList) {
        productRepository.deleteAllByIdList(idList);
    }

    @Override
    public void update(ProductRequestDto productRequestDto) {
        productRepository.findById(productRequestDto.getId()).ifPresent(product -> {
            ProductMapper.INSTANCE.partialUpdate(productRequestDto, product);
            productRepository.save(product);
        });
    }

    @Override
    public DataTablesOutput<Product> readTableProduct(DataTablesInput dataTablesInput) {
        List<Specification<Product>> specs = new ArrayList<>();
        dataTablesInput.getColumns().forEach(column -> {
            String searchValue = column.getSearch().getValue();
            if (column.getSearchable() && searchValue != null && !searchValue.isBlank()) {
                specs.add(new DynamicFilterSpecification<>(column.getData(), searchValue));
                column.getSearch().setValue("");
            }
        });

        Specification<Product> dateSpec = new DateRangeSpecification<>(
                dataTablesInput, "insTime", "insTime"
        );
        for (Specification<Product> spec : specs) {
            dateSpec = dateSpec.and(spec);
        }

        return productRepository.findAll(dataTablesInput, dateSpec);
    }
}

package app.ruzi.service.app.customer;

import app.ruzi.configuration.jwt.JwtUtils;
import app.ruzi.entity.app.Customer;
import app.ruzi.entity.app.Item;
import app.ruzi.repository.app.CustomerRepository;
import app.ruzi.service.mappers.CustomerMapper;
import app.ruzi.service.payload.app.CustomerDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final JwtUtils jwtUtils;


    private final CustomerRepository customerRepository;

    /**
     * client boyicha barcha mijozlarni berish
     */
    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * cart boyicha barcha mijoz berish
     */
    @Transactional(readOnly = true)
    public Customer getCustomerByCart(String byId) {
        return customerRepository.findById(byId).orElse(null);
    }

    @Transactional
    public void save(CustomerDto customerDto) {
        Customer entity = CustomerMapper.INSTANCE.toEntity(customerDto);
        entity.setClientCode(generateEasyClientCode(customerDto.getFullName()));
        customerRepository.save(entity);
    }

    private String generateEasyClientCode(String fullName) {
        String prefix = fullName
                .replaceAll("[^A-Za-zА-Яа-яЎўҚқҒғҲҳ]", "")
                .toUpperCase();
        if (prefix.length() >= 3) {
            prefix = prefix.substring(0, 3);
        }
        int number = (int) (Math.random() * 900) + 100; // 100–999
        return prefix + number;
    }

    @Transactional(readOnly = true)
    public DataTablesOutput<Customer> dataTableMain(DataTablesInput input) {
        Specification<Customer> spec = (root, query, cb) ->
                cb.isFalse(root.get("isDeleted"));

        return customerRepository.findAll(input, spec);
    }
}

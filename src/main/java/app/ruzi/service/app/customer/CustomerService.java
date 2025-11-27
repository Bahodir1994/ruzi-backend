package app.ruzi.service.app.customer;

import app.ruzi.configuration.jwt.JwtUtils;
import app.ruzi.entity.app.Customer;
import app.ruzi.repository.app.CustomerRepository;
import app.ruzi.service.mappers.CustomerMapper;
import app.ruzi.service.payload.app.CustomerDto;
import lombok.RequiredArgsConstructor;
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
}

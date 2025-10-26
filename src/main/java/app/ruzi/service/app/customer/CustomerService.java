package app.ruzi.service.app.customer;

import app.ruzi.configuration.jwt.JwtUtils;
import app.ruzi.entity.app.Customer;
import app.ruzi.repository.app.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {
    private final JwtUtils jwtUtils;

    private final CustomerRepository customerRepository;

    /**
     * client boyicha barcha mijozlarni berish
     */
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}

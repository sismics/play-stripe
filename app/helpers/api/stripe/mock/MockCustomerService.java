package helpers.api.stripe.mock;

import com.stripe.model.Customer;
import helpers.api.stripe.service.CustomerService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author jtremeaux
 */
public class MockCustomerService {
    private static Map<String, Customer> customerMap = new HashMap<>();

    /**
     * Create a mock of CustomerService.
     *
     * @return The mock
     */
    public static CustomerService create() {
        CustomerService customerService = mock(CustomerService.class);

        when(customerService.createCustomer(any(UUID.class), any(String.class))).thenAnswer(i -> {
            UUID userId = i.getArgument(0);
            String userEmail = i.getArgument(0);

            Customer customer = new Customer();
            customer.setId(userId.toString());
            customer.setEmail(userEmail);
            customerMap.put(userId.toString(), customer);

            return customer;
        });

        when(customerService.getCustomer(any(String.class))).thenAnswer(i -> {
            String id = i.getArgument(0);
            return customerMap.get(id);
        });

        return customerService;
    }
}

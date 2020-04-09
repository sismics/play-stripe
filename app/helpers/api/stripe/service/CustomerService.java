package helpers.api.stripe.service;

import com.stripe.model.Card;
import com.stripe.model.Customer;
import helpers.api.stripe.StripeApi;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author jtremeaux
 */
public class CustomerService {
    public StripeApi stripeApi;

    public CustomerService(StripeApi stripeApi) {
        this.stripeApi = stripeApi;
    }

    /**
     * Create a Stripe customer.
     *
     * @param userId The user ID
     * @param userEmail The user email
     * @return The updated user
     */
    public Customer createCustomer(UUID userId, String userEmail) {
        Map<String, Object> customerParams = new HashMap<>();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("id", userId);
        customerParams.put("metadata", metadata);
        customerParams.put("email", userEmail);
        try {
            return Customer.create(customerParams, stripeApi.getRequestOptions());
        } catch (Exception e) {
            throw new RuntimeException("Error creating Stripe customer: " + userEmail, e);
        }
    }

    /**
     * Update the customer's default source.
     *
     * @param customer The customer to update
     * @param card The card
     */
    public void updateDefaultSource(Customer customer, Card card) {
        Map<String, Object> customerParams = new HashMap<>();
        customerParams.put("default_source", card.getId());
        try {
            customer.update(customerParams, stripeApi.getRequestOptions());
        } catch (Exception e) {
            throw new RuntimeException("Error updating the user's default source: " + customer.getId(), e);
        }
    }

    /**
     * Return the customer.
     *
     * @param id The customer id
     * @return The customer
     */
    public Customer getCustomer(String id) {
        try {
            return Customer.retrieve(id, stripeApi.getRequestOptions());
        } catch (Exception e) {
            throw new RuntimeException("Error creating customer: " + id, e);
        }
    }
}

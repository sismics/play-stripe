package helpers.api.stripe.service;

import com.stripe.model.*;
import helpers.api.stripe.StripeApi;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jtremeaux
 */
public class SubscriptionService {
    public StripeApi stripeApi;

    public SubscriptionService(StripeApi stripeApi) {
        this.stripeApi = stripeApi;
    }

    /**
     * Create a new subscription.
     * This is used to create the 1st subscription only (with a 30day trial period).
     *
     * @param customer The customer to subscribe
     * @param planCode The plan code
     * @param trialPeriod The plan trial period
     * @param taxRate The tax rate
     */
    public Subscription createSubscription(Customer customer, String planCode, Integer trialPeriod, BigDecimal taxRate) {
        Map<String, Object> subCreateParams = new HashMap<>();
        HashMap<String, Object> items = new HashMap<>();
        HashMap<String, Object> item = new HashMap<>();

        item.put("plan", planCode);
        item.put("quantity", 1);
        items.put("0", item);

        subCreateParams.put("items", items);
        subCreateParams.put("customer", customer.getId());
        if (trialPeriod != null) {
            subCreateParams.put("trial_period_days", trialPeriod);
        }
        subCreateParams.put("tax_percent", taxRate.doubleValue());
        try {
            return Subscription.create(subCreateParams, stripeApi.getRequestOptions());
        } catch (Exception e) {
            throw new RuntimeException("Error creating a subscription: " + customer.getId(), e);
        }
    }

    /**
     * Delete a subscription.
     *
     * @param subscription The subscription
     * @return The cancelled subscription
     */
    public Subscription cancelSubscription(Subscription subscription) {
        Map<String, Object> params = new HashMap<>();
        try {
            return subscription.cancel(params, stripeApi.getRequestOptions());
        } catch (Exception e) {
            throw new RuntimeException("Error deleting a subscription: " + subscription.getId(), e);
        }
    }

    /**
     * Retrieve a subscription.
     *
     * @param id The subscription ID
     * @return The subscription
     */
    public Subscription getSubscription(String id) {
        try {
            return Subscription.retrieve(id, stripeApi.getRequestOptions());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving a subscription: " + id, e);
        }
    }

    /**
     * Retrieve a subscription item.
     *
     * @param id The subscription item ID
     * @return The subscription item
     */
    public SubscriptionItem getSubscriptionItem(String id) {
        try {
            return SubscriptionItem.retrieve(id, stripeApi.getRequestOptions());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving a subscription item: " + id, e);
        }
    }

    /**
     * Retrieve all subscriptions of a customer.
     *
     * @param customerId The customer ID
     * @return The list of subscriptions
     */
    public SubscriptionCollection findSubscriptionByCustomer(String customerId) {
        Map<String, Object> params = new HashMap<>();
        params.put("customer", customerId);
        params.put("limit", 10);
        try {
            return Subscription.list(params, stripeApi.getRequestOptions());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving a subscriptions for customer: " + customerId, e);
        }
    }

    /**
     * Upgrade the tax percent in a subscription.
     *
     * @param subscription The subscription
     * @param taxRate The new tax rate
     * @return The updated subscription
     */
    public Subscription updateSubscriptionTaxRate(Subscription subscription, BigDecimal taxRate) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("tax_percent", taxRate.doubleValue());
        try {
            return subscription.update(params, stripeApi.getRequestOptions());
        } catch (Exception e) {
            throw new RuntimeException("Error updating a subscription: " + subscription.getId(), e);
        }
    }

    /**
     * End the trial (mock only).
     *
     * @param subscription The subscription
     * @return The updated subscription
     */
    public Subscription updateSubscriptionEndTrial(Subscription subscription) {
        return subscription;
    }

    /**
     * Upgrade a plan in a subscription item.
     *
     * @param subscriptionItem The subscription item
     * @param planCode The new plan code
     * @return The updated subscription item
     */
    public SubscriptionItem updateSubscriptionItemPlan(SubscriptionItem subscriptionItem, String planCode) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("plan", planCode);
        try {
            return subscriptionItem.update(params, stripeApi.getRequestOptions());
        } catch (Exception e) {
            throw new RuntimeException("Error updating a subscription item: " + subscriptionItem.getId(), e);
        }
    }

    /**
     * Upgrade the quantity in a subscription item.
     *
     * @param subscriptionItem The subscription item
     * @param quantity The new quantity
     * @return The updated subscription item
     */
    public SubscriptionItem updateSubscriptionItemQuantity(SubscriptionItem subscriptionItem, Integer quantity) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("quantity", quantity);
        try {
            return subscriptionItem.update(params, stripeApi.getRequestOptions());
        } catch (Exception e) {
            throw new RuntimeException("Error updating a subscription item: " + subscriptionItem.getId(), e);
        }
    }

    /**
     * Create an invoice for a subscription (Mock only)
     *
     * @param subscription The subscription
     * @return The invoice
     */
    public Invoice createInvoice(Subscription subscription) {
        // NOP
        return null;
    }
}

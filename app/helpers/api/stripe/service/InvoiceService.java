package helpers.api.stripe.service;

import com.stripe.model.Invoice;
import com.stripe.model.InvoiceCollection;
import helpers.api.stripe.StripeApi;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jtremeaux
 */
public class InvoiceService {
    public StripeApi stripeApi;

    public InvoiceService(StripeApi stripeApi) {
        this.stripeApi = stripeApi;
    }

    /**
     * Get the last invoice, if any.
     *
     * @param customerId The customer ID
     * @return The invoice
     */
    public Invoice getLastInvoice(String customerId) {
        Map<String, Object> params = new HashMap<>();
        params.put("limit", 1);
        params.put("customer", customerId);
        try {
            InvoiceCollection collection = Invoice.list(params, stripeApi.getRequestOptions());
            if (collection != null && collection.getData().size() > 0) {
                return collection.getData().get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving last invoice for customer: " + customerId, e);
        }
    }

    /**
     * Retrieve an invoice.
     *
     * @param id The invoice ID
     * @return The invoice
     */
    public Invoice retrieve(String id) {
        try {
            return Invoice.retrieve(id, stripeApi.getRequestOptions());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving invoice: " + id, e);
        }
    }

    /**
     * Retry the payment of an invoice.
     *
     * @param invoice The invoice
     */
    public void pay(Invoice invoice) {
        Map<String, Object> params = new HashMap<>();
        try {
            invoice.pay(params, stripeApi.getRequestOptions());
        } catch (Exception e) {
            throw new RuntimeException("Error trying to pay an invoice: " + invoice.getId(), e);
        }
    }

    /**
     * Generate an invoice immediately, without waiting for the next billing period.
     * This is needed when upgrading the plan.
     *
     * @param customerId The customer ID
     * @param taxRate The tax rate to apply
     * @return The invoice
     */
    public Invoice generateInvoice(String customerId, BigDecimal taxRate) {
        Map<String, Object> params = new HashMap<>();
        params.put("customer", customerId);
        params.put("tax_percent", taxRate.doubleValue());

        try {
            return Invoice.create(params, stripeApi.getRequestOptions());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving invoices for customer: " + customerId, e);
        }
    }

    /**
     * Create a new invoice (for Mocking only).
     *
     * @param invoice The invoice to create
     * @return invoice The created invoice
     */
    public Invoice createInvoice(Invoice invoice) {
        return null;
    }
}

package helpers.api.stripe.service;

import com.stripe.model.BalanceTransaction;
import com.stripe.model.BalanceTransactionCollection;
import helpers.api.stripe.StripeApi;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jtremeaux
 */
public class BalanceTransactionService {
    public StripeApi stripeApi;

    public BalanceTransactionService(StripeApi stripeApi) {
        this.stripeApi = stripeApi;
    }

    /**
     * Retrieve the balanceTransactions.
     *
     * @param type The balanceTransaction type
     * @param limit The pagination limit
     * @return The list of subscriptions
     */
    public BalanceTransactionCollection findBalanceTransactionByType(String type, Integer limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);
        params.put("limit", limit);
        try {
            return BalanceTransaction.list(params, stripeApi.getRequestOptions());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving balance transactions", e);
        }
    }
}

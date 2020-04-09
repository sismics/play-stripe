package helpers.api.stripe.mock;

import helpers.api.stripe.service.BalanceTransactionService;

import static org.mockito.Mockito.mock;

/**
 * @author jtremeaux
 */
public class MockBalanceTransactionService {
    /**
     * Create a mock of BalanceTransactionService.
     *
     * @return The mock
     */
    public static BalanceTransactionService create() {
        BalanceTransactionService balanceTransactionService = mock(BalanceTransactionService.class);

        return balanceTransactionService;
    }
}

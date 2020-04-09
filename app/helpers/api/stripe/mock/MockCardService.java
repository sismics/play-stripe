package helpers.api.stripe.mock;

import com.stripe.model.*;
import helpers.api.stripe.StripeApi;
import helpers.api.stripe.service.CardService;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author jtremeaux
 */
public class MockCardService {
    /**
     * Customer ID -> Card map.
     */
    private static Map<String, Card> cardMap = new HashMap<>();

    /**
     * Create a mock of CardService.
     *
     * @return The mock
     */
    public static CardService create() {
        CardService cardService = mock(CardService.class);

        when(cardService.create(any(Customer.class), any(String.class))).thenAnswer(i -> {
            Customer customer = i.getArgument(0);
            Card card = new Card();
            card.setId(customer.getId());
            card.setCustomer(customer.getId());
            cardMap.put(customer.getId(), card);
            if (customer.getDefaultSource() == null) {
                // Generate invoices on first subscription
                for (Subscription subscription : StripeApi.get().getSubscriptionService().findSubscriptionByCustomer(customer.getId()).getData()) {
                    Invoice invoice = StripeApi.get().getSubscriptionService().createInvoice(subscription);

                    StripeApi.get().getInvoiceService().createInvoice(invoice);
                }
            }
            customer.setDefaultSource(card.getId());
            return card;
        });

        when(cardService.getCardInfo(any(Customer.class), any(String.class))).thenAnswer(i -> {
            Customer customer = i.getArgument(0);
            return cardMap.get(customer.getId());
        });

        doAnswer(i -> {
            ExternalAccount externalAccount = i.getArgument(0);
            cardMap.remove(externalAccount.getCustomer());
            StripeApi.get().getCustomerService().getCustomer(externalAccount.getCustomer()).setDefaultSource(null);
            return null;
        }).when(cardService).deleteExternalAccount(any(ExternalAccount.class));

        return cardService;
    }
}

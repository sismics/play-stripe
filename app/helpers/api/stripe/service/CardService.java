package helpers.api.stripe.service;

import com.stripe.model.Card;
import com.stripe.model.Customer;
import com.stripe.model.ExternalAccount;
import helpers.api.stripe.StripeApi;

/**
 * @author jtremeaux
 */
public class CardService {
    public StripeApi stripeApi;

    public CardService(StripeApi stripeApi) {
        this.stripeApi = stripeApi;
    }

    /**
     * Create a new card.
     *
     * @param customer The customer
     * @param token The token
     * @return The card
     */
    public Card create(Customer customer, String token) {
        try {
            return customer.createCard(token, stripeApi.getSecretKey());
        } catch (Exception e) {
            throw new RuntimeException("Error creating card: " + customer.getId(), e);
        }
    }

    /**
     * Get the card info.
     *
     * @param customer The customer
     * @param id The ID of the card
     * @return The card info
     */
    public ExternalAccount getCardInfo(Customer customer, String id) {
        try {
            return customer.getSources().retrieve(id, stripeApi.getRequestOptions());
        } catch (Exception e) {
            throw new RuntimeException("Error getting card info: " + customer.getId() + ", card: " + id, e);
        }
    }

    /**
     * Delete a card.
     *
     * @param externalAccount The card to delete
     */
    public void deleteExternalAccount(ExternalAccount externalAccount) {
        try {
            externalAccount.delete(stripeApi.getRequestOptions());
        } catch (Exception e) {
            throw new RuntimeException("Error deleting external account: " + externalAccount.getId(), e);
        }
    }
}

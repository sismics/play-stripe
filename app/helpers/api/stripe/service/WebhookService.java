package helpers.api.stripe.service;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import helpers.api.stripe.StripeApi;
import play.Play;

/**
 * @author jtremeaux
 */
public class WebhookService {
    public StripeApi stripeApi;

    public WebhookService(StripeApi stripeApi) {
        this.stripeApi = stripeApi;
    }

    /**
     * Returns the Webhook event.
     *
     * @param payload The payload
     * @param signature The signature
     */
    public Event parseEvent(String payload, String signature) {
        if (Play.mode.isDev()) {
            return Event.PRETTY_PRINT_GSON.fromJson(payload, Event.class);
        }

        try {
            return Webhook.constructEvent(payload, signature, StripeApi.getWebhookSecretKey());
        } catch (SignatureVerificationException e) {
            throw new RuntimeException("Error decoding webhook payload", e);
        }
    }
}

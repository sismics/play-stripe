package helpers.api.stripe.mock;

import com.stripe.model.Event;
import helpers.api.stripe.service.WebhookService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author jtremeaux
 */
public class MockWebhookService {
    /**
     * Create a mock of WebhookService.
     *
     * @return The mock
     */
    public static WebhookService create() {
        WebhookService webhookService = mock(WebhookService.class);

        when(webhookService.parseEvent(any(String.class), any(String.class))).thenAnswer( i -> {
            String payload = i.getArgument(0);
            return Event.PRETTY_PRINT_GSON.fromJson(payload, Event.class);
        });

        return webhookService;
    }
}

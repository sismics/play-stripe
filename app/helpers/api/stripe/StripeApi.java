package helpers.api.stripe;

import com.stripe.net.RequestOptions;
import helpers.api.stripe.mock.*;
import helpers.api.stripe.service.*;
import play.Play;

/**
 * @author jtremeaux
 */
public class StripeApi {
    private static StripeApi instance;

    private RequestOptions requestOptions;

    private CustomerService customerService;

    private CardService cardService;

    private InvoiceService invoiceService;

    private PlanService planService;

    private WebhookService webhookService;

    private SubscriptionService subscriptionService;

    public static StripeApi get() {
        if (instance == null) {
            instance = new StripeApi();
        }
        return instance;
    }

    public StripeApi() {
        requestOptions = createRequestOptions();
        if (isMock()) {
            customerService = MockCustomerService.create();
            cardService = MockCardService.create();
            invoiceService = MockInvoiceService.create();
            planService = MockPlanService.create();
            subscriptionService = MockSubscriptionService.create();
            webhookService = MockWebhookService.create();
        } else {
            customerService = new CustomerService(this);
            cardService = new CardService(this);
            invoiceService = new InvoiceService(this);
            planService = new PlanService(this);
            subscriptionService = new SubscriptionService(this);
            webhookService = new WebhookService(this);
        }
    }

    private static RequestOptions createRequestOptions() {
        return new RequestOptions.RequestOptionsBuilder()
                .setApiKey(getSecretKey())
                .build();
    }

    public boolean isMock() {
        return Boolean.parseBoolean(Play.configuration.getProperty("stripe.mock", "false"));
    }

    public static String getSecretKey() {
        return Play.configuration.getProperty("stripe.secret_key");
    }

    public static String getWebhookSecretKey() {
        return Play.configuration.getProperty("stripe.webhook.secret_key");
    }

    public RequestOptions getRequestOptions() {
        return requestOptions;
    }

    public CustomerService getCustomerService() {
        return customerService;
    }

    public CardService getCardService() {
        return cardService;
    }

    public InvoiceService getInvoiceService() {
        return invoiceService;
    }

    public PlanService getPlanService() {
        return planService;
    }

    public SubscriptionService getSubscriptionService() {
        return subscriptionService;
    }

    public WebhookService getWebhookService() {
        return webhookService;
    }
}

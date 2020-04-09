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

    private BalanceTransactionService balanceTransactionService;

    private CardService cardService;

    private CustomerService customerService;

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
            balanceTransactionService = MockBalanceTransactionService.create();
            cardService = MockCardService.create();
            customerService = MockCustomerService.create();
            invoiceService = MockInvoiceService.create();
            planService = MockPlanService.create();
            subscriptionService = MockSubscriptionService.create();
            webhookService = MockWebhookService.create();
        } else {
            balanceTransactionService = new BalanceTransactionService(this);
            cardService = new CardService(this);
            customerService = new CustomerService(this);
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

    public BalanceTransactionService getBalanceTransactionService() {
        return balanceTransactionService;
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

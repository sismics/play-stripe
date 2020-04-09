package helpers.api.stripe.mock;

import com.sismics.sapparot.reflection.ReflectionUtil;
import com.stripe.model.*;
import helpers.api.stripe.StripeApi;
import helpers.api.stripe.service.SubscriptionService;
import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;
import play.libs.Codec;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author jtremeaux
 */
public class MockSubscriptionService {
    private static Map<String, Subscription> subscriptionMap = new HashMap<>();

    private static Map<String, SubscriptionItem> subscriptionItemMap = new HashMap<>();

    /**
     * Create a mock of SubscriptionService.
     *
     * @return The mock
     */
    public static SubscriptionService create() {
        SubscriptionService subscriptionService = mock(SubscriptionService.class);

        when(subscriptionService.getSubscription(any(String.class))).thenAnswer(i -> {
            String subscriptionId = i.getArgument(0);
            return subscriptionMap.get(subscriptionId);
        });

        when(subscriptionService.getSubscriptionItem(any(String.class))).thenAnswer(i -> {
            String subscriptionItemId = i.getArgument(0);
            return subscriptionItemMap.get(subscriptionItemId);
        });

        when(subscriptionService.findSubscriptionByCustomer(any(String.class))).thenAnswer(i -> {
            String customerId = i.getArgument(0);
            SubscriptionCollection collection = new SubscriptionCollection();
            collection.setData(subscriptionMap.values().stream()
                    .filter(e -> e.getCustomer().equals(customerId))
                    .collect(Collectors.toList()));
            return collection;
        });

        when(subscriptionService.createSubscription(any(Customer.class), any(String.class), nullable(Integer.class), any(BigDecimal.class))).thenAnswer(i -> {
            Customer customer = i.getArgument(0);
            String planCode = i.getArgument(1);
            Integer trialPeriod = i.getArgument(2);
            BigDecimal taxRate = i.getArgument(3);
            Subscription subscription = new Subscription();
            subscription.setId(Codec.UUID());
            subscription.setStatus("trialing");
            subscription.setCustomer(customer.getId());
            subscription.setTaxPercent(taxRate.doubleValue());
            subscription.setCurrentPeriodStart(new Date().getTime() / 1000);
            subscription.setCurrentPeriodEnd(DateTime.now().withFieldAdded(DurationFieldType.months(), 1).toDate().getTime() / 1000);
            com.stripe.model.Plan stripePlan = StripeApi.get().getPlanService().getPlan(planCode);
            subscription.setPlan(stripePlan);
            ArrayList<SubscriptionItem> items = new ArrayList<>();
            SubscriptionItem subscriptionItem = new SubscriptionItem();
            subscriptionItem.setId(Codec.UUID());
            subscriptionItem.setQuantity(1);
            subscriptionItem.setPlan(stripePlan);
            subscriptionItem.setObject(subscription.getId());
            items.add(subscriptionItem);
            subscriptionItemMap.put(subscriptionItem.getId(), subscriptionItem);
            SubscriptionItemCollection collection = new SubscriptionItemCollection();
            collection.setData(items);
            subscription.setSubscriptionItems(collection);
            subscriptionMap.put(subscription.getId(), subscription);

            return subscription;
        });

        when(subscriptionService.createInvoice(any(Subscription.class))).thenAnswer(i -> {
            Subscription subscription = i.getArgument(0);

            Invoice invoice = new Invoice();
            Customer customer = StripeApi.get().getCustomerService().getCustomer(subscription.getCustomer());
            invoice.setCustomer(customer.getId());
            invoice.setSubscription(subscription.getId());
            if ("trialing".equalsIgnoreCase(subscription.getStatus())) {
                invoice.setSubtotal(0L);
            } else {
                invoice.setSubtotal(subscription.getPlan().getAmount());
            }
            invoice.setTax((long) (invoice.getSubtotal() * subscription.getTaxPercent() / 100.d));
            invoice.setTaxPercent(subscription.getTaxPercent());
            invoice.setTotal(invoice.getSubtotal() + invoice.getTax());
            DateTime time = DateTime.now().withTimeAtStartOfDay();
            invoice.setPeriodStart(time.toDate().getTime() / 1000);
            invoice.setPeriodEnd(time.withFieldAdded(DurationFieldType.months(), 1).toDate().getTime() / 1000);

            InvoiceLineItemCollection collection = new InvoiceLineItemCollection();
            List<InvoiceLineItem> invoiceLineItemList = new ArrayList<>();
            InvoiceLineItem invoiceLineItem = new InvoiceLineItem();
            ReflectionUtil.setFieldValue(invoiceLineItem, "id", subscription.getId());
            ReflectionUtil.setFieldValue(invoiceLineItem, "plan", subscription.getPlan());
            ReflectionUtil.setFieldValue(invoiceLineItem, "quantity", 1);
            ReflectionUtil.setFieldValue(invoiceLineItem, "amount", invoice.getTotal());
            InvoiceLineItemPeriod period = new InvoiceLineItemPeriod();
            period.setStart(invoice.getPeriodStart());
            period.setEnd(invoice.getPeriodEnd());
            ReflectionUtil.setFieldValue(invoiceLineItem, "period", period);
            invoiceLineItemList.add(invoiceLineItem);
            collection.setData(invoiceLineItemList);
            ReflectionUtil.setFieldValue(invoice, "lines", collection);

            return invoice;
        });

        when(subscriptionService.updateSubscriptionTaxRate(any(Subscription.class), any(BigDecimal.class))).thenAnswer(i -> {
            Subscription subscription = i.getArgument(0);
            BigDecimal taxRate = i.getArgument(1);
            subscription.setTaxPercent(taxRate.doubleValue());
            return subscription;
        });

        when(subscriptionService.updateSubscriptionEndTrial(any(Subscription.class))).thenAnswer(i -> {
            Subscription subscription = i.getArgument(0);
            subscription.setStatus("active");

            Invoice invoice = StripeApi.get().getSubscriptionService().createInvoice(subscription);
            StripeApi.get().getInvoiceService().createInvoice(invoice);

            return subscription;
        });

        when(subscriptionService.updateSubscriptionItemPlan(any(SubscriptionItem.class), any(String.class))).thenAnswer(i -> {
            SubscriptionItem subscriptionItem = i.getArgument(0);
            String planCode = i.getArgument(1);

            Subscription subscription = subscriptionMap.get(subscriptionItem.getObject());
            com.stripe.model.Plan stripePlan = StripeApi.get().getPlanService().getPlan(planCode);
            subscription.setPlan(stripePlan);
            subscriptionItem.setPlan(stripePlan);

            return subscriptionItem;
        });

        return subscriptionService;
    }
}

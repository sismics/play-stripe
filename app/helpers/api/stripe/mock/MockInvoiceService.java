package helpers.api.stripe.mock;

import com.stripe.model.Invoice;
import helpers.api.stripe.service.InvoiceService;
import play.libs.Codec;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author jtremeaux
 */
public class MockInvoiceService {
    private static Map<String, Invoice> invoiceMap = new HashMap<>();

    public static Consumer<Invoice> onCreateInvoice;

    /**
     * Create a mock of InvoiceService.
     *
     * @return The mock
     */
    public static InvoiceService create() {
        InvoiceService invoiceService = mock(InvoiceService.class);

        when(invoiceService.retrieve(any(String.class))).thenAnswer(i -> {
            String id = i.getArgument(0);
            return invoiceMap.get(id);
        });

        doAnswer(i -> {
            Invoice invoice = i.getArgument(0);
            invoice.setId(Codec.UUID());
            invoice.setNumber(Codec.UUID());
            invoice.setDate(new Date().getTime() / 1000);
            invoiceMap.put(invoice.getId(), invoice);
            if (onCreateInvoice != null) {
                onCreateInvoice.accept(invoice);
            }
            return null;
        }).when(invoiceService).createInvoice(any(Invoice.class));

        return invoiceService;
    }
}

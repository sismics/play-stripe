package helpers.api.stripe.service;

import com.stripe.exception.InvalidRequestException;
import helpers.api.stripe.StripeApi;
import play.Logger;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author jtremeaux
 */
public class PlanService {
    public StripeApi stripeApi;

    public PlanService(StripeApi stripeApi) {
        this.stripeApi = stripeApi;
    }

    /**
     * Create a new plan.
     *
     * @param id The business model ID
     * @param code The plan code
     * @param name The plan name
     * @param price The plan price
     * @return The plan
     */
    public com.stripe.model.Plan create(UUID id, String code, String name, BigDecimal price) {
        try {
            try {
                com.stripe.model.Plan currentPlan = com.stripe.model.Plan.retrieve(code, stripeApi.getRequestOptions());
                Logger.info("Plan already exists on stripe: " + code);
                Map<String, Object> planParams = new HashMap<>();
                planParams.put("name", name);
                return currentPlan.update(planParams, stripeApi.getRequestOptions());
            } catch (InvalidRequestException e) {
                if (e.getMessage().startsWith("No such plan")) {
                    Logger.info("Creating stripe plan: " + code);
                    Map<String, Object> planParams = new HashMap<>();
                    planParams.put("id", code);
                    planParams.put("amount", price.multiply(BigDecimal.valueOf(100)).intValue());
                    planParams.put("currency", "eur");
                    planParams.put("interval", "month");
                    planParams.put("name", name);

                    Map<String, Object> metadata = new HashMap<>();
                    metadata.put("id", id);
                    planParams.put("metadata", metadata);
                    return com.stripe.model.Plan.create(planParams, stripeApi.getRequestOptions());
                } else {
                    throw new RuntimeException("Error getting current plan: " + code);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creating plan: " + code, e);
        }
    }

    /**
     * Retrieve a plan.
     *
     * @param id The plan ID
     */
    public com.stripe.model.Plan getPlan(String id) {
        try {
            return com.stripe.model.Plan.retrieve(id, stripeApi.getRequestOptions());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving a plan: " + id, e);
        }
    }

}

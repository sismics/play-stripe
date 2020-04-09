package helpers.api.stripe.mock;

import com.stripe.model.Plan;
import helpers.api.stripe.service.PlanService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author jtremeaux
 */
public class MockPlanService {
    private static Map<String, Plan> planMap = new HashMap<>();

    /**
     * Create a mock of PlanService.
     *
     * @return The mock
     */
    public static PlanService create() {
        PlanService planService = mock(PlanService.class);

        when(planService.getPlan(any(String.class))).thenAnswer(i -> {
            String id = i.getArgument(0);
            return planMap.get(id);
        });

        when(planService.create(any(UUID.class), any(String.class), any(String.class), any(BigDecimal.class))).thenAnswer(i -> {
            UUID id = i.getArgument(0);
            String code = i.getArgument(1);
            String name = i.getArgument(2);
            BigDecimal amount = i.getArgument(3);

            Plan stripePlan = new Plan();
            stripePlan.setId(code);
            stripePlan.setTrialPeriodDays(30);
            stripePlan.setCurrency("eur");
            stripePlan.setAmount(amount.longValue() * 100);
            stripePlan.setName(name);
            planMap.put(stripePlan.getId(), stripePlan);
            return stripePlan;
        });

        return planService;
    }
}

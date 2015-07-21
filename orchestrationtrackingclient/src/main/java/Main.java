import com.google.common.collect.ImmutableMap;
import io.yaas.workflow.Workflow;
import io.yaas.workflow.action.ActionResult;
import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.action.SimpleAction;
import io.yaas.workflow.runtime.resulthandler.LoggingWorkflowEngineResultHandler;
import io.yaas.workflow.runtime.WorkflowEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by i303874 on 3/10/15.
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // takes cartId, returns cart (productId: quantity)

        SimpleAction getShoppingCart = new SimpleAction("Get Shopping Cart", "1.0", (arguments) -> {
            String cartId = checkNotNull(String.class.cast(arguments.get("cartid")));

            return new ActionResult(new Arguments(new ImmutableMap.Builder<String, Object>()
                    .putAll(arguments)
                    .put("cart", new ImmutableMap.Builder<String, Integer>()
                            .put("product1", 5)
                            .put("product2", 7)
                            .build())
                    .build()));
        });

        getShoppingCart.setCompensationFunction((arguments) -> {
            // arguments.getError().getMessage()
            System.out.println("In getShoppingCart an issue is occured !!!");
            return ActionResult.EMPTY_RESULT;
        });

        // "calculate cart price"

        SimpleAction calculateCartPrice = new SimpleAction("Calculate Cart Price", "1.0", (arguments) -> {
            String cartId = checkNotNull(String.class.cast(arguments.get("cartid")));

            return new ActionResult(new Arguments(new ImmutableMap.Builder<String, Object>()
                    .putAll(arguments)
                    .put("cartprice", BigDecimal.valueOf(100.0))
                    .build()));
        });

        calculateCartPrice.setCompensationFunction((arguments) -> {
            // arguments.getError().getMessage()
            System.out.println("in calculateCartPrice an issue is occured !!!");
            return ActionResult.EMPTY_RESULT;
        });
        // "reserve stock"

        SimpleAction reserveStock = new SimpleAction("Reserve Stock", "1.0", (arguments) -> {
            Set<Map.Entry<String, String>> cartEntries = checkNotNull(Map.class.cast(arguments.get("cart"))).entrySet();
            return new ActionResult(arguments);
        });

        reserveStock.setCompensationFunction((arguments) -> {
            // arguments.getError().getMessage()
            System.out.println("In reserveStock an issue is occured !!!");
            return ActionResult.EMPTY_RESULT;
        });
        // "capture payment"

        SimpleAction capturePayment = new SimpleAction("Capture Payment", "1.0", (arguments) -> {
            BigDecimal cartPrice = checkNotNull(BigDecimal.class.cast(arguments.get("cartprice")));

            return new ActionResult(arguments);
        });

        capturePayment.setCompensationFunction((arguments) -> {
            // arguments.getError().getMessage()
            throw new RuntimeException("bum2");

//            System.out.println("In capturePayment an issue is occured !!!");
//            return new ActionResult(Arguments.EMPTY_ARGUMENTS);
        });
        // "create order"

        SimpleAction createOrder = new SimpleAction("Create Order", "1.0", (arguments) -> {
            String cartId = checkNotNull(String.class.cast(arguments.get("cartid")));

            //throw new RuntimeException("bum");

            return new ActionResult(arguments);
        });

        createOrder.setCompensationFunction((arguments) -> {
            // arguments.getError().getMessage()
            System.out.println("In createOrder an issue is occured !!!");
            return ActionResult.EMPTY_RESULT;
        });

        Workflow w = new Workflow("Shopping Cart Checkout", 1);
        w.setErrorHandler((arguments) -> {
            System.out.println("Workflow failed!");
            return ActionResult.EMPTY_RESULT;
        });
        w.getStartAction().addAction(getShoppingCart);
        getShoppingCart.addAction(calculateCartPrice);
        getShoppingCart.addAction(reserveStock);
        reserveStock.addAction(capturePayment);
        calculateCartPrice.addAction(capturePayment);
        capturePayment.addAction(createOrder);

        w.execute(new WorkflowEngine(getTrackingClientEndpoint()), new Arguments(new ImmutableMap.Builder<String, Object>()
                .put("cartid", "123")
                .build()), new LoggingWorkflowEngineResultHandler());

//        w.compensate(new WorkflowEngine(getTrackingClientEndpoint()), "fa4f2e28-458e-4ccc-b553-cbcb7c37028e");
    }

    private static String getTrackingClientEndpoint() {
        String trackingClient = System.getenv("TRACKER_SERVICE");
        if (trackingClient == null) {
            trackingClient = "http://localhost:9080";
        }
        return trackingClient;
    }
}

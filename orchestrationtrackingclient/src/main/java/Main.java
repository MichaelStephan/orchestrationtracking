import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import io.yaas.workflow.Action;
import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;
import io.yaas.workflow.Workflow;
import io.yaas.workflow.errorhandler.UndoActionErrorHandler;
import io.yaas.workflow.runtime.WorkflowEngine;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

/**
 * Created by i303874 on 3/10/15.
 */
public class Main {

    public static void main(String[] args) {

        // takes cartId, returns cart (productId: quantity)

        Action getShoppingCart = new Action("Get Shopping Cart", "1.0", (arguments) -> {
            String cartId = Preconditions.checkNotNull(String.class.cast(arguments.get("cartid")));

            return new ActionResult(new Arguments(new ImmutableMap.Builder<String, Object>()
                    .putAll(arguments)
                    .put("cart", new ImmutableMap.Builder<String, Integer>()
                            .put("product1", 5)
                            .put("product2", 7)
                            .build())
                    .build()));
        });
        getShoppingCart.setErrorHandler(new UndoActionErrorHandler((cause, arguments) -> {
            System.out.println("getShoppingCart an issue occured !!!" + arguments);
            return null;
        }));

        // "calculate cart price"

        Action calculateCartPrice = new Action("Calculate Cart Price", "1.0", (arguments) -> {
            String cartId = Preconditions.checkNotNull(String.class.cast(arguments.get("cartid")));

            return new ActionResult(new Arguments(new ImmutableMap.Builder<String, Object>()
                    .putAll(arguments)
                    .put("cartprice", BigDecimal.valueOf(100.0))
                    .build()));
        });
        calculateCartPrice.setErrorHandler(new UndoActionErrorHandler((cause, arguments) -> {
            System.out.println("calculateCartPrice an issue occured !!!" + arguments);
            throw new RuntimeException("crash!!");
            // return null;
        }));

        // "reserve stock"

        Action reserveStock = new Action("Reserve Stock", "1.0", (arguments) -> {
            Set<Map.Entry<String, String>> cartEntries = Preconditions.checkNotNull(Map.class.cast(arguments.get("cart"))).entrySet();
            return new ActionResult(arguments);
        });
        reserveStock.setErrorHandler(new UndoActionErrorHandler((cause, arguments) -> {
            System.out.println("reserveStock an issue occured !!!" + arguments);
            return null;
        }));

        // "capture payment"

        Action capturePayment = new Action("Capture Payment", "1.0", (arguments) -> {
            BigDecimal cartPrice = Preconditions.checkNotNull(BigDecimal.class.cast(arguments.get("cartprice")));

            return new ActionResult(arguments);
        });
        capturePayment.setErrorHandler(new UndoActionErrorHandler((cause, arguments) -> {
            System.out.println("capturePayment an issue occured !!!" + arguments);
            return null;
        }));

        // "create order"

        Action createOrder = new Action("Create Order", "1.0", (arguments) -> {
            String cartId = Preconditions.checkNotNull(String.class.cast(arguments.get("cartid")));

            throw new RuntimeException("bum");

//            return new ActionResult(arguments);
        });
        createOrder.setErrorHandler(new UndoActionErrorHandler((cause, arguments) -> {
            System.out.println("createOrder an issue occured !!!" + arguments);
            return null;
        }));

        Workflow w = new Workflow("Shopping Cart Checkout", 1);
        w.getStartAction().addAction(getShoppingCart);
        getShoppingCart.addAction(calculateCartPrice);
        getShoppingCart.addAction(reserveStock);
        reserveStock.addAction(capturePayment);
        calculateCartPrice.addAction(capturePayment);

        capturePayment.addAction(createOrder);

        w.execute(new WorkflowEngine(getTrackingClientEndpoint()), new Arguments(new ImmutableMap.Builder<String, Object>()
                .put("cartid", "123")
                .build()));
    }

    private static String getTrackingClientEndpoint() {
        String trackingClient = System.getenv("TRACKER_SERVICE");
        if (trackingClient == null) {
            trackingClient = "http://localhost:8080";
        }
        return trackingClient;
    }
}

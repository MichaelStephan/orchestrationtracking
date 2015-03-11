import com.google.common.collect.ImmutableMap;
import io.yaas.workflow.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by i303874 on 3/10/15.
 */
public class Main {

    public static void main(String[] args) {
        // "create order"
        Action createOrder = new Action("1", "create order", (action, arguments, result) -> {
            String cartId = checkNotNull(String.class.cast(arguments.get("cartid")));

            new Thread(() -> {
                result.set(new ActionResult(action, arguments));
            }).start();
            return result;
        }, Collections.EMPTY_LIST);

        // "capture payment"
        Action capturePayment = new Action("2", "capture payment", (action, arguments, result) -> {
            BigDecimal cartPrice = checkNotNull(BigDecimal.class.cast(arguments.get("cartprice")));

            new Thread(() -> {
                result.set(new ActionResult(action, arguments));
            }).start();

            return result;
        }, Arrays.asList(new Action[]{createOrder}));

        // "merge"
        Action merge = new MergeAction(Arrays.asList(new Action[]{capturePayment}), 2);

        // "reserve stock"
        Action reserveStock =
                new Action("3", "reserve stock", (action, arguments, result) -> {
                    Set<Map.Entry<String, String>> cartEntries = checkNotNull(Map.class.cast(arguments.get("cart"))).entrySet();

                    new Thread(() -> {
                        result.set(new ActionResult(action, arguments));
                    }).start();

                    return result;
                }, Arrays.asList(new Action[]{merge}));

        //"calculate cart price"
        Action calculateCartPrice =
                new Action("4", "calculate cart price", (action, arguments, result) -> {
                    String cartId = checkNotNull(String.class.cast(arguments.get("cartid")));

                    new Thread(() -> {
                        result.set(new ActionResult(action, new ImmutableMap.Builder<String, Object>()
                                .putAll(arguments)
                                .put("cartprice", BigDecimal.valueOf(100.0))
                                .build()));
                    }).start();

                    return result;
                }, Arrays.asList(new Action[]{merge}));

        //takes cartId, returns cart (productId: quantity)
        Action getCart = new Action("5", "get cart", (action, arguments, result) -> {
            String cartId = checkNotNull(String.class.cast(arguments.get("cartid")));

            new Thread(() -> {
                result.set(new ActionResult(action, new ImmutableMap.Builder<String, Object>()
                        .putAll(arguments)
                        .put("cart", new ImmutableMap.Builder<String, Integer>()
                                .put("product1", 5)
                                .put("product2", 7)
                                .build())
                        .build()));
            }).start();

            return result;
        }, Arrays.asList(new Action[]{calculateCartPrice, reserveStock}));


        WorkflowEngine workflowEngine = new WorkflowEngine();
        workflowEngine.runAction(new StartAction(Arrays.asList(new Action[]{getCart})), new ImmutableMap.Builder<String, Object>()
                .put("cartid", "123")
                .build());
    }
}

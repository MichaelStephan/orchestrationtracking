import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;
import io.yaas.workflow.Workflow;
import io.yaas.workflow.Workflow.Action;
import io.yaas.workflow.runtime.WorkflowEngine;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

/**
 * Created by i303874 on 3/10/15.
 */
public class Main {

    public static void main(String[] args) {

        // takes cartId, returns cart (productId: quantity)
    	
		Action getShoppingCart = new Workflow.Action("Get Shopping Cart", "1.0", (action, arguments, result) -> {
            String cartId = Preconditions.checkNotNull(String.class.cast(arguments.get("cartid")));
            
            new Thread(() -> {
                result.set(new ActionResult(action, new Arguments(new ImmutableMap.Builder<String, Object>()
                        .putAll(arguments)
                        .put("cart", new ImmutableMap.Builder<String, Integer>()
                                .put("product1", 5)
                                .put("product2", 7)
                                .build())
                        .build())));
            }).start();

            return result;
		});
    	
        // "calculate cart price"

		Action calculateCartPrice = new Action("Calculate Cart Price", "1.0", (action, arguments, result) -> {
	        String cartId = Preconditions.checkNotNull(String.class.cast(arguments.get("cartid")));
	
	        new Thread(() -> {
	            result.set(new ActionResult(action, new Arguments(new ImmutableMap.Builder<String, Object>()
	                    .putAll(arguments)
	                    .put("cartprice", BigDecimal.valueOf(100.0))
	                    .build())));
	        }).start();
	
	        return result;
	    });
		
        // "reserve stock"
		
        Action reserveStock = new Action("Reserve Stock", "1.0", (action, arguments, result) -> {
            Set<Map.Entry<String, String>> cartEntries = Preconditions.checkNotNull(Map.class.cast(arguments.get("cart"))).entrySet();

            new Thread(() -> {
                result.set(new ActionResult(action, arguments));
            }).start();

            return result;
        });

        // "capture payment"
        
        Action capturePayment = new Action("Capture Payment", "1.0", (action, arguments, result) -> {
            BigDecimal cartPrice = Preconditions.checkNotNull(BigDecimal.class.cast(arguments.get("cartprice")));

            new Thread(() -> {
                result.set(new ActionResult(action, arguments));
            }).start();

            return result;
        });
        

        // "create order"
        
        Action createOrder = new Action("Create Order", "1.0", (action, arguments, result) -> {
            String cartId = Preconditions.checkNotNull(String.class.cast(arguments.get("cartid")));

            new Thread(() -> {
                result.set(new ActionResult(action, arguments));
            }).start();
            return result;
        });

    	Workflow w = new Workflow("Shopping Cart Checkout", "1");
		w.setErrorHandler((exception) -> { 
			System.out.println("global failure");
		}, (exception) -> { 
			System.out.println("global unknown");
		})
		.getStartAction()
		.addAction(getShoppingCart);
		
		getShoppingCart.addAction(calculateCartPrice);
		getShoppingCart.addAction(reserveStock);
		reserveStock.addAction(capturePayment);
		calculateCartPrice.addAction(capturePayment);
		
		capturePayment.addAction(createOrder);
		
		w.execute(new WorkflowEngine(), new Arguments(new ImmutableMap.Builder<String, Object>()
                .put("cartid", "123")
                .build()));    	
    }
}
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import rx.functions.Func3;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by i303874 on 3/10/15.
 */
public class Main {
    static class ActionResult {
        private Action action;

        private Map<String, Object> result;

        public ActionResult(Action action, Map<String, Object> result) {
            this.action = action;
            this.result = result;
        }

        public Action getAction() {
            return action;
        }

        public Map<String, Object> getResult() {
            return result;
        }
    }

    static class MergeAction extends Action {
        private AtomicInteger count;

        private List<ActionResult> results = new CopyOnWriteArrayList<>();

        public MergeAction(List<Action> successors, int count) {
            super(UUID.randomUUID().toString(), "merge", successors);

            this.count = new AtomicInteger(count);
            this.setFunc((action, arguments, result) -> {
                System.out.println(action.getName() + " is done");
                results.add(new ActionResult(this, arguments));

                if (this.count.decrementAndGet() == 0) {
                    Map<String, Object> consolidatedArguments = new HashMap<>();
                    results.stream().forEach((argument) -> {
                        consolidatedArguments.putAll(argument.getResult());
                    });

                    result.set(new ActionResult(action, new ImmutableMap.Builder<String, Object>().putAll(consolidatedArguments).build()));
                }
                return result;
            });
        }
    }

    static class Action {
        private String id;

        private String name;

        private Func3<Action, Map<String, Object>, SettableFuture<ActionResult>, Future<ActionResult>> func;

        private Set<Action> successors;

        public Action(String id, String name, Func3<Action, Map<String, Object>, SettableFuture<ActionResult>, Future<ActionResult>> func, List<Action> successors) {
            this.id = id;
            this.name = name;
            this.func = func;
            this.successors = ImmutableSet.copyOf(successors);
        }

        public Action(String id, String name, List<Action> successors) {
            this.id = id;
            this.name = name;
            this.successors = ImmutableSet.copyOf(successors);
        }

        public void setFunc(Func3<Action, Map<String, Object>, SettableFuture<ActionResult>, Future<ActionResult>> func) {
            if (this.func != null) {
                throw new IllegalStateException();
            }
            this.func = func;
        }

        public Func3<Action, Map<String, Object>, SettableFuture<ActionResult>, Future<ActionResult>> getFunc() {
            return func;
        }

        public Set<Action> getSuccessors() {
            return successors;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public String toString() {
            return "ACTION: " + getId() + ":" + getName() + " \nSUCCESSORS: " + getSuccessors().stream().map((action) -> {
                return action.getName();
            }).collect(Collectors.toList());
        }

    }

    public static void main(String[] args) {
        // "create order"
        Action createOrder = new Action("1", "create order", (action, arguments, result) -> {
            String cartId = checkNotNull(String.class.cast(arguments.get("cartid")));

            new Thread(() -> {
                System.out.println("running create order");
                // create order

                result.set(new ActionResult(action, arguments));
            }).start();
            return result;
        }, Collections.EMPTY_LIST);

        // "capture payment"
        Action capturePayment = new Action("2", "capture payment", (action, arguments, result) -> {
            BigDecimal cartPrice = checkNotNull(BigDecimal.class.cast(arguments.get("cartprice")));

            new Thread(() -> {
                System.out.println("running capture payment");
                // capture payment

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
                        System.out.println("running reserve stock");
                        // reserve stock

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        result.set(new ActionResult(action, arguments));
                    }).start();

                    return result;
                }, Arrays.asList(new Action[]{merge}));

        //"calculate cart price"
        Action calculateCartPrice =
                new Action("4", "calculate cart price", (action, arguments, result) -> {
                    String cartId = checkNotNull(String.class.cast(arguments.get("cartid")));

                    new Thread(() -> {

                        System.out.println("running calculate cart price");
                        // fetch cart price

                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

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
                System.out.println("running get cart");
                // fetch cart

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

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

        runSerialAction(getCart, new ImmutableMap.Builder<String, Object>()
                .put("cartid", "123")
                .build());
    }

    private static void printWorkflow(Action action) {
        System.out.println(action);
        action.getSuccessors().stream().forEach((a) -> {
            printWorkflow(a);
        });
    }

    public static void runSerialAction(Action action, Map<String, Object> arguments) {
        SettableFuture<ActionResult> future = SettableFuture.create();
        Futures.addCallback(future, new FutureCallback<ActionResult>() {
            @Override
            public void onSuccess(ActionResult result) {
                action.getSuccessors().forEach((successor) -> {
                    runSerialAction(successor, result.getResult());
                });
            }

            @Override
            public void onFailure(Throwable error) {
                error.printStackTrace();
            }
        });

        action.getFunc().call(action, arguments, future);
    }
}

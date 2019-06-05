package de.raphaelmuesseler.financer.util.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FinancerExecutor {
    private FinancerExecutor() {
        super();
    }

    private static ExecutorService executor = Executors.newCachedThreadPool();

    public static ExecutorService getExecutor() {
        return executor;
    }
}

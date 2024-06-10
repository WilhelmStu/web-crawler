package aau.cc;

import aau.cc.model.CrawledWebsite;

import java.util.concurrent.*;

public class ConcurrencyManager {
    private static final int EXECUTOR_TIMEOUT = 10000;
    private ExecutorService executorService;

    public ConcurrencyManager() {
        this.executorService = Executors.newCachedThreadPool(); // cachedThreadPool has the best performance;
    }

    public Future<CrawledWebsite> submitTask(Callable<CrawledWebsite> task) {
        return executorService.submit(task);
    }

    public void shutdown() {
        try {
            ensureShutdown();
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            System.err.println("ExecutorService did not terminate: " + e.getMessage());
        }
    }

    private void ensureShutdown() throws InterruptedException {
        executorService.shutdown();
        if (!executorService.awaitTermination(EXECUTOR_TIMEOUT, TimeUnit.MILLISECONDS)) {
            executorService.shutdownNow();
            if (!executorService.awaitTermination(EXECUTOR_TIMEOUT, TimeUnit.MILLISECONDS)) {
                System.err.println("ExecutorService did not terminate");
            }
        }
    }

    public void resetIfDown() {
        if (executorService.isShutdown() || executorService.isTerminated()) {
            executorService = Executors.newCachedThreadPool();
        }
    }

    protected void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    protected ExecutorService getExecutorService() {
        return executorService;
    }
}

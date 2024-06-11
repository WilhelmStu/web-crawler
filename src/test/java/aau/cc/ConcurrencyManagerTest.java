package aau.cc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


public class ConcurrencyManagerTest {

    private ConcurrencyManager concurrencyManager;
    private ExecutorService mockExecutor;

    @BeforeEach
    public void setUp() {
        concurrencyManager = new ConcurrencyManager();
        mockExecutor = Mockito.mock(ExecutorService.class);
    }

    private void setUpMockExecutor() throws InterruptedException {
        when(mockExecutor.awaitTermination(anyLong(), any())).thenReturn(false);
        when(mockExecutor.shutdownNow()).thenReturn(new ArrayList<>());
        concurrencyManager.setExecutorService(mockExecutor);
    }

    @AfterEach
    public void tearDown() {
        concurrencyManager = null;
    }

    @Test
    public void testProperCreationOfExecutor() {
        ExecutorService service = concurrencyManager.getExecutorService();
        assertNotNull(service);
        assertFalse(service.isShutdown());
    }

    @Test
    public void testProperShutdown() {
        ExecutorService service = concurrencyManager.getExecutorService();
        assertFalse(service.isShutdown());
        assertTrue(concurrencyManager.shutdown());
        assertTrue(service.isShutdown());
    }

    @Test
    public void testImproperShutdown() throws InterruptedException {
        setUpMockExecutor();
        assertFalse(concurrencyManager.shutdown());
    }

    @Test
    public void testImproperShutdown2() throws InterruptedException {
        setUpMockExecutor();
        when(mockExecutor.awaitTermination(anyLong(), any())).thenReturn(false).thenReturn(true);
        assertFalse(concurrencyManager.shutdown());
    }

    @Test
    public void testImproperShutdownException() throws InterruptedException {
        setUpMockExecutor();
        when(mockExecutor.awaitTermination(anyLong(), any())).thenThrow(InterruptedException.class);
        assertDoesNotThrow(() -> concurrencyManager.shutdown());
    }

    @Test
    public void testResetOfExecutor() throws InterruptedException {
        setUpMockExecutor();
        when(mockExecutor.isShutdown()).thenReturn(true);
        concurrencyManager.resetIfDown();
        ExecutorService service = concurrencyManager.getExecutorService();
        assertNotEquals(mockExecutor, service);
    }

    @Test
    public void testResetOfExecutorNotShutdown() {
        concurrencyManager.resetIfDown();
        ExecutorService service = concurrencyManager.getExecutorService();
        assertFalse(service.isShutdown());
    }

    @Test
    public void testResetOfExecutorTerminated() throws InterruptedException {
        setUpMockExecutor();
        when(mockExecutor.isTerminated()).thenReturn(true);
        concurrencyManager.resetIfDown();
        ExecutorService service = concurrencyManager.getExecutorService();
        assertFalse(service.isShutdown());
    }
}

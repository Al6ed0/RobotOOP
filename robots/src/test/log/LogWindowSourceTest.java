package log;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class LogWindowSourceTest {
    @Test
    public void maxQueueLengthTest() {
        LogWindowSource source = new LogWindowSource(4);

        source.append(LogLevel.Debug, "first");
        source.append(LogLevel.Debug, "second");
        source.append(LogLevel.Debug, "third");

        Assertions.assertEquals(3, source.size());

        source.append(LogLevel.Debug, "fourth");
        source.append(LogLevel.Debug, "fifth");

        Assertions.assertEquals(4, source.size());

        LogEntry firstMsg = source.all().iterator().next();
        Assertions.assertEquals("second", firstMsg.getMessage());
    }

    @Test
    public void registerAndUnregisterTest() throws NoSuchFieldException,
            IllegalAccessException, InterruptedException {

        LogWindowSource source = new LogWindowSource(12);

        LogChangeListener firstListener = new LogChangeListener() {
            @Override
            public void onLogChanged() {}
        };
        LogChangeListener secondListener = new LogChangeListener() {
            @Override
            public void onLogChanged() {}
        };
        source.registerListener(firstListener);
        source.registerListener(secondListener);


        Field listenersField = LogWindowSource.class.getDeclaredField("m_listeners");
        listenersField.setAccessible(true);
        List<?> internalList = (List<?>) listenersField.get(source);

        Assertions.assertFalse(internalList.isEmpty());

        source.unregisterListener(secondListener);
        Assertions.assertFalse(internalList.isEmpty());

        source.unregisterListener(firstListener);
        Assertions.assertTrue(internalList.isEmpty());
    }

    @Test
    public void getMessageTest() {
        LogWindowSource source = new LogWindowSource(12);

        AtomicInteger firstCount = new AtomicInteger(0);
        AtomicInteger secondCount = new AtomicInteger(0);

        LogChangeListener firstListener = new LogChangeListener() {
            @Override
            public void onLogChanged() {
                firstCount.incrementAndGet();
            }
        };
        LogChangeListener secondListener = new LogChangeListener() {
            @Override
            public void onLogChanged() {
                secondCount.incrementAndGet();
            }
        };

        source.registerListener(firstListener);
        source.registerListener(secondListener);

        source.append(LogLevel.Debug, "first");
        Assertions.assertEquals(1, firstCount.get());
        Assertions.assertEquals(1, secondCount.get());

        source.unregisterListener(firstListener);
        source.append(LogLevel.Debug, "second");
        Assertions.assertEquals(1, firstCount.get());
        Assertions.assertEquals(2, secondCount.get());

    }

}

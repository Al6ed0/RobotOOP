package log;

import java.lang.ref.WeakReference;
import java.util.*;

/**
 * Что починить:
 * 1. Этот класс порождает утечку ресурсов (связанные слушатели оказываются
 * удерживаемыми в памяти)
 * 2. Этот класс хранит активные сообщения лога, но в такой реализации он 
 * их лишь накапливает. Надо же, чтобы количество сообщений в логе было ограничено 
 * величиной m_iQueueLength (т.е. реально нужна очередь сообщений 
 * ограниченного размера) 
 */
public class LogWindowSource
{
    private final int m_iQueueLength;
    
    private final LinkedList<LogEntry> m_messages;
    private final List<WeakReference<LogChangeListener>> m_listeners;
    private volatile LogChangeListener[] m_activeListeners;
    
    public LogWindowSource(int iQueueLength) 
    {
        m_iQueueLength = iQueueLength;
        m_messages = new LinkedList<>();
        m_listeners = new ArrayList<>();
    }
    
    public void registerListener(LogChangeListener listener)
    {
        eraseListeners();
        synchronized(m_listeners)
        {
            m_listeners.add(new WeakReference<>(listener));
            m_activeListeners = null;
        }
    }
    
    public void unregisterListener(LogChangeListener listener)
    {
        eraseListeners();
        synchronized(m_listeners)
        {
            Iterator<WeakReference<LogChangeListener>> iterator = m_listeners.iterator();
            while (iterator.hasNext()) {
                WeakReference<LogChangeListener> reference = iterator.next();
                LogChangeListener storedListener = reference.get();
                if (storedListener == listener) {
                    iterator.remove();
                }
            }
            m_activeListeners = null;
        }
    }

    private void eraseListeners() {
        synchronized (m_listeners) {
            m_listeners.removeIf(ref -> ref.get() == null);
        }
    }
    
    public void append(LogLevel logLevel, String strMessage)
    {
        LogEntry entry = new LogEntry(logLevel, strMessage);

        synchronized (m_messages) {
            m_messages.add(entry);
            if (size() > m_iQueueLength) {
                m_messages.removeFirst();
            }
        }
        LogChangeListener [] activeListeners = getActiveListeners();
        if (activeListeners != null){
            for (LogChangeListener listener : activeListeners) {
                if (listener != null) {
                    listener.onLogChanged();
                }
            }
        }
    }

    private LogChangeListener [] getActiveListeners() {
        LogChangeListener[] activeListeners = m_activeListeners;
        if (activeListeners == null) {
            synchronized (m_listeners) {
                if (m_activeListeners == null) {
                    eraseListeners();
                    List<LogChangeListener> tempActiveListeners = new ArrayList<>();
                    for (WeakReference<LogChangeListener> reference : m_listeners) {
                        LogChangeListener listener = reference.get();
                        if (listener != null) {
                            tempActiveListeners.add(listener);
                        }
                    }
                    activeListeners = tempActiveListeners.toArray(new LogChangeListener[0]);
                    m_activeListeners = activeListeners;
                }
            }
        }
        return  activeListeners;
    }
    
    public int size()
    {
        synchronized (m_messages) {
            return m_messages.size();
        }
    }

    public Iterable<LogEntry> range(int startFrom, int count)
    {
        if (startFrom < 0 || startFrom >= size())
        {
            return Collections.emptyList();
        }
        int indexTo = Math.min(startFrom + count, size());
        return m_messages.subList(startFrom, indexTo);
    }

    public Iterable<LogEntry> all()
    {
        return m_messages;
    }
}

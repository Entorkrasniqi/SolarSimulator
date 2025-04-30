package model;

import types.EventType;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import types.*;


public class EventQueue {
    private final LinkedBlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>();
    private final Map<EventType, EventHandler> handlers = new HashMap<>();
    private volatile boolean running = false;
    private Thread processorThread;
    
    public void registerHandler(EventType type, EventHandler handler) {
        handlers.put(type, handler);
    }
    
    public void queueEvent(Event event) {
        eventQueue.offer(event);
    }
    
    public void start() {
        if (running) return;
        
        running = true;
        processorThread = new Thread(this::processEvents);
        processorThread.setName("EventProcessorThread");
        processorThread.start();
    }
    
    public void stop() {
        running = false;
        if (processorThread != null) {
            processorThread.interrupt();
        }
    }
    
    private void processEvents() {
        while (running) {
            try {
                Event event = eventQueue.poll(100, TimeUnit.MILLISECONDS);
                if (event != null) {
                    EventHandler handler = handlers.get(event.getType());
                    if (handler != null) {
                        handler.handle(event);
                    } else {
                        System.err.println("No handler registered for event type: " + event.getType());
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("Error processing event: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
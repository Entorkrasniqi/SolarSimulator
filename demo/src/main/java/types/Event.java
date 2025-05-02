package types;

public abstract class Event {
    private final EventType type;
    private final long timestamp;
    
    public Event(EventType type) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }
    
    public EventType getType() {
        return type;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
}
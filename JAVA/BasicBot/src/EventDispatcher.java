public interface EventDispatcher {
    public void addEventHandler(EventHandler eventHandler);

    public void removeEventHandler(EventHandler eventHandler);

    public void executeEventHandler(Event event);
}

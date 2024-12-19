package hand.interactor.voodoo.handler;

public enum ListenerPriority {
    LOWEST(-750),
    LOWER(-500),
    LOW(-250),
    NORMAL(0),
    HIGH(250),
    HIGHER(500),
    HIGHEST(750),
    TURBOMAX(1000);

    private final int priorityLevel;

    ListenerPriority(final int priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public int getPriorityLevel() {
        return priorityLevel;
    }
}

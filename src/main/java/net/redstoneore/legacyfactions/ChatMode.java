package net.redstoneore.legacyfactions;

public enum ChatMode {
    FACTION(3, Lang.CHAT_FACTION),
    ALLIANCE(2, Lang.CHAT_ALLIANCE),
    TRUCE(1, Lang.CHAT_TRUCE),
    PUBLIC(0, Lang.CHAT_PUBLIC),
    ;

    public final int value;
    public final Lang nicename;

    private ChatMode(final int value, final Lang nicename) {
        this.value = value;
        this.nicename = nicename;
    }

    public boolean isAtLeast(ChatMode role) {
        return this.value >= role.value;
    }

    public boolean isAtMost(ChatMode role) {
        return this.value <= role.value;
    }

    @Override
    public String toString() {
        return this.nicename.toString();
    }

    public ChatMode getNext() {
        if (this == PUBLIC) {
            return ALLIANCE;
        }
        if (this == ALLIANCE) {
            return FACTION;
        }
        return PUBLIC;
    }
}

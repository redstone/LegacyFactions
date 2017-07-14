package net.redstoneore.legacyfactions;

public interface EconomyParticipator extends RelationParticipator {

    String getAccountId();

    void msg(String str, Object... args);

    void msg(Lang translation, Object... args);
    
}

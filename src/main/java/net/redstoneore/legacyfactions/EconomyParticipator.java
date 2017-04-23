package net.redstoneore.legacyfactions;

public interface EconomyParticipator extends RelationParticipator {

    public String getAccountId();

    public void msg(String str, Object... args);

    public void msg(TL translation, Object... args);
}
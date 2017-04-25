package net.redstoneore.legacyfactions;

import net.redstoneore.legacyfactions.entity.Conf;

public enum Role {
    ADMIN(2, Lang.ROLE_ADMIN),
    MODERATOR(1, Lang.ROLE_MODERATOR),
    NORMAL(0, Lang.ROLE_NORMAL);

    public final int value;
    public final String nicename;
    public final Lang translation;

    private Role(final int value, final Lang translation) {
        this.value = value;
        this.nicename = translation.toString();
        this.translation = translation;
    }

    public boolean isAtLeast(Role role) {
        return this.value >= role.value;
    }

    public boolean isAtMost(Role role) {
        return this.value <= role.value;
    }

    @Override
    public String toString() {
        return this.nicename;
    }

    public Lang getTranslation(){
        return translation;
    }

    public String getPrefix() {
        if (this == Role.ADMIN) {
            return Conf.prefixAdmin;
        }

        if (this == Role.MODERATOR) {
            return Conf.prefixMod;
        }

        return "";
    }
    
}

package net.redstoneore.legacyfactions;

import net.redstoneore.legacyfactions.entity.Conf;

public enum Role {
    ADMIN(3, Lang.ROLE_ADMIN),
    COLEADER(2, Lang.ROLE_COLEADER),
    MODERATOR(1, Lang.ROLE_MODERATOR),
    NORMAL(0, Lang.ROLE_NORMAL);

    public final int value;
    public final String nicename;
    public final Lang translation;

    Role(final int value, final Lang translation) {
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

    public String toNiceName() {
        return this.nicename;
    }

    public Lang getTranslation(){
        return translation;
    }

    public String getPrefix() {
        if (this == Role.ADMIN) {
            return Conf.prefixAdmin;
        }

        if (this == Role.COLEADER) {
            return Conf.prefixColeader;
        }

        if (this == Role.MODERATOR) {
            return Conf.prefixMod;
        }

        return "";
    }
    
}

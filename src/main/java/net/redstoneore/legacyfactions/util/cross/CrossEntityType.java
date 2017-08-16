package net.redstoneore.legacyfactions.util.cross;

import org.bukkit.entity.EntityType;

public class CrossEntityType implements Cross<CrossEntityType> {
	
	// -------------------------------------------------- //
	// STATIC METHODS
	// -------------------------------------------------- //
	
	public static CrossEntityType of(String name) {
		return new CrossEntityType(name);
	}
	
	public static CrossEntityType of(DefaultEntityType type) {
		return new CrossEntityType(type.name());
	}
	
	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //
	
	private CrossEntityType(String name) {
		this.name = name;
	}
	
	// -------------------------------------------------- //
	// FIELDS
	// -------------------------------------------------- //
	
	protected final String name;
	
	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	public String getName() {
		return this.name;
	}
	
	public EntityType toEntityType() {
		return EntityType.valueOf(this.name);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CrossEntityType)) return false;
		
		if (obj == this) return true;
		
		if (((CrossEntityType) obj).name == this.name) return true;
		
		return false;
	}
	
	@Override
	public boolean is(CrossEntityType what) {
		return this.equals(what);
	}
	
	// -------------------------------------------------- //
	// ENUM
	// -------------------------------------------------- //
	
	public enum DefaultEntityType {
		DROPPED_ITEM,
		EXPERIENCE_ORB,
		AREA_EFFECT_CLOUD,
		ELDER_GUARDIAN,
		WITHER_SKELETON,
		STRAY,
		EGG,
		LEASH_HITCH,
		PAINTING,
		ARROW,
		SNOWBALL,
		FIREBALL,
		SMALL_FIREBALL,
		ENDER_PEARL,
		ENDER_SIGNAL,
		SPLASH_POTION,
		THROWN_EXP_BOTTLE,
		ITEM_FRAME,
		WITHER_SKULL,
		PRIMED_TNT,
		FALLING_BLOCK,
		FIREWORK,
		HUSK,
		SPECTRAL_ARROW,
		SHULKER_BULLET,
		DRAGON_FIREBALL,
		ZOMBIE_VILLAGER,
		SKELETON_HORSE,
		ZOMBIE_HORSE,
		ARMOR_STAND,
		DONKEY,
		MULE,
		EVOKER_FANGS,
		EVOKER,
		VEX,
		VINDICATOR,
		ILLUSIONER,
		MINECART_COMMAND,
		BOAT,
		MINECART,
		MINECART_CHEST,
		MINECART_FURNACE,
		MINECART_TNT,
		MINECART_HOPPER,
		MINECART_MOB_SPAWNER,
		CREEPER,
		SKELETON,
		SPIDER,
		GIANT,
		ZOMBIE,
		SLIME,
		GHAST,
		PIG_ZOMBIE,
		ENDERMAN,
		CAVE_SPIDER,
		SILVERFISH,
		BLAZE,
		MAGMA_CUBE,
		ENDER_DRAGON,
		WITHER,
		BAT,
		WITCH,
		ENDERMITE,
		GUARDIAN,
		SHULKER,
		PIG,
		SHEEP,
		COW,
		CHICKEN,
		SQUID,
		WOLF,
		MUSHROOM_COW,
		SNOWMAN,
		OCELOT,
		IRON_GOLEM,
		HORSE,
		RABBIT,
		POLAR_BEAR,
		LLAMA,
		LLAMA_SPIT,
		PARROT,
		VILLAGER,
		ENDER_CRYSTAL,
		
		;
		
		public org.bukkit.entity.EntityType toEntity() {
			return org.bukkit.entity.EntityType.valueOf(this.name());
		}
	}
	
}

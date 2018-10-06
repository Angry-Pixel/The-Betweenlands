package thebetweenlands.api.entity;

import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.world.storage.loot.LootContext;

public interface IEntityWithLootModifier {
	/**
	 * Returns the loot modifier for this entity. The loot modifier condition/property can
	 * specify in which modifier range the condition/property returns true so that different
	 * loot pools can be specified for certain modifiers. Different modifier types
	 * can be returned by using different keys
	 * @param context The loot context, can be used to get the killer, luck, etc.
	 * The context is null if the modifier is being checked from an entity property
	 * @param isEntityProperty Whether the loot modifier is being checked from an entity property
	 * @return a map containing the modifier keys and values. If null is returned the
	 * condition/property will always return false
	 */
	@Nullable
	public Map<String, Float> getLootModifiers(@Nullable LootContext context, boolean isEntityProperty);
}

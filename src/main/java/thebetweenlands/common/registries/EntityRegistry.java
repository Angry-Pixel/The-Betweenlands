package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entities.*;
import thebetweenlands.common.entities.fishing.BLFishHook;
import thebetweenlands.common.entities.fishing.anadia.Anadia;
import thebetweenlands.common.entities.fishing.BubblerCrab;
import thebetweenlands.common.entities.fishing.FishBait;
import thebetweenlands.common.entities.fishing.SiltCrab;

public class EntityRegistry {
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, TheBetweenlands.ID);
	public static final DeferredRegister.Items SPAWN_EGGS = DeferredRegister.createItems(TheBetweenlands.ID);

	public static final DeferredHolder<EntityType<?>, EntityType<SwampHag>> SWAMP_HAG = registerWithEgg("swamp_hag", EntityType.Builder.of(SwampHag::new, MobCategory.MONSTER).sized(0.6F, 1.8F), 0x6E5B36, 0x226124);
	public static final DeferredHolder<EntityType<?>, EntityType<Gecko>> GECKO = registerWithEgg("gecko", EntityType.Builder.of(Gecko::new, MobCategory.CREATURE).sized(0.75F, 0.35F),  0xFF8000, 0x22E0B1);
	public static final DeferredHolder<EntityType<?>, EntityType<Wight>> WIGHT = registerWithEgg("wight", EntityType.Builder.of(Wight::new, MobCategory.MONSTER).sized(0.7F, 2.2F), 0xECF8E0, 0x243B0B);
	public static final DeferredHolder<EntityType<?>, EntityType<Anadia>> ANADIA = registerWithEgg("anadia", EntityType.Builder.of(Anadia::new, MobCategory.WATER_CREATURE).sized(0.8F, 0.8F), 0x5D633A, 0xB53D2F);
	public static final DeferredHolder<EntityType<?>, EntityType<BubblerCrab>> BUBBLER_CRAB = registerWithEgg("bubbler_crab", EntityType.Builder.of(BubblerCrab::new, MobCategory.WATER_CREATURE).sized(0.7F, 0.6F), 0xD8D5CB, 0xC7692C);
	public static final DeferredHolder<EntityType<?>, EntityType<SiltCrab>> SILT_CRAB = registerWithEgg("silt_crab", EntityType.Builder.of(SiltCrab::new, MobCategory.WATER_CREATURE).sized(0.8F, 0.6F), 0x468282, 0xBC4114);
	public static final DeferredHolder<EntityType<?>, EntityType<Seat>> SEAT = ENTITY_TYPES.register("seat", () -> EntityType.Builder.<Seat>of(Seat::new, MobCategory.MISC).sized(0.0F, 0.0F).fireImmune().noSummon().build(prefix("seat")));
	public static final DeferredHolder<EntityType<?>, EntityType<FishBait>> FISH_BAIT = ENTITY_TYPES.register("fish_bait", () -> EntityType.Builder.<FishBait>of(FishBait::new, MobCategory.MISC).sized(0.25F, 0.25F).eyeHeight(0.2125F).clientTrackingRange(6).updateInterval(20).build(prefix("fish_bait")));
	public static final DeferredHolder<EntityType<?>, EntityType<BLFishHook>> FISH_HOOK = ENTITY_TYPES.register("fish_hook", () -> EntityType.Builder.<BLFishHook>of(BLFishHook::new, MobCategory.MISC).sized(0.25F, 0.25F).build(prefix("fish_hook")));
	public static final DeferredHolder<EntityType<?>, EntityType<ThrownElixir>> ELIXIR = ENTITY_TYPES.register("elixir", () -> EntityType.Builder.<ThrownElixir>of(ThrownElixir::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build(prefix("elixir")));
	public static final DeferredHolder<EntityType<?>, EntityType<MireSnail>> MIRE_SNAIL = registerWithEgg("mire_snail", EntityType.Builder.of(MireSnail::new, MobCategory.CREATURE).sized(0.75F, 0.6F), 0x8E9456, 0xF2FA96);
	public static final DeferredHolder<EntityType<?>, EntityType<AngryPebble>> ANGRY_PEBBLE = ENTITY_TYPES.register("angry_pebble", () -> EntityType.Builder.<AngryPebble>of(AngryPebble::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build(prefix("angry_pebble")));
	public static final DeferredHolder<EntityType<?>, EntityType<BLItemFrame>> ITEM_FRAME = ENTITY_TYPES.register("item_frame", () -> EntityType.Builder.<BLItemFrame>of(BLItemFrame::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE).build(prefix("item_frame")));
	public static final DeferredHolder<EntityType<?>, EntityType<BetweenstonePebble>> BETWEENSTONE_PEBBLE = ENTITY_TYPES.register("betweenstone_pebble", () -> EntityType.Builder.<BetweenstonePebble>of(BetweenstonePebble::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(20).build(prefix("betweenstone_pebble")));

	private static String prefix(String name) {
		return TheBetweenlands.prefix(name).toString();
	}

	public static <E extends Mob> DeferredHolder<EntityType<?>, EntityType<E>> registerWithEgg(String name, EntityType.Builder<E> builder, int primaryColor, int secondaryColor) {
		DeferredHolder<EntityType<?>, EntityType<E>> ret = ENTITY_TYPES.register(name, () -> builder.build(TheBetweenlands.prefix(name).toString()));
		if (primaryColor != 0 && secondaryColor != 0) {
			SPAWN_EGGS.register(name + "_spawn_egg", () -> new DeferredSpawnEggItem(ret, primaryColor, secondaryColor, new Item.Properties()));
		}
		return ret;
	}
}

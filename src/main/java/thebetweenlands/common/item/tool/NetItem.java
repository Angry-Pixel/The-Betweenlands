package thebetweenlands.common.item.tool;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.entity.creature.*;
import thebetweenlands.common.entity.fishing.anadia.Anadia;
import thebetweenlands.common.entity.monster.TinySludgeWorm;
import thebetweenlands.common.entity.monster.TinySludgeWormHelper;
import thebetweenlands.common.item.misc.MobItem;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.Collection;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class NetItem extends Item {
	public static final Multimap<Class<? extends Entity>, Pair<Supplier<? extends MobItem<?>>, BiPredicate<Player, Entity>>> CATCHABLE_ENTITIES = MultimapBuilder.hashKeys().arrayListValues().build();

	@SuppressWarnings("unchecked")
	public static <T extends Entity> void register(Class<? extends Entity> cls, Supplier<? extends MobItem<T>> item, BiPredicate<Player, T> predicate) {
		CATCHABLE_ENTITIES.put(cls, Pair.of(item, (BiPredicate<Player, Entity>) predicate));
	}

	static {
		register(Firefly.class, ItemRegistry.FIREFLY, (p, e) -> true);
		register(Gecko.class, ItemRegistry.GECKO, (p, e) -> true);
		register(Dragonfly.class, ItemRegistry.DRAGONFLY, (p, e) -> true);
//		register(EntityTinyWormEggSac.class, ItemRegistry.SLUDGE_WORM_EGG_SAC, (p, e) -> true);
//		register(EntityChiromawHatchling.class, ItemRegistry.CHIROMAW_EGG, (p, e) -> !e.getHasHatched() && !e.getElectricBoogaloo());
//		register(EntityChiromawHatchling.class, ItemRegistry.CHIROMAW_EGG_LIGHTNING, (p, e) -> !e.getHasHatched() && e.getElectricBoogaloo());
//		register(EntityChiromawTame.class, ItemRegistry.CHIROMAW_TAME, (p, e) -> e.getOwner() == p && !e.getElectricBoogaloo());
//		register(EntityChiromawTame.class, ItemRegistry.CHIROMAW_TAME_LIGHTNING, (p, e) -> e.getOwner() == p && e.getElectricBoogaloo());
		register(TinySludgeWorm.class, ItemRegistry.TINY_SLUDGE_WORM, (p, e) -> true);
		register(TinySludgeWormHelper.class, ItemRegistry.TINY_SLUDGE_WORM_HELPER, (p, e) -> true);
		register(MireSnail.class, ItemRegistry.MIRE_SNAIL, (p, e) -> true);
		register(Anadia.class, ItemRegistry.ANADIA, (p, e) -> e.getNettableTimer() > 0);
//		register(EntityFreshwaterUrchin.class, ItemRegistry.FRESHWATER_URCHIN, (p, e) -> true);
//		register(EntityCaveFish.class, ItemRegistry.CRITTER, (p, e) -> true);
		register(Olm.class, ItemRegistry.OLM, (p, e) -> true);
		register(Jellyfish.class, ItemRegistry.JELLYFISH, (p, e) -> true);
	}

	public NetItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
		Collection<Pair<Supplier<? extends MobItem<?>>, BiPredicate<Player, Entity>>> entries = CATCHABLE_ENTITIES.get(target.getClass());

		if (entries != null) {
			for (Pair<Supplier<? extends MobItem<?>>, BiPredicate<Player, Entity>> entry : entries) {
				if (entry.getSecond().test(player, target)) {
					MobItem<?> item = entry.getFirst().get();

					if (!player.level().isClientSide()) {
						ItemStack mobItemStack = item.capture(target);

						if (!mobItemStack.isEmpty()) {
							target.discard();
							if (!player.getInventory().add(mobItemStack)) {
								player.drop(mobItemStack, false);
							}
							stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
							item.onCapturedByPlayer(player, hand, mobItemStack, target);
						}
					}
					return InteractionResult.sidedSuccess(player.level().isClientSide());
				}
			}
		}

		return InteractionResult.PASS;
	}
}

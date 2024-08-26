package thebetweenlands.common.items;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.entities.fishing.BLFishHook;
import thebetweenlands.common.entities.fishing.anadia.Anadia;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.Collection;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class NetItem extends Item {
	public static final Multimap<Class<? extends Entity>, Pair<Supplier<? extends MobItem>, BiPredicate<Player, Entity>>> CATCHABLE_ENTITIES = MultimapBuilder.hashKeys().arrayListValues().build();

	@SuppressWarnings("unchecked")
	public static <T extends Entity> void register(Class<T> cls, Supplier<? extends MobItem> item, BiPredicate<Player, T> predicate) {
		CATCHABLE_ENTITIES.put(cls, Pair.of(item, (BiPredicate<Player, Entity>) predicate));
	}

	static {
//		register(EntityFirefly.class, ItemRegistry.CRITTER, (p, e) -> true);
//		register(EntityGecko.class, ItemRegistry.CRITTER, (p, e) -> true);
//		register(EntityDragonFly.class, ItemRegistry.CRITTER, (p, e) -> true);
//		register(EntityTinyWormEggSac.class, ItemRegistry.SLUDGE_WORM_EGG_SAC, (p, e) -> true);
//		register(EntityChiromawHatchling.class, ItemRegistry.CHIROMAW_EGG, (p, e) -> !e.getHasHatched() && !e.getElectricBoogaloo());
//		register(EntityChiromawHatchling.class, ItemRegistry.CHIROMAW_EGG_LIGHTNING, (p, e) -> !e.getHasHatched() && e.getElectricBoogaloo());
//		register(EntityChiromawTame.class, ItemRegistry.CHIROMAW_TAME, (p, e) -> e.getOwner() == p && !e.getElectricBoogaloo());
//		register(EntityChiromawTame.class, ItemRegistry.CHIROMAW_TAME_LIGHTNING, (p, e) -> e.getOwner() == p && e.getElectricBoogaloo());
//		register(EntityTinySludgeWorm.class, ItemRegistry.TINY_SLUDGE_WORM, (p, e) -> true);
//		register(EntityTinySludgeWormHelper.class, ItemRegistry.TINY_SLUDGE_WORM_HELPER, (p, e) -> true);
//		register(EntityMireSnail.class, ItemRegistry.CRITTER, (p, e) -> true);
		register(Anadia.class, ItemRegistry.ANADIA, (p, e) -> e.getNettableTimer() > 0);
//		register(EntityFreshwaterUrchin.class, ItemRegistry.FRESHWATER_URCHIN, (p, e) -> true);
//		register(EntityCaveFish.class, ItemRegistry.CRITTER, (p, e) -> true);
//		register(EntityOlm.class, ItemRegistry.CRITTER, (p, e) -> true);
//		register(EntityJellyfish.class, ItemRegistry.JELLYFISH, (p, e) -> true);
	}

	public NetItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
		Collection<Pair<Supplier<? extends MobItem>, BiPredicate<Player, Entity>>> entries = CATCHABLE_ENTITIES.get(target.getClass());

		if (entries != null) {
			for (Pair<Supplier<? extends MobItem>, BiPredicate<Player, Entity>> entry : entries) {
				if (entry.getSecond().test(player, target)) {
					MobItem item = entry.getFirst().get();
					player.swing(hand);

					if (!player.level().isClientSide()) {
						ItemStack mobItemStack = item.capture(target);

						if (!mobItemStack.isEmpty()) {
							target.discard();
							player.level().addFreshEntity(new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), mobItemStack));
							stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
							item.onCapturedByPlayer(player, hand, mobItemStack, target);
							return InteractionResult.SUCCESS;
						}
					} else {
						return InteractionResult.SUCCESS;
					}
				}
			}
		}

		return InteractionResult.PASS;
	}
}

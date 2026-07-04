package thebetweenlands.common.dispenser;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.DispenserBlock;
import thebetweenlands.common.registries.ItemRegistry;

public class BetweenlandsDispenserBehaviours {

	public static void registerBehaviours() {
		DispenserBlock.registerBehavior(ItemRegistry.WEEDWOOD_BUCKET, new BucketDispenseBehavior());
		DispenserBlock.registerBehavior(ItemRegistry.SYRMORITE_BUCKET, new BucketDispenseBehavior());

		registerCustomSoundProjectileBehaviour(ItemRegistry.ANGRY_PEBBLE);
		registerCustomSoundProjectileBehaviour(ItemRegistry.SILKY_PEBBLE);

		registerMultipleProjectileBehaviour(ItemRegistry.PYRAD_FLAME, 1, 6);
		
		DispenserBlock.registerBehavior(ItemRegistry.OCTINE_INGOT, new OctineIngotDispenseBehaviour());

		DispenserBlock.registerProjectileBehavior(ItemRegistry.ANGLER_TOOTH_ARROW);
		DispenserBlock.registerProjectileBehavior(ItemRegistry.POISONED_ANGLER_TOOTH_ARROW);
		DispenserBlock.registerProjectileBehavior(ItemRegistry.OCTINE_ARROW);
		DispenserBlock.registerProjectileBehavior(ItemRegistry.BASILISK_ARROW);
		DispenserBlock.registerProjectileBehavior(ItemRegistry.SLUDGE_WORM_ARROW);
		DispenserBlock.registerProjectileBehavior(ItemRegistry.SHOCK_ARROW);
		DispenserBlock.registerProjectileBehavior(ItemRegistry.CHIROMAW_BARB);
	}

	public static void registerCustomSoundProjectileBehaviour(ItemLike itemLike) {
		DispenserBlock.registerBehavior(itemLike, new CustomSoundProjectileDispenseBehavior(itemLike.asItem()));
	}

	public static void registerMultipleProjectileBehaviour(ItemLike itemLike, int minProjectiles, int randomProjectiles) {
		DispenserBlock.registerBehavior(itemLike, new MultipleProjectileDispenseBehaviour(itemLike.asItem(), minProjectiles, randomProjectiles));
	}
	
}

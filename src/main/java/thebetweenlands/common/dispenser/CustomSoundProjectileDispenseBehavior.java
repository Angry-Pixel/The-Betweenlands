package thebetweenlands.common.dispenser;

import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.world.item.Item;

public class CustomSoundProjectileDispenseBehavior extends ProjectileDispenseBehavior {

	protected final CustomDispenseSoundItem customSoundItem;
	
	public CustomSoundProjectileDispenseBehavior(Item projectile) {
		super(projectile);
		if(projectile instanceof CustomDispenseSoundItem customSoundItem) {
			this.customSoundItem = customSoundItem;
		} else {
			throw new IllegalArgumentException(projectile + " not instance of " + CustomDispenseSoundItem.class.getSimpleName());
		}
	}
	
	@Override
	protected void playSound(BlockSource blockSource) {
		this.customSoundItem.playSound(blockSource);
	}

}

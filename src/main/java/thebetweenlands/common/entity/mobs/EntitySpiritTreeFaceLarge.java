package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

public class EntitySpiritTreeFaceLarge extends EntitySpiritTreeFace {
	public EntitySpiritTreeFaceLarge(World world) {
		super(world);
		this.setSize(1.8F, 1.8F);
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0D);
	}
}

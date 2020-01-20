package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.monster.IMob;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntitySpiritTreeFaceSmall extends EntitySpiritTreeFaceSmallBase implements IMob {
	public EntitySpiritTreeFaceSmall(World world) {
		super(world);
	}

	@Override
	public boolean isActive() {
		return this.world.getDifficulty() != EnumDifficulty.PEACEFUL;
	}
}

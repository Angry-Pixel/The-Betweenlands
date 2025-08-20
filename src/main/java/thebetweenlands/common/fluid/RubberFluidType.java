package thebetweenlands.common.fluid;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import thebetweenlands.common.datagen.tags.BLEntityTagProvider;

public class RubberFluidType extends FluidType {

	public RubberFluidType(Properties properties) {
		super(properties);
	}
	
	@Override
	public boolean move(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity) {
		boolean moved = super.move(state, entity, movementVector, gravity);
		if(!entity.getType().is(BLEntityTagProvider.IMMUNE_TO_RUBBER_SLOWDOWN) && !entity.isSpectator()) {
			Vec3 deltaMovement = entity.getDeltaMovement();
			entity.setDeltaMovement(
					deltaMovement.x() * 0.35D,
					deltaMovement.y() * 0.8D - 0.01D,
					deltaMovement.z() * 0.35D
				);
		}
		return moved;
	}
	
}

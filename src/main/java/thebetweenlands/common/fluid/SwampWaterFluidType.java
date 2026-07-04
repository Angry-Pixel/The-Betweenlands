package thebetweenlands.common.fluid;

import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.fluids.FluidType;
import thebetweenlands.common.component.entity.GunkData;
import thebetweenlands.common.registries.AttachmentRegistry;

public class SwampWaterFluidType extends FluidType {

	public SwampWaterFluidType(Properties properties) {
		super(properties);
	}

	@Override
	public boolean canSwim(Entity entity) {
		if(entity.hasData(AttachmentRegistry.GUNK)) {
			GunkData gunkData = entity.getData(AttachmentRegistry.GUNK);
			
			if(gunkData.isSwimmingBlocked()) {
				return false;
			}
		}
		return super.canSwim(entity);
	}
	
}

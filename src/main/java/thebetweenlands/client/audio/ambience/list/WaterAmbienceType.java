package thebetweenlands.client.audio.ambience.list;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import thebetweenlands.client.audio.ambience.AmbienceType;
import thebetweenlands.common.registries.SoundRegistry;

public class WaterAmbienceType extends AmbienceType {
	@Override
	public boolean isActive() {
		//Get the view block
		IBlockState viewBlockState = ActiveRenderInfo.getBlockStateAtEntityViewpoint(this.getPlayer().worldObj, this.getPlayer(), 1);
		if (viewBlockState.getMaterial().isLiquid()) {
			return true;
		}
		return false;
	}

	@Override
	public EnumAmbienceLayer getAmbienceLayer() {
		return EnumAmbienceLayer.LAYER1;
	}

	@Override
	public int getPriority() {
		return 1;
	}

	@Override
	public int getFadeTime() {
		return 5;
	}

	@Override
	public float getLowerPriorityVolume() {
		return 0.1F;
	}

	@Override
	public SoundCategory getCategory() {
		return SoundCategory.AMBIENT;
	}

	@Override
	public SoundEvent getSound() {
		return SoundRegistry.AMBIENT_WATER;
	}
}

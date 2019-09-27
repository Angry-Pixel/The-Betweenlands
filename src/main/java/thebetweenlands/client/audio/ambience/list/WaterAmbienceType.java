package thebetweenlands.client.audio.ambience.list;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import thebetweenlands.client.audio.ambience.AmbienceLayer;
import thebetweenlands.client.audio.ambience.AmbienceType;
import thebetweenlands.common.registries.AmbienceRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class WaterAmbienceType extends AmbienceType {
	private final boolean baseLayer;
	
	public WaterAmbienceType(boolean baseLayer) {
		this.baseLayer = baseLayer;
	}
	
	@Override
	public boolean isActive() {
		//Get the view block
		IBlockState viewBlockState = ActiveRenderInfo.getBlockStateAtEntityViewpoint(this.getPlayer().world, this.getPlayer(), 1);
		if (viewBlockState.getMaterial().isLiquid()) {
			return true;
		}
		return false;
	}

	@Override
	public AmbienceLayer getAmbienceLayer() {
		return this.baseLayer ? AmbienceRegistry.BASE_LAYER : AmbienceRegistry.DETAIL_LAYER;
	}

	@Override
	public float getVolume() {
		return this.baseLayer ? 0.0f : 1.0f;
	}
	
	@Override
	public int getPriority() {
		return 2;
	}

	@Override
	public int getFadeTime() {
		return 5;
	}

	@Override
	public SoundCategory getCategory() {
		return SoundCategory.AMBIENT;
	}

	@Override
	public SoundEvent getSound() {
		return SoundRegistry.AMBIENT_WATER;
	}

	@Override
	public float getLowerPriorityVolume() {
		//Decrease volume of other ambiences (e.g. surface and cave ambience)
		return 0.1F;
	}
}

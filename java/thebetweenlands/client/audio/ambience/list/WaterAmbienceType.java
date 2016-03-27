package thebetweenlands.client.audio.ambience.list;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import thebetweenlands.client.audio.ambience.AmbienceType;

public class WaterAmbienceType extends AmbienceType {
	@Override
	public boolean isActive() {
		//Get the view block
		Vec3 vec = Vec3.createVectorHelper(RenderManager.renderPosX, RenderManager.renderPosY, RenderManager.renderPosZ);
		ChunkPosition chunkposition = new ChunkPosition(vec);
		Block viewBlock = this.getPlayer().worldObj.getBlock(chunkposition.chunkPosX, chunkposition.chunkPosY, chunkposition.chunkPosZ);
		if (viewBlock.getMaterial().isLiquid()) {
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
	public ResourceLocation getSound() {
		return new ResourceLocation("thebetweenlands:ambientWater");
	}

	@Override
	public int getFadeTime() {
		return 5;
	}

	@Override
	public float getLowerPriorityVolume() {
		return 0.1F;
	}
}

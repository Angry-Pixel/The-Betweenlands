package thebetweenlands.common.entity.monster.wall;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class WallSpriteInfo {

	@Nullable
	private TextureAtlasSprite wallSprite;

	protected void updateWallSprite(AbstractWallCreature creature) {
		this.wallSprite = null;

		BlockPos pos = creature.blockPosition();

		BlockState state = creature.level().getBlockState(pos);

		if (state.isRedstoneConductor(creature.level(), pos)) {
			this.wallSprite = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getTexture(state, creature.level(), pos);
		}
	}

	@Nullable
	public TextureAtlasSprite getWallSprite() {
		return this.wallSprite;
	}
}

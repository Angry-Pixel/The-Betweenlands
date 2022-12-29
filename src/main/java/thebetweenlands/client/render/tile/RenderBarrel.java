package thebetweenlands.client.render.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.animation.FastTESR;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.tile.TileEntityBarrel;

public class RenderBarrel extends FastTESR<TileEntityBarrel> {
	public static final ResourceLocation WHITE_SPRITE_PATH = new ResourceLocation(ModInfo.ID, "tiles/white");

	@Override
	public void renderTileEntityFast(TileEntityBarrel te, double x, double y, double z, float partialTicks, int destroyStage, float partial, BufferBuilder buffer) {
		IFluidTankProperties props = te.getTankProperties()[0];
		FluidStack fluid = props.getContents();

		if(fluid != null && fluid.amount > 0) {
			ResourceLocation texture = fluid.getFluid().getStill();

			if(texture == null) {
				texture = WHITE_SPRITE_PATH;
			}

			TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture.toString());

			int maxAmount = props.getCapacity();

			World world = te.getWorld();
			BlockPos pos = te.getPos();

			IBlockState state = world.getBlockState(pos);

			int packedLightmap = state.getPackedLightmapCoords(world, pos);
			int skyLight = packedLightmap >> 16 & 65535;
			int blockLight = packedLightmap & 65535;

			float height = fluid.amount / (float)maxAmount * 0.8f + 0.13f;

			int color = fluid.getFluid().getColor(fluid);

			float r = ((color >> 16) & 0xFF) / 255f;
			float g = ((color >> 8) & 0xFF) / 255f;
			float b = ((color >> 0) & 0xFF) / 255f;
			float a = ((color >> 24) & 0xFF) / 255f;

			float minU = sprite.getMinU();
			float minV = sprite.getMinV();
			float maxU = sprite.getMaxU();
			float maxV = sprite.getMaxV();

			float width = 0.5F;
			float depth = 0.5F;

			float offX = (1 - width) / 2.0f;
			float offZ = (1 - depth) / 2.0f;

			buffer.setTranslation(x, y, z);

			buffer.pos(offX, height, offZ).color(r, g, b, a).tex(minU, maxV).lightmap(skyLight, blockLight).endVertex();
			buffer.pos(offX, height, 1 - offZ).color(r, g, b, a).tex(minU, minV).lightmap(skyLight, blockLight).endVertex();
			buffer.pos(1 - offX, height, 1 - offZ).color(r, g, b, a).tex(maxU, minV).lightmap(skyLight, blockLight).endVertex();
			buffer.pos(1 - offX, height, offZ).color(r, g, b, a).tex(maxU, maxV).lightmap(skyLight, blockLight).endVertex();

			buffer.setTranslation(0, 0, 0);
		}
	}
}

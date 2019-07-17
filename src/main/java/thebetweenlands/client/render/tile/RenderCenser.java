package thebetweenlands.client.render.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.postprocessing.GroundFog.GroundFogVolume;
import thebetweenlands.common.tile.TileEntityCenser;

public class RenderCenser extends TileEntitySpecialRenderer<TileEntityCenser> {
	@Override
	public void render(TileEntityCenser te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if(ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();

			float fogBrightness = 0.85F;
			float inScattering = 0.025F;
			float extinction = 2.5F;

			BlockPos pos = te.getPos();

			float width = 12.0F;
			float height = 12.0F;
			
			ShaderHelper.INSTANCE.getWorldShader().addGroundFogVolume(new GroundFogVolume(new Vec3d(pos.getX() + 0.5D - width / 2.0F, pos.getY() - 0.1D, pos.getZ() + 0.5D - width / 2.0F), new Vec3d(width, height, width), inScattering, extinction, fogBrightness, fogBrightness, fogBrightness));
		}
	}
}

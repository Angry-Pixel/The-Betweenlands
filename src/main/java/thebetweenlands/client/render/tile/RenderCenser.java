package thebetweenlands.client.render.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.api.recipes.ICenserRecipe;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.postprocessing.GroundFog.GroundFogVolume;
import thebetweenlands.common.tile.TileEntityCenser;

public class RenderCenser extends TileEntitySpecialRenderer<TileEntityCenser> {
	@Override
	public boolean isGlobalRenderer(TileEntityCenser te) {
		return true;
	}

	@Override
	public void render(TileEntityCenser te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if(ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();

			float strength = te.getDungeonFogStrength(partialTicks);

			float fogBrightness = 0.85F;
			float inScattering = 0.025F * strength;
			float extinction = 2.5F + 5.0F * (1 - strength);

			AxisAlignedBB fogArea = te.getFogRenderArea();

			ShaderHelper.INSTANCE.getWorldShader().addGroundFogVolume(new GroundFogVolume(new Vec3d(fogArea.minX, fogArea.minY, fogArea.minZ), new Vec3d(fogArea.maxX - fogArea.minX, fogArea.maxY - fogArea.minY, fogArea.maxZ - fogArea.minZ), inScattering, extinction, fogBrightness, fogBrightness, fogBrightness));
		}

		ICenserRecipe<Object> recipe = te.getCurrentRecipe();
		if(recipe != null) {
			recipe.render(te.getCurrentRecipeContext(), te.getCurrentRecipeInputAmount(), te, x, y, z, partialTicks);
		}
	}
}

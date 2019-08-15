package thebetweenlands.common.recipe.censer;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fluids.FluidStack;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.postprocessing.GroundFog.GroundFogVolume;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.FluidRegistry;

public class CenserRecipeStagnantWater extends AbstractCenserRecipe<Void> {
	private static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "stagnant_water");

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public boolean matchesInput(FluidStack stack) {
		return stack.getFluid() == FluidRegistry.STAGNANT_WATER;
	}

	@Override
	public void render(Void context, int amountLeft, TileEntity censer, double x, double y, double z, float partialTicks) {
		if(ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();

			float fogBrightness = 0.85F;
			float inScattering = 0.04F;
			float extinction = 3F;

			AxisAlignedBB fogArea = new AxisAlignedBB(censer.getPos()).grow(6, 0.1D, 6).expand(0, 12, 0);

			ShaderHelper.INSTANCE.getWorldShader().addGroundFogVolume(new GroundFogVolume(new Vec3d(fogArea.minX, fogArea.minY, fogArea.minZ), new Vec3d(fogArea.maxX - fogArea.minX, fogArea.maxY - fogArea.minY, fogArea.maxZ - fogArea.minZ), inScattering, extinction, fogBrightness * 0.7F, fogBrightness * 0.7F, fogBrightness * 0.5F));
		}
	}
	
	@Override
	public int getEffectColor(Void context, int amountLeft, TileEntity censer, EffectColorType type) {
		return type == EffectColorType.FOG ? 0xFFFFFFAA : super.getEffectColor(context, amountLeft, censer, type);
	}
}

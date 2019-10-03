package thebetweenlands.common.recipe.censer;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.postprocessing.GroundFog.GroundFogVolume;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
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

	@SideOnly(Side.CLIENT)
	@Override
	public void render(Void context, int amountLeft, TileEntity censer, boolean running, float effectStrength, double x, double y, double z, float partialTicks) {
		if(effectStrength > 0.01F && ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();

			float fogBrightness = 0.85F;
			float inScattering = 0.04F * effectStrength;
			float extinction = 3F;

			AxisAlignedBB fogArea = new AxisAlignedBB(censer.getPos()).grow(6, 0.1D, 6).expand(0, 12, 0);

			ShaderHelper.INSTANCE.getWorldShader().addGroundFogVolume(new GroundFogVolume(new Vec3d(fogArea.minX, fogArea.minY, fogArea.minZ), new Vec3d(fogArea.maxX - fogArea.minX, fogArea.maxY - fogArea.minY, fogArea.maxZ - fogArea.minZ), inScattering, extinction, fogBrightness * 0.7F, fogBrightness * 0.7F, fogBrightness * 0.5F));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getEffectColor(Void context, int amountLeft, TileEntity censer, EffectColorType type) {
		return 0xFFFFFFAA;
	}

	private List<EntityLivingBase> getAffectedEntities(World world, BlockPos pos) {
		return world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos).grow(6, 0.1D, 6).expand(0, 12, 0));
	}

	@Override
	public int update(Void context, int amountLeft, TileEntity censer) {
		World world = censer.getWorld();

		if(!world.isRemote && world.getTotalWorldTime() % 100 == 0) {
			BlockPos pos = censer.getPos();

			List<EntityLivingBase> affected = this.getAffectedEntities(world, pos);
			for(EntityLivingBase living : affected) {
				living.addPotionEffect(ElixirEffectRegistry.EFFECT_DECAY.createEffect(200, 1));
			}
		}

		return 0;
	}
	
	@Override
	public int getConsumptionDuration(Void context, int amountLeft, TileEntity censer) {
		return 30;
	}
}

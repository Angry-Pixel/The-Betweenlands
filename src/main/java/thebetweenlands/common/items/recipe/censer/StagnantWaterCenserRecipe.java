package thebetweenlands.common.items.recipe.censer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import thebetweenlands.api.block.Censer;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.client.shader.postprocessing.GroundFog;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.FluidRegistry;

import java.util.List;

public class StagnantWaterCenserRecipe extends AbstractCenserRecipe<Void> {

	@Override
	public boolean matchesInput(FluidStack stack) {
		return stack.is(FluidRegistry.STAGNANT_WATER_STILL);
	}

	@Override
	public void render(Void context, Censer censer, double x, double y, double z, float partialTicks) {
		float effectStrength = censer.getEffectStrength(partialTicks);

		if(effectStrength > 0.01F && ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();

			float fogBrightness = 0.85F;
			float inScattering = 0.04F * effectStrength;
			float extinction = 3F;

			AABB fogArea = new AABB(censer.getBlockPos()).inflate(6, 0.1D, 6).expandTowards(0, 12, 0);

			ShaderHelper.INSTANCE.getWorldShader().addGroundFogVolume(new GroundFog.GroundFogVolume(new Vec3(fogArea.minX, fogArea.minY, fogArea.minZ), new Vec3(fogArea.maxX - fogArea.minX, fogArea.maxY - fogArea.minY, fogArea.maxZ - fogArea.minZ), inScattering, extinction, fogBrightness * 0.7F, fogBrightness * 0.7F, fogBrightness * 0.5F));
		}
	}

	@Override
	public int getEffectColor(Void context, Censer censer, EffectColorType type) {
		return 0xFFFFFFAA;
	}

	private List<LivingEntity> getAffectedEntities(Level level, BlockPos pos) {
		return level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(6, 0.1D, 6).expandTowards(0, 12, 0));
	}

	@Override
	public int update(Void context, Censer censer) {
		Level level = censer.getLevel();

		if(!level.isClientSide() && level.getGameTime() % 100 == 0) {
			BlockPos pos = censer.getBlockPos();

			List<LivingEntity> affected = this.getAffectedEntities(level, pos);
			for(LivingEntity living : affected) {
				living.addEffect(ElixirEffectRegistry.EFFECT_DECAY.get().createEffect(200, 1));
			}
		}

		return 0;
	}

	@Override
	public int getConsumptionDuration(Void context, Censer censer) {
		return 30;
	}
}

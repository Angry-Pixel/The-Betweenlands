package thebetweenlands.common.items.recipe.censer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.api.aspect.AspectContainerItem;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.api.block.Censer;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.client.shader.postprocessing.GroundFog;
import thebetweenlands.common.component.item.AspectContents;
import thebetweenlands.common.items.AspectVialItem;
import thebetweenlands.common.registries.DataComponentRegistry;

public class AspectCenserRecipe extends AbstractCenserRecipe<AspectCenserRecipe.CenserRecipeAspectContext> {

	@Override
	public boolean matchesInput(ItemStack stack) {
		return stack.getOrDefault(DataComponentRegistry.ASPECT_CONTENTS, AspectContents.EMPTY).aspect().isPresent();
	}

	@Override
	public int getInputAmount(ItemStack stack) {
		return Math.min(stack.getOrDefault(DataComponentRegistry.ASPECT_CONTENTS, AspectContents.EMPTY).amount() / 5, 1000);
	}

	@Override
	public CenserRecipeAspectContext createContext(ItemStack stack) {
		return new CenserRecipeAspectContext(stack);
	}

	@Override
	public int getConsumptionDuration(CenserRecipeAspectContext context, Censer censer) {
		//20 min. / 1000 aspect
		return 24 * 5;
	}

	@Override
	public void render(CenserRecipeAspectContext context, Censer censer, BlockPos pos, float partialTicks) {
		float effectStrength = censer.getEffectStrength(partialTicks);

		if(effectStrength > 0.01F && ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();

			float fogBrightness = 0.4F;
			float inScattering = 0.018F * effectStrength;
			float extinction = 2.5F;

			AABB fogArea = new AABB(censer.getBlockPos()).inflate(6, 0.1D, 6).expandTowards(0, 12, 0);

			int fogColor = this.getEffectColor(context, censer, EffectColorType.FOG);

			float r = ((fogColor >> 16) & 0xFF) / 255f;
			float g = ((fogColor >> 8) & 0xFF) / 255f;
			float b = ((fogColor >> 0) & 0xFF) / 255f;

			ShaderHelper.INSTANCE.getWorldShader().addGroundFogVolume(new GroundFog.GroundFogVolume(new Vec3(fogArea.minX, fogArea.minY, fogArea.minZ), new Vec3(fogArea.maxX - fogArea.minX, fogArea.maxY - fogArea.minY, fogArea.maxZ - fogArea.minZ), inScattering, extinction, fogBrightness * r, fogBrightness * g, fogBrightness * b));
		}
	}

	@Override
	public int getEffectColor(CenserRecipeAspectContext context, Censer censer, EffectColorType type) {
		return context.stack().getOrDefault(DataComponentRegistry.ASPECT_CONTENTS, AspectContents.EMPTY).getAspectColor();
	}

	@Override
	public Holder<AspectType> getAspectFogType(CenserRecipeAspectContext context, Censer censer) {
		return context.stack().getOrDefault(DataComponentRegistry.ASPECT_CONTENTS, AspectContents.EMPTY).aspect().orElse(null);
	}

	public record CenserRecipeAspectContext(ItemStack stack) {
	}
}

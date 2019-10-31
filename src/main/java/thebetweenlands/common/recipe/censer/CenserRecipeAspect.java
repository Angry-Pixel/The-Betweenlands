package thebetweenlands.common.recipe.censer;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.api.block.ICenser;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.postprocessing.GroundFog.GroundFogVolume;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.ItemRegistry;

public class CenserRecipeAspect extends AbstractCenserRecipe<CenserRecipeAspectContext> {
	private static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "aspect");

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public boolean matchesInput(ItemStack stack) {
		return stack.getItem() == ItemRegistry.ASPECT_VIAL;
	}

	@Override
	public int getInputAmount(ItemStack stack) {
		return Math.min(ItemAspectContainer.fromItem(stack).getAspects().get(0).amount / 5, 1000);
	}

	@Override
	public CenserRecipeAspectContext createContext(ItemStack stack) {
		return new CenserRecipeAspectContext(ItemAspectContainer.fromItem(stack).getAspects().get(0).type);
	}

	@Override
	public int getConsumptionDuration(CenserRecipeAspectContext context, ICenser censer) {
		//20 min. / 1000 aspect
		return 24 * 5;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void render(CenserRecipeAspectContext context, ICenser censer, double x, double y, double z, float partialTicks) {
		float effectStrength = censer.getEffectStrength(partialTicks);

		if(effectStrength > 0.01F && ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();

			float fogBrightness = 0.4F;
			float inScattering = 0.018F * effectStrength;
			float extinction = 2.5F;

			AxisAlignedBB fogArea = new AxisAlignedBB(censer.getPos()).grow(6, 0.1D, 6).expand(0, 12, 0);

			int fogColor = this.getEffectColor(context, censer, EffectColorType.FOG);

			float r = ((fogColor >> 16) & 0xFF) / 255f;
			float g = ((fogColor >> 8) & 0xFF) / 255f;
			float b = ((fogColor >> 0) & 0xFF) / 255f;

			ShaderHelper.INSTANCE.getWorldShader().addGroundFogVolume(new GroundFogVolume(new Vec3d(fogArea.minX, fogArea.minY, fogArea.minZ), new Vec3d(fogArea.maxX - fogArea.minX, fogArea.maxY - fogArea.minY, fogArea.maxZ - fogArea.minZ), inScattering, extinction, fogBrightness * r, fogBrightness * g, fogBrightness * b));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getEffectColor(CenserRecipeAspectContext context, ICenser censer, EffectColorType type) {
		return context.type.getColor();
	}

	@Override
	public IAspectType getAspectFogType(CenserRecipeAspectContext context, ICenser censer) {
		return context.type;
	}
}

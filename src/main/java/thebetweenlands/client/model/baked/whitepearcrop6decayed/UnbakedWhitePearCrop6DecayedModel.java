package thebetweenlands.client.model.baked.whitepearcrop6decayed;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

public class UnbakedWhitePearCrop6DecayedModel implements IUnbakedGeometry<UnbakedWhitePearCrop6DecayedModel> {

	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
		return new WhitePearCrop6DecayedModel(spriteGetter.apply(context.getMaterial("texture")), context.getTransforms(), context.getRootTransform());
	}
}


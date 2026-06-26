package thebetweenlands.client.model.baked.aspectruscrop;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

public class UnbakedAspectrusCropModel implements IUnbakedGeometry<UnbakedAspectrusCropModel> {

	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
		return new AspectrusCropModel(spriteGetter.apply(context.getMaterial("texture")), spriteGetter.apply(context.getMaterial("aspectrus_crop_1")), 
			spriteGetter.apply(context.getMaterial("aspectrus_crop_2")), spriteGetter.apply(context.getMaterial("aspectrus_crop_3")), 
			spriteGetter.apply(context.getMaterial("aspectrus_crop_4")), spriteGetter.apply(context.getMaterial("aspectrus_crop_5")), 
			spriteGetter.apply(context.getMaterial("aspectrus_crop_6")), spriteGetter.apply(context.getMaterial("fence")),
			spriteGetter.apply(context.getMaterial("particle")), context.getTransforms(), context.getRootTransform());
	}
}


package thebetweenlands.client.model.baked.woodensupportbeam3;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;

import java.util.function.Function;

import org.joml.Vector3f;

import com.mojang.math.Transformation;

public class UnbakedWoodenSupportBeam3Model implements IUnbakedGeometry<UnbakedWoodenSupportBeam3Model> {

	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
		Transformation transform = UnbakedGeometryHelper
        .composeRootTransformIntoModelState(modelState, context.getRootTransform())
        .getRotation()
        .applyOrigin(new Vector3f(0.5F, 0.5F, 0.5F));
        return new WoodenSupportBeam3Model(
            spriteGetter.apply(context.getMaterial("texture")), spriteGetter.apply(context.getMaterial("particle")), 
            context.getTransforms(),
            transform);
	}
}


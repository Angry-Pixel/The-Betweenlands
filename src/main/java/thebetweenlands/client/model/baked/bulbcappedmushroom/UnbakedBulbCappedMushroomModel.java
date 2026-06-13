package thebetweenlands.client.model.baked.bulbcappedmushroom;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import java.util.function.Function;

public class UnbakedBulbCappedMushroomModel implements IUnbakedGeometry<UnbakedBulbCappedMushroomModel> {

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker,
            Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
        return new BulbCappedMushroomModel(
            spriteGetter.apply(context.getMaterial("texture")),
            context.getTransforms(),
            context.getRootTransform());
            //TODO: make it so it will take one rotation at random (rn it always faces in one direction)
    }
}


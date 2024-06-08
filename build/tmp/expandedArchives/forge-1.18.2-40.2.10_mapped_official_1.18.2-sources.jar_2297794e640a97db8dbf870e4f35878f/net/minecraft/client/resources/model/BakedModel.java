package net.minecraft.client.resources.model;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface BakedModel extends net.minecraftforge.client.extensions.IForgeBakedModel {
   /**@deprecated Forge: Use {@link net.minecraftforge.client.extensions.IForgeBakedModel#getQuads(BlockState, Direction, Random, net.minecraftforge.client.model.data.IModelData)}*/
   @Deprecated
   List<BakedQuad> getQuads(@Nullable BlockState p_119123_, @Nullable Direction p_119124_, Random p_119125_);

   boolean useAmbientOcclusion();

   boolean isGui3d();

   boolean usesBlockLight();

   boolean isCustomRenderer();

   /**@deprecated Forge: Use {@link net.minecraftforge.client.extensions.IForgeBakedModel#getParticleIcon(net.minecraftforge.client.model.data.IModelData)}*/
   @Deprecated
   TextureAtlasSprite getParticleIcon();

   /**@deprecated Forge: Use {@link net.minecraftforge.client.extensions.IForgeBakedModel#handlePerspective(ItemTransforms.TransformType, com.mojang.blaze3d.vertex.PoseStack)} instead */
   @Deprecated
   default ItemTransforms getTransforms() { return ItemTransforms.NO_TRANSFORMS; }

   ItemOverrides getOverrides();
}

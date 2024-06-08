package net.minecraft.client.resources.model;

import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface UnbakedModel {
   Collection<ResourceLocation> getDependencies();

   Collection<Material> getMaterials(Function<ResourceLocation, UnbakedModel> p_119538_, Set<Pair<String, String>> p_119539_);

   @Nullable
   BakedModel bake(ModelBakery p_119534_, Function<Material, TextureAtlasSprite> p_119535_, ModelState p_119536_, ResourceLocation p_119537_);
}
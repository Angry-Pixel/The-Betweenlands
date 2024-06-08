package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UndeadHorseRenderer extends AbstractHorseRenderer<AbstractHorse, HorseModel<AbstractHorse>> {
   private static final Map<EntityType<?>, ResourceLocation> MAP = Maps.newHashMap(ImmutableMap.of(EntityType.ZOMBIE_HORSE, new ResourceLocation("textures/entity/horse/horse_zombie.png"), EntityType.SKELETON_HORSE, new ResourceLocation("textures/entity/horse/horse_skeleton.png")));

   public UndeadHorseRenderer(EntityRendererProvider.Context p_174432_, ModelLayerLocation p_174433_) {
      super(p_174432_, new HorseModel<>(p_174432_.bakeLayer(p_174433_)), 1.0F);
   }

   public ResourceLocation getTextureLocation(AbstractHorse p_116274_) {
      return MAP.get(p_116274_.getType());
   }
}
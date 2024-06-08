package net.minecraft.data.tags;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

public class EntityTypeTagsProvider extends TagsProvider<EntityType<?>> {
   /** @deprecated Forge: Use the {@link #EntityTypeTagsProvider(DataGenerator, String, net.minecraftforge.common.data.ExistingFileHelper) mod id variant} */
   @Deprecated
   public EntityTypeTagsProvider(DataGenerator p_126517_) {
      super(p_126517_, Registry.ENTITY_TYPE);
   }
   public EntityTypeTagsProvider(DataGenerator p_126517_, String modId, @org.jetbrains.annotations.Nullable net.minecraftforge.common.data.ExistingFileHelper existingFileHelper) {
      super(p_126517_, Registry.ENTITY_TYPE, modId, existingFileHelper);
   }

   protected void addTags() {
      this.tag(EntityTypeTags.SKELETONS).add(EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON);
      this.tag(EntityTypeTags.RAIDERS).add(EntityType.EVOKER, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.VINDICATOR, EntityType.ILLUSIONER, EntityType.WITCH);
      this.tag(EntityTypeTags.BEEHIVE_INHABITORS).add(EntityType.BEE);
      this.tag(EntityTypeTags.ARROWS).add(EntityType.ARROW, EntityType.SPECTRAL_ARROW);
      this.tag(EntityTypeTags.IMPACT_PROJECTILES).addTag(EntityTypeTags.ARROWS).add(EntityType.SNOWBALL, EntityType.FIREBALL, EntityType.SMALL_FIREBALL, EntityType.EGG, EntityType.TRIDENT, EntityType.DRAGON_FIREBALL, EntityType.WITHER_SKULL);
      this.tag(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS).add(EntityType.RABBIT, EntityType.ENDERMITE, EntityType.SILVERFISH, EntityType.FOX);
      this.tag(EntityTypeTags.AXOLOTL_HUNT_TARGETS).add(EntityType.TROPICAL_FISH, EntityType.PUFFERFISH, EntityType.SALMON, EntityType.COD, EntityType.SQUID, EntityType.GLOW_SQUID);
      this.tag(EntityTypeTags.AXOLOTL_ALWAYS_HOSTILES).add(EntityType.DROWNED, EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN);
      this.tag(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES).add(EntityType.STRAY, EntityType.POLAR_BEAR, EntityType.SNOW_GOLEM, EntityType.WITHER);
      this.tag(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES).add(EntityType.STRIDER, EntityType.BLAZE, EntityType.MAGMA_CUBE);
   }

   public String getName() {
      return "Entity Type Tags";
   }
}

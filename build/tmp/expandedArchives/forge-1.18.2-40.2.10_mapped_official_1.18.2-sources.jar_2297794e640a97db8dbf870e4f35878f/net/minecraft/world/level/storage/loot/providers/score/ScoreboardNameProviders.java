package net.minecraft.world.level.storage.loot.providers.score;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.Serializer;

public class ScoreboardNameProviders {
   public static final LootScoreProviderType FIXED = register("fixed", new FixedScoreboardNameProvider.Serializer());
   public static final LootScoreProviderType CONTEXT = register("context", new ContextScoreboardNameProvider.Serializer());

   private static LootScoreProviderType register(String p_165874_, Serializer<? extends ScoreboardNameProvider> p_165875_) {
      return Registry.register(Registry.LOOT_SCORE_PROVIDER_TYPE, new ResourceLocation(p_165874_), new LootScoreProviderType(p_165875_));
   }

   public static Object createGsonAdapter() {
      return GsonAdapterFactory.builder(Registry.LOOT_SCORE_PROVIDER_TYPE, "provider", "type", ScoreboardNameProvider::getType).withInlineSerializer(CONTEXT, new ContextScoreboardNameProvider.InlineSerializer()).build();
   }
}
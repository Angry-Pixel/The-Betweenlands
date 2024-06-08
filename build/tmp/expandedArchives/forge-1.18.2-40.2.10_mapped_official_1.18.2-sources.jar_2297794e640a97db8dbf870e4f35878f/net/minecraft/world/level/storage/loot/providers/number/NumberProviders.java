package net.minecraft.world.level.storage.loot.providers.number;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.Serializer;

public class NumberProviders {
   public static final LootNumberProviderType CONSTANT = register("constant", new ConstantValue.Serializer());
   public static final LootNumberProviderType UNIFORM = register("uniform", new UniformGenerator.Serializer());
   public static final LootNumberProviderType BINOMIAL = register("binomial", new BinomialDistributionGenerator.Serializer());
   public static final LootNumberProviderType SCORE = register("score", new ScoreboardValue.Serializer());

   private static LootNumberProviderType register(String p_165739_, Serializer<? extends NumberProvider> p_165740_) {
      return Registry.register(Registry.LOOT_NUMBER_PROVIDER_TYPE, new ResourceLocation(p_165739_), new LootNumberProviderType(p_165740_));
   }

   public static Object createGsonAdapter() {
      return GsonAdapterFactory.builder(Registry.LOOT_NUMBER_PROVIDER_TYPE, "provider", "type", NumberProvider::getType).withInlineSerializer(CONSTANT, new ConstantValue.InlineSerializer()).withDefaultType(UNIFORM).build();
   }
}
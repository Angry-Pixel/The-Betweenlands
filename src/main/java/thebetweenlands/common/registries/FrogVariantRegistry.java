package thebetweenlands.common.registries;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.biome.Biome;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.creature.frog.FrogVariant;

import java.util.List;

public class FrogVariantRegistry {

	public static final ResourceKey<FrogVariant> GREEN = registerKey("green");
	public static final ResourceKey<FrogVariant> PURPLE = registerKey("purple");
	public static final ResourceKey<FrogVariant> BLUE = registerKey("blue");
	public static final ResourceKey<FrogVariant> YELLOW = registerKey("yellow");
	public static final ResourceKey<FrogVariant> POISON = registerKey("poison");

	public static ResourceKey<FrogVariant> registerKey(String name) {
		return ResourceKey.create(BLRegistries.Keys.FROG_VARIANT, TheBetweenlands.prefix(name));
	}

	public static Holder<FrogVariant> getSpawnVariant(RegistryAccess registryAccess, RandomSource random, Holder<Biome> biome) {
		Registry<FrogVariant> registry = registryAccess.registryOrThrow(BLRegistries.Keys.FROG_VARIANT);
		List<Holder.Reference<FrogVariant>> validVariants = registry.holders().filter(variant -> variant.value().biomes().isEmpty() || variant.value().biomes().get().contains(biome)).toList();
		return validVariants.isEmpty() ? registry.getHolderOrThrow(GREEN) : validVariants.get(random.nextInt(validVariants.size()));
	}

	public static void bootstrap(BootstrapContext<FrogVariant> context) {
		context.register(GREEN, new FrogVariant(TheBetweenlands.prefix("textures/entity/frog/green.png")));
		context.register(PURPLE, new FrogVariant(TheBetweenlands.prefix("textures/entity/frog/purple.png")));
		context.register(BLUE, new FrogVariant(TheBetweenlands.prefix("textures/entity/frog/blue.png")));
		context.register(YELLOW, new FrogVariant(TheBetweenlands.prefix("textures/entity/frog/yellow.png")));
		context.register(POISON, new FrogVariant(TheBetweenlands.prefix("textures/entity/frog/poison.png"),
			ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.306F, 0.576F, 0.192F),
			new MobEffectInstance(MobEffects.POISON, 140, 0)));
	}
}

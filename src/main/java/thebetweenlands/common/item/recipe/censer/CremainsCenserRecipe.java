package thebetweenlands.common.item.recipe.censer;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import thebetweenlands.api.block.Censer;
import thebetweenlands.api.recipes.CenserRecipe;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.client.shader.postprocessing.GroundFog;
import thebetweenlands.common.block.entity.CenserBlockEntity;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.Iterator;
import java.util.List;

public class CremainsCenserRecipe extends AbstractCenserRecipe<Void> {

	@Override
	public boolean matchesInput(ItemStack stack) {
		return stack.is(ItemRegistry.CREMAINS);
	}

	@Override
	public void render(Void context, Censer censer, BlockPos pos, float partialTicks) {
		float effectStrength = censer.getEffectStrength(partialTicks);

		if(effectStrength > 0.01F && ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();

			float fogBrightness = 0.13F;
			float inScattering = 0.004F * effectStrength;
			float extinction = 0.15F;

			AABB fogArea = new AABB(censer.getBlockPos()).inflate(6, 0.1D, 6).expandTowards(0, 32, 0);

			ShaderHelper.INSTANCE.getWorldShader().addGroundFogVolume(new GroundFog.GroundFogVolume(new Vec3(fogArea.minX, fogArea.minY, fogArea.minZ), new Vec3(fogArea.maxX - fogArea.minX, fogArea.maxY - fogArea.minY, fogArea.maxZ - fogArea.minZ), inScattering, extinction, fogBrightness * 0.7F, fogBrightness * 0.2F, fogBrightness * 0.1F));
		}
	}

	private List<LivingEntity> getAffectedEntities(Level level, BlockPos pos) {
		return level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(6, 0.1D, 6).expandTowards(0, 12, 0));
	}

	@Override
	public int update(Void context, Censer censer) {
		Level level = censer.getLevel();

		if(!level.isClientSide() && level.getGameTime() % 10 == 0) {
			BlockPos pos = censer.getBlockPos();

			List<LivingEntity> affected = this.getAffectedEntities(level, pos);
			for(LivingEntity living : affected) {
				if(!living.isInWater() && !living.isInvulnerableTo(level.damageSources().inFire()) && !living.fireImmune() && (!(living instanceof Player player) || !player.isCreative())) {
					living.igniteForSeconds(1);
				}
			}
		}

		return 0;
	}

	public static void onLivingDrops(LivingDropsEvent event) {
		LivingEntity entity = event.getEntity();

		boolean isDeathFromCenser = false;

		if(entity != null && entity.isOnFire() && event.getSource().is(DamageTypeTags.IS_FIRE)) {
			int sx = Mth.floor(entity.getX() - 6) >> 4;
			int sz = Mth.floor(entity.getZ() - 6) >> 4;
			int ex = Mth.floor(entity.getX() + 6) >> 4;
			int ez = Mth.floor(entity.getZ() + 6) >> 4;

			check : for(int cx = sx; cx <= ex; cx++) {
				for(int cz = sz; cz <= ez; cz++) {
					ChunkAccess chunk = entity.level().getChunk(cx, cz);

					for(BlockPos pos : chunk.getBlockEntitiesPos()) {

						if(entity.level().getBlockEntity(pos) instanceof CenserBlockEntity censer) {
							if(censer.isRecipeRunning() && ((CenserRecipe<?>) censer.getCurrentRecipe()) instanceof CremainsCenserRecipe) {
								isDeathFromCenser = true;
								break check;
							}
						}
					}
				}
			}
		}

		if(isDeathFromCenser) {
			Iterator<ItemEntity> dropsIT = event.getDrops().iterator();

			while(dropsIT.hasNext()) {
				dropsIT.next();

				if(entity.level().getRandom().nextBoolean()) {
					dropsIT.remove();
				}
			}

			int cremains = entity.level().getRandom().nextInt(3);

			for(int i = 0; i < cremains; i++) {
				event.getDrops().add(new ItemEntity(entity.level(), entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ(), new ItemStack(ItemRegistry.CREMAINS.get())));
			}
		}
	}

	@Override
	public int getConsumptionDuration(Void context, Censer censer) {
		//2.5 min. / item
		return 3;
	}

	@Override
	public int getEffectColor(Void context, Censer censer, EffectColorType type) {
		return 0xFF500000;
	}
}

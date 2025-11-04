package thebetweenlands.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.ParticleRegistry;

public class GreeblingCorpse extends Entity implements IEntityWithComplexSpawn {

	private static final byte EVENT_FADE = 80;

	public float rotation;

	private int lootCount = 0;
	private int maxDrops = 2;

	private boolean fading;
	public int fadeTimer = 0;

	public GreeblingCorpse(EntityType<? extends Entity> type, Level level) {
		super(type, level);
		this.rotation = level.getRandom().nextFloat() * 360.0F;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {

	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		tag.putBoolean("fading", this.fading);
		tag.putInt("loot_count", this.lootCount);
		tag.putInt("max_drops", this.maxDrops);
		tag.putFloat("rotation", this.rotation);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		this.fading = tag.getBoolean("fading");
		this.lootCount = tag.getInt("loot_count");
		this.maxDrops = tag.getInt("max_drops");
		this.rotation = tag.getFloat("rotation");
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public void tick() {
		super.tick();

		double motionY = this.getDeltaMovement().y();

		if (!this.isNoGravity()) {
			motionY -= 0.08D;
		}

		this.move(MoverType.SELF, new Vec3(0.0D, motionY, 0.0D));

		if (this.level().isClientSide() && this.getRandom().nextInt(40) == 0) {
			this.spawnParticles();
		}

		if (this.fading) {
			this.fadeTimer++;
		}

		if (!this.level().isClientSide() && this.fadeTimer > 50) {
			this.discard();
		}
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (id == EVENT_FADE) {
			this.fading = true;
		} else {
			super.handleEntityEvent(id);
		}
	}

	private void spawnParticles() {
		this.level().addParticle(ParticleRegistry.MOSQUITO.get(), this.getX(), this.getY() + this.getBbHeight() / 2, this.getZ(), 0.0D, 0.0D, 0.0D);
	}

	private void spawnBreakParticles() {
		for (int i = 0; i < 12; i++) {
			this.level().addParticle(ParticleRegistry.LEAF.get(),
				this.getX() + (this.getRandom().nextFloat() - 0.5f) * 0.5f,
				this.getY() + this.getBbHeight() / 2,
				this.getZ() + (this.getRandom().nextFloat() - 0.5f) * 0.5f,
				(this.getRandom().nextFloat() - 0.5f) * 0.1f,
				this.getRandom().nextFloat() * 0.05f + 0.05f,
				(this.getRandom().nextFloat() - 0.5f) * 0.1f);
		}
	}

	@Override
	public boolean skipAttackInteraction(Entity entity) {
		if (!this.fading && entity instanceof Player player) {
			if (!this.level().isClientSide() && this.getRandom().nextBoolean()) {
				if (!this.dropLoot(player)) {
					this.fading = true;
					this.level().broadcastEntityEvent(this, EVENT_FADE);
				}
			}

			if (this.level().isClientSide()) {
				this.spawnBreakParticles();
			} else {
				SoundType soundType = SoundType.WOOD;
				this.playSound(soundType.getHitSound(), (soundType.getVolume() + 1.0F) / 4.0F, soundType.getPitch() * 0.5F);
			}
		}

		return true;
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	protected boolean dropLoot(Player player) {
		if (this.level() instanceof ServerLevel level) {
			if (this.lootCount < this.maxDrops) {
				this.lootCount++;
				LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(LootTableRegistry.GREEBLING_CORPSE);
				LootParams lootparams = new LootParams.Builder(level)
					.withParameter(LootContextParams.ORIGIN, this.position())
					.withParameter(LootContextParams.THIS_ENTITY, this)
					.withParameter(LootContextParams.ATTACKING_ENTITY, player)
					.withParameter(LootContextParams.TOOL, player.getMainHandItem())
					.withParameter(LootContextParams.DAMAGE_SOURCE, this.damageSources().playerAttack(player))
					.create(LootContextParamSets.ENTITY);

				for (ItemStack stack : lootTable.getRandomItems(lootparams)) {
					if (!stack.isEmpty()) {
						this.spawnAtLocation(stack, 0.0F);
					}
				}
				return true;
			}
		}

		return false;
	}

	@Override
	public void writeSpawnData(RegistryFriendlyByteBuf buf) {
		buf.writeFloat(this.rotation);
		buf.writeBoolean(this.fading);
		buf.writeInt(this.fadeTimer);
	}

	@Override
	public void readSpawnData(RegistryFriendlyByteBuf buf) {
		this.rotation = buf.readFloat();
		this.fading = buf.readBoolean();
		this.fadeTimer = buf.readInt();
	}
}

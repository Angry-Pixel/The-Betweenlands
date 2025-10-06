package thebetweenlands.common.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.common.registries.EntityRegistry;

public class ShockwaveSwordItemEntity extends ItemEntity {

	private int waveProgress;
	private int lastWaveProgress;

	public ShockwaveSwordItemEntity(EntityType<? extends ItemEntity> type, Level level) {
		super(type, level);
		this.setPickUpDelay(80);
		this.setUnlimitedLifetime();
	}

	public ShockwaveSwordItemEntity(Level level, double x, double y, double z, ItemStack stack) {
		super(level, x, y, z, stack);
		this.setPickUpDelay(80);
		this.setUnlimitedLifetime();
	}

	@Override
	public EntityType<?> getType() {
		return EntityRegistry.SHOCKWAVE_SWORD_ITEM.get();
	}

	@Override
	public void tick() {
		super.tick();
		this.lastWaveProgress = this.waveProgress;
		if (this.waveProgress < 50)
			this.waveProgress++;
	}

	public float getWaveProgress(float partialTicks) {
		return Mth.lerp(partialTicks, this.lastWaveProgress, this.waveProgress);
	}

	@Override
	public float getSpin(float partialTicks) {
		return ((float)this.tickCount + partialTicks) / 20.0F + this.bobOffs;
	}
}

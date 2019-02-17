package thebetweenlands.common.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.registries.EntityRegistry;

public class EntityShockwaveSwordItem extends EntityItem {
	//TODO 1.13 Shockwave sword can't extend EnttiyItem because EntitType is hardcoded
	
	private static final DataParameter<Integer> WAVE_PROGRESS = EntityDataManager.createKey(EntityShockwaveSwordItem.class, DataSerializers.VARINT);

	private int waveProgress;
	private int lastWaveProgress;

	public EntityShockwaveSwordItem(World worldIn) {
		super(EntityRegistry.SHOCKWAVE_SWORD_ITEM, worldIn);
		this.setPickupDelay(80);
		this.setNoDespawn();
		this.setSize(0.25F, 1.0F);
	}

	public EntityShockwaveSwordItem(World worldObj, double posX, double posY, double posZ, ItemStack itemStack) {
		super(EntityRegistry.SHOCKWAVE_SWORD_ITEM, worldObj, posX, posY, posZ, itemStack);
		this.setPickupDelay(80);
		this.setNoDespawn();
	}

	@Override
	protected void registerData() {
		super.registerData();
		this.getDataManager().register(WAVE_PROGRESS, 0);
	}

	@Override
	public void tick() {
		super.tick();
		this.lastWaveProgress = this.waveProgress;
		this.waveProgress = this.getDataManager().get(WAVE_PROGRESS);
		if(this.waveProgress < 50)
			this.getDataManager().set(WAVE_PROGRESS, this.waveProgress + 1);
	}

	@Override
	public void writeAdditional(NBTTagCompound nbt) {
		super.writeAdditional(nbt);
		nbt.setInt("WaveProgress", this.getDataManager().get(WAVE_PROGRESS));
	}

	@Override
	public void readAdditional(NBTTagCompound nbt) {
		super.readAdditional(nbt);
		this.getDataManager().set(WAVE_PROGRESS, nbt.getInt("WaveProgress"));
	}

	@OnlyIn(Dist.CLIENT)
	public float getWaveProgress(float partialTicks) {
		return this.lastWaveProgress + (this.waveProgress - this.lastWaveProgress) * partialTicks;
	}
}

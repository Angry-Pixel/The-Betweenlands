package thebetweenlands.common.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityShockwaveSwordItem extends EntityItem {
	private static final DataParameter<Integer> WAVE_PROGRESS = EntityDataManager.createKey(EntityShockwaveSwordItem.class, DataSerializers.VARINT);

	private int waveProgress;
	private int lastWaveProgress;

	public EntityShockwaveSwordItem(World worldIn) {
		super(worldIn);
		this.setPickupDelay(80);
		this.setNoDespawn();
		this.setSize(0.25F, 1.0F);
	}

	public EntityShockwaveSwordItem(World worldObj, double posX, double posY, double posZ, ItemStack itemStack) {
		super(worldObj, posX, posY, posZ, itemStack);
		this.setPickupDelay(80);
		this.setNoDespawn();
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.getDataManager().register(WAVE_PROGRESS, 0);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.lastWaveProgress = this.waveProgress;
		this.waveProgress = this.getDataManager().get(WAVE_PROGRESS);
		if(this.waveProgress < 50)
			this.getDataManager().set(WAVE_PROGRESS, this.waveProgress + 1);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("WaveProgress", this.getDataManager().get(WAVE_PROGRESS));
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.getDataManager().set(WAVE_PROGRESS, nbt.getInteger("WaveProgress"));
	}

	@SideOnly(Side.CLIENT)
	public float getWaveProgress(float partialTicks) {
		return this.lastWaveProgress + (this.waveProgress - this.lastWaveProgress) * partialTicks;
	}
}

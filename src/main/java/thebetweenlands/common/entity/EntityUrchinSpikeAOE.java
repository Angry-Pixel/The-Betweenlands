package thebetweenlands.common.entity;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.render.particle.entity.ParticleUrchinSpike;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityUrchinSpikeAOE extends Entity {
	private static final DataParameter<Integer> SPIKE_BOX_SIZE = EntityDataManager.createKey(EntityUrchinSpikeAOE.class, DataSerializers.VARINT);
	private boolean shootSpikes;
	public int MAX_SPIKE_TIMER = 10;
	private EntityPlayer owner;
	private int damage;
	public static final byte EVENT_ATTACK = 66;

	public EntityUrchinSpikeAOE(World world) {
		super(world);
		setSize(0.0F, 0.0F);
	}
	
	public EntityUrchinSpikeAOE(World world, EntityPlayer owner, int damage) {
		super(world);
		this.owner = owner;
		this.damage = damage;
	}

	@Override
	protected void entityInit() {
		dataManager.register(SPIKE_BOX_SIZE, 0);
	}

	public int getSpikeBoxTimer() {
		return dataManager.get(SPIKE_BOX_SIZE);
	}

	public void setSpikeBoxTimer(int count) {
		dataManager.set(SPIKE_BOX_SIZE, count);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!getEntityWorld().isRemote) {
			checkAOEDamage();
			if (getSpikeBoxTimer() < MAX_SPIKE_TIMER)
				setSpikeBoxTimer(getSpikeBoxTimer() + 1);
			if (getSpikeBoxTimer() >= MAX_SPIKE_TIMER)
				setDead();
		}
	}

	private void checkAOEDamage() {
		if (!getEntityWorld().isRemote && getEntityWorld().getDifficulty() != EnumDifficulty.PEACEFUL) {
			List<EntityLivingBase> list = getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, spikesBox(), e -> e instanceof IMob);
			for (Iterator<EntityLivingBase> iterator = list.iterator(); iterator.hasNext();) {
				EntityLivingBase entity  = iterator.next();
				if (entity != null && entity == owner || entity instanceof EntityPlayer && ((EntityPlayer) entity).isSpectator() || entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative())
					iterator.remove();
			}

			if (list.isEmpty())
				return;

			if (!list.isEmpty()) {
				EntityLivingBase entity = list.get(0);
				if(entity.hurtResistantTime <= 0) {
					entity.attackEntityFrom(new EntityDamageSource("cactus", owner !=null ? owner : this), 2F * damage);
				}
			}
		}
	}

	public void shootSpikes() {
		getEntityWorld().playSound(null, getPosition(), SoundRegistry.URCHIN_SHOOT, SoundCategory.NEUTRAL, 1F, 1.5F + (getEntityWorld().rand.nextFloat() - getEntityWorld().rand.nextFloat()) * 0.5F);
		getEntityWorld().setEntityState(this, EVENT_ATTACK);
	}

	public AxisAlignedBB spikesBox() {
		float x = (2F / MAX_SPIKE_TIMER) * getSpikeBoxTimer();
		float y = (2F / MAX_SPIKE_TIMER) * getSpikeBoxTimer();
		float z = (2F / MAX_SPIKE_TIMER) * getSpikeBoxTimer();
		return new AxisAlignedBB(getPosition()).grow(x, y, z);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);               
		if (id == EVENT_ATTACK) {
			Vec3d frontCenter = new Vec3d(posX, posY, posZ);
			for (int i = 0; i < 64; i++) {
				Random rnd = world.rand;
				float rx = rnd.nextFloat() * 4.0F - 2.0F;
				float ry = rnd.nextFloat() * 4.0F - 2.0F;
				float rz = rnd.nextFloat() * 4.0F - 2.0F;
				Vec3d vec = new Vec3d(rx, ry, rz);
				vec = vec.normalize();
				ParticleUrchinSpike particle = (ParticleUrchinSpike) BLParticles.URCHIN_SPIKE.spawn(world, frontCenter.x, frontCenter.y - 0.25D, frontCenter.z, ParticleArgs.get().withMotion(vec.x * 0.175F, vec.y * 0.15F + 0.35F, vec.z * 0.175F).withScale(0.2F));
				particle.setUseSound(this.rand.nextInt(15) == 0);
			}
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
	}

}

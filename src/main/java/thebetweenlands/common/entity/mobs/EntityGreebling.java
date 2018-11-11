package thebetweenlands.common.entity.mobs;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.audio.IEntitySound;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.api.entity.IEntityMusic;
import thebetweenlands.client.audio.EntityMusicLayers;
import thebetweenlands.client.audio.GreeblingMusicSound;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.sound.BLSoundEvent;

/**
 * Created by Josh on 8/19/2018.
 */
public class EntityGreebling extends EntityCreature implements IEntityBL, IEntityMusic {
	protected static final byte EVENT_START_DISAPPEARING = 40;
	protected static final byte EVENT_DISAPPEAR = 41;
	
	private static final DataParameter<Integer> TYPE = EntityDataManager.createKey(EntityGreebling.class, DataSerializers.VARINT);
	private static final DataParameter<EnumFacing> FACING = EntityDataManager.createKey(EntityGreebling.class, DataSerializers.FACING);

	public int disappearTimer = 0;

	public EntityGreebling(World worldIn) {
		super(worldIn);
		this.setSize(1, 0.75f);
	}

	private static final class GreeblingGroup implements IEntityLivingData {
		public boolean hasType1;
		public boolean hasType2;
		public int count;
	}

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		super.onInitialSpawn(difficulty, livingdata);

		if(livingdata == null) {
			livingdata = new GreeblingGroup();
		}

		if(livingdata instanceof GreeblingGroup) {
			GreeblingGroup group = (GreeblingGroup) livingdata;

			if(group.count > 0 && (!group.hasType1 || !group.hasType2)) {
				if(!group.hasType1) {
					this.setType(0);
				} else {
					this.setType(1);
				}
			} else {
				this.setType(rand.nextInt(2));
				if(this.getType() == 0) {
					group.hasType1 = true;
				} else {
					group.hasType2 = true;
				}
			}

			group.count++;
		} else {
			this.setType(rand.nextInt(2));
		}

		this.dataManager.set(FACING, EnumFacing.HORIZONTALS[rand.nextInt(EnumFacing.HORIZONTALS.length)]);

		return livingdata;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(TYPE, 0);
		this.dataManager.register(FACING, EnumFacing.NORTH);
	}

	public void setType(int type) {
		this.dataManager.set(TYPE, type);
	}

	public int getType() {
		return this.dataManager.get(TYPE);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		this.prevRotationPitch = this.rotationPitch = 0;
		this.prevRotationYaw = this.rotationYaw = this.dataManager.get(FACING).getHorizontalAngle();
		this.prevRenderYawOffset = this.renderYawOffset = this.dataManager.get(FACING).getHorizontalAngle();

		if (disappearTimer > 0 && disappearTimer < 8) disappearTimer++;
		
		if(!this.world.isRemote) {
			if (disappearTimer == 5) this.world.setEntityState(this, EVENT_DISAPPEAR);
			if (disappearTimer >= 8) setDead();
			
			List<EntityPlayer> nearPlayers = world.getEntitiesWithinAABB(EntityPlayer.class, getEntityBoundingBox().grow(4.5, 5, 4.5), e -> !e.capabilities.isCreativeMode && !e.isInvisible());
			if (disappearTimer == 0 && !nearPlayers.isEmpty()) {
				disappearTimer++;
				this.world.playSound(null, this.posX, this.posY, this.posZ, SoundRegistry.GREEBLING_VANISH, SoundCategory.NEUTRAL, 1, 1);
				this.world.setEntityState(this, EVENT_START_DISAPPEARING);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);
		
		if(id == EVENT_START_DISAPPEARING) {
			disappearTimer = 1;
		} else if(id == EVENT_DISAPPEAR) {
			doLeafEffects();
		}
	}
	
	private void doLeafEffects() {
		if(world.isRemote) {
			int leafCount = 40;
			float x = (float) (posX);
			float y = (float) (posY + 1.3F);
			float z = (float) (posZ);
			while (leafCount-- > 0) {
				float dx = world.rand.nextFloat() * 1 - 0.5f;
				float dy = world.rand.nextFloat() * 1f - 0.1F;
				float dz = world.rand.nextFloat() * 1 - 0.5f;
				float mag = 0.08F + world.rand.nextFloat() * 0.07F;
				BLParticles.WEEDWOOD_LEAF.spawn(world, x, y, z, ParticleFactory.ParticleArgs.get().withMotion(dx * mag, dy * mag, dz * mag));
			}
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("type", getType());
		compound.setInteger("facing", this.dataManager.get(FACING).getHorizontalIndex());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		setType(compound.getInteger("type"));
		this.dataManager.set(FACING, EnumFacing.getHorizontal(compound.getInteger("facing")));
	}

	@Override
	public void knockBack(Entity entityIn, float strength, double xRatio, double zRatio) { }

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	protected void collideWithNearbyEntities() { }

	@Override
	public void applyEntityCollision(Entity entityIn) { }

	@Override
	public BLSoundEvent getMusicFile(EntityPlayer listener) {
		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IEntitySound getMusicSound(EntityPlayer listener) {
		return new GreeblingMusicSound(this.getType(), this, 0.75f);
	}

	@Override
	public double getMusicRange(EntityPlayer listener) {
		return 40.0D;
	}

	@Override
	public boolean isMusicActive(EntityPlayer listener) {
		return this.isEntityAlive();
	}

	@Override
	public int getMusicLayer(EntityPlayer listener) {
		return this.getType() == 0 ? EntityMusicLayers.GREEBLING_1 : EntityMusicLayers.GREEBLING_2;
	}

	@Override
	public boolean canInterruptOtherEntityMusic(EntityPlayer listener) {
		return false;
	}

	public void setFacing(EnumFacing facing) {
		this.dataManager.set(FACING, facing);
	}
}

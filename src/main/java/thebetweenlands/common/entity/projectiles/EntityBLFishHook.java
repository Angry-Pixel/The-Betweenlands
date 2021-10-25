package thebetweenlands.common.entity.projectiles;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityAnadia;
import thebetweenlands.common.item.tools.ItemBLFishingRod;

public class EntityBLFishHook extends EntityFishHook implements IEntityAdditionalSpawnData {

	private static final DataParameter<Boolean> IS_BAITED = EntityDataManager.createKey(EntityBLFishHook.class, DataSerializers.BOOLEAN);
	public boolean inGround;
	private int ticksInGround;
	private EntityPlayer angler;
	private int ticksInAir;
	private EntityBLFishHook.State currentState = State.FLYING;

	protected double interpTargetX;
	protected double interpTargetY;
	protected double interpTargetZ;
	protected int newPosRotationIncrements;

	static enum State {
		FLYING, HOOKED_IN_ENTITY, BOBBING;
	}

	@SideOnly(Side.CLIENT)
	public EntityBLFishHook(World world, EntityPlayer player, double x, double y, double z) {
		super(world, player, x, y, z);
		init(player);
		setPosition(x, y, z);
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
	}

	public EntityBLFishHook(World world, EntityPlayer player) {
		super(world, player);
		init(player);
		shoot(player);
	}

	public EntityBLFishHook(World world) {
		super(world, new EntityPlayer(world, new GameProfile(UUID.randomUUID(), "[FishHookDummy]")) {
			@Override
			public boolean isSpectator() {
				return false;
			}

			@Override
			public boolean isCreative() {
				return false;
			}
		});
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		this.rotationYaw = 0;
		this.rotationPitch = 0;
		this.prevRotationYaw = this.rotationYaw;
		this.prevRotationPitch = this.rotationPitch;
	}

	private void init(EntityPlayer player) {
		setSize(0.25F, 0.25F);
		ignoreFrustumCheck = true;
		angler = player;
		angler.fishEntity = this;
	}

	@Override
	protected void entityInit() {
		dataManager.register(IS_BAITED, false);
	}

	@Override
	public EntityPlayer getAngler() {
		return angler;
	}

	@Override
	public void setLureSpeed(int speed) {}

	@Override
	public void setLuck(int luck) {}

	public void shoot(EntityPlayer angler) {
		float f = angler.prevRotationPitch + (angler.rotationPitch - angler.prevRotationPitch);
		float f1 = angler.prevRotationYaw + (angler.rotationYaw - angler.prevRotationYaw);
		float f2 = MathHelper.cos(-f1 * 0.017453292F - (float) Math.PI);
		float f3 = MathHelper.sin(-f1 * 0.017453292F - (float) Math.PI);
		float f4 = -MathHelper.cos(-f * 0.017453292F);
		float f5 = MathHelper.sin(-f * 0.017453292F);
		double d0 = angler.prevPosX + (angler.posX - angler.prevPosX) - (double) f3 * 0.3D;
		double d1 = angler.prevPosY + (angler.posY - angler.prevPosY) + (double) angler.getEyeHeight();
		double d2 = angler.prevPosZ + (angler.posZ - angler.prevPosZ) - (double) f2 * 0.3D;
		setLocationAndAngles(d0, d1, d2, f1, f);
		motionX = (double) (-f3);
		motionY = (double) MathHelper.clamp(-(f5 / f4), -5.0F, 5.0F);
		motionZ = (double) (-f2);
		float f6 = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
		motionX *= 0.6D / (double) f6 + 0.5D + rand.nextGaussian() * 0.0045D;
		motionY *= 0.6D / (double) f6 + 0.5D + rand.nextGaussian() * 0.0045D;
		motionZ *= 0.6D / (double) f6 + 0.5D + rand.nextGaussian() * 0.0045D;
		float f7 = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
		rotationYaw = (float) (MathHelper.atan2(motionX, motionZ) * (180D / Math.PI));
		rotationPitch = (float) (MathHelper.atan2(motionY, (double) f7) * (180D / Math.PI));
		prevRotationYaw = rotationYaw;
		prevRotationPitch = rotationPitch;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return distance < 4096.0D;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
		this.interpTargetX = x;
		this.interpTargetY = y;
		this.interpTargetZ = z;
		this.newPosRotationIncrements = posRotationIncrements;
	}

	@Override
	public void onUpdate() {
		if (!this.world.isRemote)
			this.setFlag(6, this.isGlowing());

		if(this.ticksExisted < 2) {
			//Stupid EntityTrackerEntry is broken and desyncs server position.
			//Tracker updates server side position but *does not* send the change to the client
			//when tracker.updateCounter == 0, causing a desync until the next force teleport
			//packet.......
			//By not moving the entity until then it works.
			return;
		}

		this.onEntityUpdate();

		if (currentState != EntityBLFishHook.State.FLYING /*for smooth throwing*/ && this.newPosRotationIncrements > 0 && !this.canPassengerSteer())
		{
			double d0 = this.posX + (this.interpTargetX - this.posX) / (double)this.newPosRotationIncrements;
			double d1 = this.posY + (this.interpTargetY - this.posY) / (double)this.newPosRotationIncrements;
			double d2 = this.posZ + (this.interpTargetZ - this.posZ) / (double)this.newPosRotationIncrements;
			--this.newPosRotationIncrements;
			this.setPosition(d0, d1, d2);
		}

		EntityPlayer angler = this.getAngler();

		if (angler == null || angler.fishEntity != this) {
			this.setDead();
		} else if (world.isRemote || !shouldStopFishing()) {
			if (inGround) {
				++ticksInGround;

				if (ticksInGround >= 1200) {
					setDead();
					return;
				}
			}

			float f = 0.0F;
			BlockPos blockpos = new BlockPos(this);
			IBlockState iblockstate = world.getBlockState(blockpos);

			if (iblockstate.getMaterial() == Material.WATER) {
				f = BlockLiquid.getBlockLiquidHeight(iblockstate, world, blockpos);
			}

			if (currentState == EntityBLFishHook.State.FLYING) {
				if (caughtEntity != null) {
					motionX = 0.0D;
					motionY = 0.0D;
					motionZ = 0.0D;
					currentState = EntityBLFishHook.State.HOOKED_IN_ENTITY;
					return;
				}

				if (f > 0.0F) {
					motionX *= 0.3D;
					motionY *= 0.2D;
					motionZ *= 0.3D;
					currentState = EntityBLFishHook.State.BOBBING;
					return;
				}

				if (!world.isRemote) {
					checkCollision();
				}

				if (!inGround && !onGround && !collidedHorizontally) {
					++ticksInAir;
				} else {
					ticksInAir = 0;
					motionX = 0.0D;
					motionY = 0.0D;
					motionZ = 0.0D;
				}
			} else {
				if (currentState == EntityBLFishHook.State.HOOKED_IN_ENTITY) {
					if (caughtEntity != null) {
						if (caughtEntity.isDead) {
							caughtEntity = null;
							currentState = EntityBLFishHook.State.FLYING;
						}
					}
					return;
				}

				if (!world.isRemote && currentState == EntityBLFishHook.State.BOBBING) {
					//Bobbing is random so only do it on server side to stay in sync

					motionX *= 0.9D;
					motionZ *= 0.9D;
					double d0 = posY + motionY - (double) blockpos.getY() - (double) f;

					if (Math.abs(d0) < 0.01D) {
						d0 += Math.signum(d0) * 0.1D;
					}

					motionY -= d0 * (double) rand.nextFloat() * 0.2D;

				}
			}

			if (iblockstate.getMaterial() != Material.WATER) {
				motionY -= 0.03D;
			}

			move(MoverType.SELF, motionX, motionY, motionZ);
			updateRotation();
			motionX *= 0.92D;
			motionY *= 0.92D;
			motionZ *= 0.92D;
			setPosition(posX, posY, posZ);
		}
	}

	private void checkCollision() {
		Vec3d vec3d = new Vec3d(posX, posY, posZ);
		Vec3d vec3d1 = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
		RayTraceResult raytraceresult = world.rayTraceBlocks(vec3d, vec3d1, false, true, false);
		vec3d = new Vec3d(posX, posY, posZ);
		vec3d1 = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);

		if (raytraceresult != null)
			vec3d1 = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);

		if (raytraceresult != null && raytraceresult.typeOfHit != RayTraceResult.Type.MISS)
			inGround = true;
	}

	private boolean shouldStopFishing() {
		EntityPlayer angler = this.getAngler();

		if(angler == null) {
			return true;
		}

		ItemStack stack = angler.getHeldItemMainhand();
		ItemStack stack1 = angler.getHeldItemOffhand();
		boolean mainHandHeld = stack.getItem() instanceof ItemBLFishingRod;
		boolean offHandHeld = stack1.getItem() instanceof ItemBLFishingRod;

		if (!angler.isDead && angler.isEntityAlive() && (mainHandHeld || offHandHeld) && (int) getDistance(angler) <= 32) {
			return false;
		} else {
			setDead();
			return true;
		}
	}

	private void updateRotation() {
		float f = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
		rotationYaw = (float) (MathHelper.atan2(motionX, motionZ) * (180D / Math.PI));

		for (rotationPitch = (float) (MathHelper.atan2(motionY, (double) f) * (180D / Math.PI)); rotationPitch - prevRotationPitch < -180.0F; prevRotationPitch -= 360.0F) {
			;
		}

		while (rotationPitch - prevRotationPitch >= 180.0F) {
			prevRotationPitch += 360.0F;
		}

		while (rotationYaw - prevRotationYaw < -180.0F) {
			prevRotationYaw -= 360.0F;
		}

		while (rotationYaw - prevRotationYaw >= 180.0F) {
			prevRotationYaw += 360.0F;
		}

		rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
		rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;
	}

	public void setBaited(boolean hasBait) {
		dataManager.set(IS_BAITED, hasBait);
	}

	public boolean getBaited() {
		return dataManager.get(IS_BAITED);
	}

	@Override
	public int handleHookRetraction() {
		if (!world.isRemote && getAngler() != null) {
			int i = 1;

			if (caughtEntity == null) {
				bringInHookedEntity();
				world.setEntityState(this, (byte) 31);
			}

			if (caughtEntity != null) {
				bringInHookedEntity();
				world.setEntityState(this, (byte) 31);

				if(caughtEntity instanceof EntityAnadia) {
					i = (int) Math.floor(((EntityAnadia) caughtEntity).getStrengthMods() + 0.5D);
				}
			}

			if (inGround) {
				i = 2;
			}

			return i;
		} else {
			return 0;
		}
	}

	@Override
	protected void bringInHookedEntity() {
		EntityPlayer angler = this.getAngler();

		if (angler != null) {
			double d0 = angler.posX - posX;
			double d1 = angler.posY - posY;
			double d2 = angler.posZ - posZ;

			if (caughtEntity instanceof EntityAnadia) {
				EntityAnadia anadia = (EntityAnadia) caughtEntity;

				if(anadia.getStaminaTicks() > 0) {
					if(!anadia.isObstructed()) {
						anadia.setStaminaTicks(anadia.getStaminaTicks() - 2);
					}

					if(anadia.isObstructed()) {
						anadia.setEscapeTicks(anadia.getEscapeTicks() -16);
					}

					if(anadia.isTreasureFish() && anadia.isObstructedTreasure()) {
						if(!anadia.getTreasureUnlocked()) {
							anadia.playTreasureCollectedSound(angler);
						}
						anadia.setAsLootFish(true); //does the loot table changes
					}

					if (anadia.getStaminaTicks() % 40 == 0) {
						// consumes half a shank of hunger every 2 seconds or so whilst the fish has stamina
						angler.getFoodStats().setFoodLevel(angler.getFoodStats().getFoodLevel() - 1);
					}
				}

				caughtEntity.motionX += d0 * (0.045D - anadia.getStrengthMods() * 0.005D);
				caughtEntity.motionY += d1 * (0.045D - anadia.getStrengthMods() * 0.005D);
				caughtEntity.motionZ += d2 * (0.045D - anadia.getStrengthMods() * 0.005D);
			} else if(caughtEntity != null) {
				caughtEntity.motionX += d0 * 0.02D;
				caughtEntity.motionY += d1 * 0.02D;
				caughtEntity.motionZ += d2 * 0.02D;
			} else {
				motionX += d0 * 0.06D;
				motionY += d1 * 0.06D;
				motionZ += d2 * 0.06D;
			}
		}
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	public void setDead() {
		super.setDead();

		EntityPlayer angler = this.getAngler();
		if (angler != null) {
			angler.fishEntity = null;
		}
	}

	@Override
	public boolean writeToNBTOptional(NBTTagCompound compound) {
		return false;
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		EntityPlayer angler = this.getAngler();
		buffer.writeBoolean(angler != null);
		if(angler != null) {
			buffer.writeInt(angler.getEntityId());
		}
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		if(buffer.readBoolean()) {
			int entityId = buffer.readInt();
			Entity angler = world.getEntityByID(entityId);
			if(angler instanceof EntityPlayer) {
				((EntityPlayer) angler).fishEntity = this;
				this.angler = (EntityPlayer) angler;
			} else {
				this.angler = null;
			}
		}
	}
}
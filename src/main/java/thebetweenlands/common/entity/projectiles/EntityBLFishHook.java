package thebetweenlands.common.entity.projectiles;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.mobs.EntityAnadia;
import thebetweenlands.common.item.tools.ItemBLFishingRod;

public class EntityBLFishHook extends EntityFishHook implements IEntityAdditionalSpawnData {

	private static final DataParameter<Boolean> IS_BAITED = EntityDataManager.createKey(EntityBLFishHook.class, DataSerializers.BOOLEAN);
	public boolean inGround;
	private int ticksInGround;
	private EntityPlayer angler;
	private int ticksInAir;
	private EntityBLFishHook.State currentState = State.FLYING;

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
		shoot();
	}

	public EntityBLFishHook(World world) {
		super(world, getPlayer(world));
		setSize(0.25F, 0.25F);
		ignoreFrustumCheck = true;
	}

	private static EntityPlayer getPlayer(World world) {
		if (world.isRemote)
			return TheBetweenlands.proxy.getClientPlayer();

		if (FMLCommonHandler.instance().getMinecraftServerInstance() != null) {
			PlayerList players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
			if (!players.getPlayers().isEmpty()) {
				return (EntityPlayer) players.getPlayers().get(0);
			}
		}

		throw new IllegalStateException("Can't create Betweenlands Fish Hook Entity without a player.");
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

	public void shoot() {
		float f = getAngler().prevRotationPitch + (getAngler().rotationPitch - getAngler().prevRotationPitch);
		float f1 = getAngler().prevRotationYaw + (getAngler().rotationYaw - getAngler().prevRotationYaw);
		float f2 = MathHelper.cos(-f1 * 0.017453292F - (float) Math.PI);
		float f3 = MathHelper.sin(-f1 * 0.017453292F - (float) Math.PI);
		float f4 = -MathHelper.cos(-f * 0.017453292F);
		float f5 = MathHelper.sin(-f * 0.017453292F);
		double d0 = getAngler().prevPosX + (getAngler().posX - getAngler().prevPosX) - (double) f3 * 0.3D;
		double d1 = getAngler().prevPosY + (getAngler().posY - getAngler().prevPosY) + (double) getAngler().getEyeHeight();
		double d2 = getAngler().prevPosZ + (getAngler().posZ - getAngler().prevPosZ) - (double) f2 * 0.3D;
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
		double d0 = 64.0D;
		return distance < 4096.0D;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {}

	@Override
	public void onUpdate() {
        if (!this.world.isRemote)
            this.setFlag(6, this.isGlowing());

        this.onEntityUpdate();

		if (getAngler() == null) {
			setDead();
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

				if (currentState == EntityBLFishHook.State.BOBBING) {
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
			double d1 = 0.92D;
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
		ItemStack stack = getAngler().getHeldItemMainhand();
		ItemStack stack1 = getAngler().getHeldItemOffhand();
		boolean mainHandHeld = stack.getItem() instanceof ItemBLFishingRod;
		boolean offHandHeld = stack1.getItem() instanceof ItemBLFishingRod;

		if (!getAngler().isDead && getAngler().isEntityAlive() && (mainHandHeld || offHandHeld) && (int) getDistance(getAngler()) <= 32) {
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
			int i = 0;
			
			if (caughtEntity == null) {
				bringInHookedEntity();
				world.setEntityState(this, (byte) 31);
			}

			if (caughtEntity != null) {
				bringInHookedEntity();
				world.setEntityState(this, (byte) 31);
				i = (int) ((EntityAnadia) caughtEntity).getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
			}

			if (inGround)
				i = 2;

			return i;
		} else {
			return 0;
		}
	}

	@Override
	protected void bringInHookedEntity() {
		if (getAngler() != null) {
			double d0 = getAngler().posX - posX;
			double d1 = getAngler().posY - posY;
			double d2 = getAngler().posZ - posZ;
			if (caughtEntity != null) {
					if(((EntityAnadia) caughtEntity).getStaminaTicks() > 0) { 
						((EntityAnadia) caughtEntity).setStaminaTicks(((EntityAnadia) caughtEntity).getStaminaTicks() - 1);
						if (((EntityAnadia) caughtEntity).getStaminaTicks()%40 == 0) {
							// consumes half a shank of hunger every 2 seconds or so whilst the fish has stamina
							getAngler().getFoodStats().setFoodLevel(getAngler().getFoodStats().getFoodLevel() - 1);
						}
					}
				caughtEntity.motionX += d0 * (0.045D - ((EntityAnadia) caughtEntity).getStrengthMods() * 0.005D);
				caughtEntity.motionY += d1 * (0.045D - ((EntityAnadia) caughtEntity).getStrengthMods() * 0.005D);
				caughtEntity.motionZ += d2 * (0.045D - ((EntityAnadia) caughtEntity).getStrengthMods() * 0.005D);
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
		if (getAngler() != null)
			getAngler().fishEntity = null;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		if (getAngler() != null)
			buffer.writeInt(getAngler().getEntityId());
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		if(buffer.isReadable()) {
			int entityPlayerID = buffer.readInt();
			EntityPlayer playerIn = (EntityPlayer) world.getEntityByID(entityPlayerID);
			EntityBLFishHook entityFishHookIn = (EntityBLFishHook) ((EntityPlayer) world.getEntityByID(entityPlayerID)).fishEntity;
			ObfuscationReflectionHelper.setPrivateValue(EntityBLFishHook.class, entityFishHookIn, playerIn, new String[] { "angler", "field_146042_b" });
		}
	}

}
package thebetweenlands.common.entity.projectiles;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.common.item.tools.bow.EnumArrowType;
import thebetweenlands.common.registries.ItemRegistry;

public class EntityBLArrow extends EntityArrow {
	private static final DataParameter<String> DW_TYPE = EntityDataManager.<String>createKey(EntityArrow.class, DataSerializers.STRING);
	private static final DataParameter<String> DW_UUID = EntityDataManager.<String>createKey(EntityArrow.class, DataSerializers.STRING);
	private boolean checkedShooter = false;
	private boolean inGround = false;
	private int inGroundTicks = 0;

	public EntityBLArrow(World worldIn) {
		super(worldIn);
	}

	public EntityBLArrow(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}

	public EntityBLArrow(World worldIn, EntityLivingBase shooter) {
		super(worldIn, shooter);
		this.dataManager.set(DW_UUID, shooter.getUniqueID().toString());
	}

	@Override
	public void entityInit() {
		super.entityInit();
		this.dataManager.register(DW_TYPE, "");
		this.dataManager.register(DW_UUID, "");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("inGroundTicks", this.inGroundTicks);
		nbt.setString("arrowType", this.getArrowType().getName());
		nbt.setString("shooter", this.getDataManager().get(DW_UUID));
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.inGroundTicks = nbt.getInteger("inGroundTicks");
		this.setType(EnumArrowType.getEnumFromString(nbt.getString("arrowType")));
		this.dataManager.set(DW_UUID, "shooter");
	}

	@Override
	public void onEntityUpdate() {
		if(this.shootingEntity == null && !this.checkedShooter) {
			try {
				UUID uuid = UUID.fromString(this.dataManager.get(DW_UUID));
				if(uuid != null) {
					this.shootingEntity = this.world.getPlayerEntityByUUID(uuid);
				}
			} catch(Exception ignored) { } finally {
				this.checkedShooter = true;
			}
		}

		if(this.inGround) {
			this.inGroundTicks++;
		}

		if(!this.world.isRemote && !this.inGround) {
			RayTraceResult collision = getCollision(this);
			if(collision != null && collision.typeOfHit == RayTraceResult.Type.ENTITY && collision.entityHit instanceof EntityLivingBase) {
				EntityLivingBase hitEntity = (EntityLivingBase) collision.entityHit;

				switch(this.getArrowType()) {
				case ANGLER_POISON:
					hitEntity.addPotionEffect(new PotionEffect(MobEffects.POISON, 200, 2));
					break;
				case OCTINE:
					if(hitEntity.isBurning()) {
						hitEntity.setFire(9);
					} else {
						hitEntity.setFire(5);
					}
					break;
				case BASILISK:
					//TODO Add when basilisk effect is implemented
					//hitEntity.addPotionEffect(ElixirEffectRegistry.EFFECT_PETRIFY.createEffect(100, 1));
					break;
				default:
				}
			} else if(collision != null && collision.typeOfHit == RayTraceResult.Type.BLOCK) {
				this.inGround = true;
			}
		}
		super.onEntityUpdate();
	}

	private static RayTraceResult getCollision(EntityArrow ea) {
		Vec3d start = new Vec3d(ea.posX, ea.posY, ea.posZ);
		Vec3d dest = new Vec3d(ea.posX + ea.motionX, ea.posY + ea.motionY, ea.posZ + ea.motionZ);
		RayTraceResult hit = ea.world.rayTraceBlocks(start, dest, false, true, false);
		start = new Vec3d(ea.posX, ea.posY, ea.posZ);
		dest = new Vec3d(ea.posX + ea.motionX, ea.posY + ea.motionY, ea.posZ + ea.motionZ);
		if (hit != null) {
			dest = new Vec3d(hit.hitVec.x, hit.hitVec.y, hit.hitVec.z);
		}

		Entity collidedEntity = null;
		List<Entity> entityList = ea.world.getEntitiesWithinAABBExcludingEntity(ea, ea.getEntityBoundingBox().expand(ea.motionX, ea.motionY, ea.motionZ).expand(1.05D, 1.05D, 1.05D));
		double lastDistance = 0.0D;
		for (Object anEntityList : entityList) {
			Entity currentEntity = (Entity) anEntityList;
			if (currentEntity.canBeCollidedWith() && (currentEntity != ea.shootingEntity || ea.ticksExisted > 5)) {
				AxisAlignedBB entityBoundingBox = currentEntity.getEntityBoundingBox().expand((double) 0.35F, (double) 0.35F, (double) 0.35F);
				RayTraceResult collision = entityBoundingBox.calculateIntercept(start, dest);
				if (collision != null) {
					double currentDistance = start.distanceTo(collision.hitVec);

					if (currentDistance < lastDistance || lastDistance == 0.0D) {
						collidedEntity = currentEntity;
						lastDistance = currentDistance;
					}
				}
			}
		}

		if (collidedEntity != null) {
			hit = new RayTraceResult(collidedEntity);
		}

		return hit;
	}

	/**
	 * Sets the arrow type
	 * @param type
	 */
	public void setType(EnumArrowType type) {
		this.dataManager.set(DW_TYPE, type.getName());
	}

	/**
	 * Returns the arrow type
	 * @return
	 */
	public EnumArrowType getArrowType(){
		return EnumArrowType.getEnumFromString(this.dataManager.get(DW_TYPE));
	}

	@Override
	protected ItemStack getArrowStack() {
		switch(this.getArrowType()) {
		case ANGLER_POISON:
			return new ItemStack(ItemRegistry.POISONED_ANGLER_TOOTH_ARROW);
		case OCTINE:
			return new ItemStack(ItemRegistry.OCTINE_ARROW);
		case BASILISK:
			return new ItemStack(ItemRegistry.BASILISK_ARROW);
		case DEFAULT:
		default:
			return new ItemStack(ItemRegistry.ANGLER_TOOTH_ARROW);
		}
	}
}

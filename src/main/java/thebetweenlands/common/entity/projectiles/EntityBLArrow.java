package thebetweenlands.common.entity.projectiles;

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
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import thebetweenlands.common.entity.mobs.EntityTinySludgeWormHelper;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.item.tools.bow.EnumArrowType;
import thebetweenlands.common.registries.ItemRegistry;

public class EntityBLArrow extends EntityArrow implements IThrowableEntity /*for shooter sync*/ {
	private static final DataParameter<String> DW_TYPE = EntityDataManager.<String>createKey(EntityBLArrow.class, DataSerializers.STRING);

	public EntityBLArrow(World worldIn) {
		super(worldIn);
	}

	public EntityBLArrow(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}

	public EntityBLArrow(World worldIn, EntityLivingBase shooter) {
		super(worldIn, shooter);
	}

	@Override
	public Entity getThrower() {
		return this.shootingEntity;
	}
	
	@Override
	public void setThrower(Entity entity) {
		this.shootingEntity = entity;
	}
	
	@Override
	public void entityInit() {
		super.entityInit();
		this.dataManager.register(DW_TYPE, "");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setString("arrowType", this.getArrowType().getName());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.setType(EnumArrowType.getEnumFromString(nbt.getString("arrowType")));
	}

	@Override
	protected void arrowHit(EntityLivingBase living) {
		switch(this.getArrowType()) {
		case ANGLER_POISON:
			living.addPotionEffect(new PotionEffect(MobEffects.POISON, 200, 2));
			break;
		case OCTINE:
			if(living.isBurning()) {
				living.setFire(9);
			} else {
				living.setFire(5);
			}
			break;
		case BASILISK:
			if(!living.isNonBoss()) {
				
			}
			living.addPotionEffect(ElixirEffectRegistry.EFFECT_PETRIFY.createEffect(100, 1));
			break;
		case WORM:
			if (!getEntityWorld().isRemote) {
				EntityTinySludgeWormHelper worm = new EntityTinySludgeWormHelper(getEntityWorld());
				worm.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
				worm.setAttackTarget(living);
				getEntityWorld().spawnEntity(worm);
				this.setDead();
			}
			break;
		default:
		}
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
		case WORM:
			return new ItemStack(ItemRegistry.SLUDGE_WORM_ARROW);
		case DEFAULT:
		default:
			return new ItemStack(ItemRegistry.ANGLER_TOOTH_ARROW);
		}
	}
}

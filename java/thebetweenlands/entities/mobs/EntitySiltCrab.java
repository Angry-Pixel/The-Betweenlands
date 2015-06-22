package thebetweenlands.entities.mobs;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.items.AxeBL;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.PickaxeBL;
import thebetweenlands.items.SpadeBL;
import thebetweenlands.items.SwordBL;

public class EntitySiltCrab extends EntityMob implements IEntityBL {
	
	private EntityAIAttackOnCollide meleeAttack = new EntityAIAttackOnCollide(this, EntityPlayer.class, 0.7D, false);
	private EntityAIAvoidEntity runAway = new EntityAIAvoidEntity(this, EntityPlayer.class, 10.0F, 0.7D, 0.7D);
	private EntityAINearestAttackableTarget target =  new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true);
	private int aggroCooldown = 200;
	private boolean canAttack = false;

	public EntitySiltCrab(World world) {
		super(world);
		setSize(0.8F, 0.6F);
		tasks.addTask(0, meleeAttack);
		tasks.addTask(1, runAway);
		tasks.addTask(2, new EntityAIWander(this, 0.7D));
		tasks.addTask(3, new EntityAILookIdle(this));
		targetTasks.addTask(0, new EntityAIHurtByTarget(this, true));
		targetTasks.addTask(1, target);
		stepHeight = 2;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3.0D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	public boolean getCanSpawnHere() {
		return worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(boundingBox.minY) - 1, MathHelper.floor_double(posZ)) == BLBlockRegistry.silt && super.getCanSpawnHere();
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 5;
	}

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		entityDropItem(ItemMaterialsBL.createStack(BLItemRegistry.siltCrabClaw, 2, 0), 0.0F);
	}

	@Override
	protected void func_145780_a(int x, int y, int z, Block block) {
		playSound("mob.spider.step", 0.15F, 2.0F);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!worldObj.isRemote) {
			if (aggroCooldown == 200 && !canAttack) {
				tasks.removeTask(runAway);
				tasks.addTask(0, meleeAttack);
				targetTasks.addTask(1, target);
				canAttack = true;
			}

			if (aggroCooldown == 0 && canAttack) {
				tasks.removeTask(meleeAttack);
				targetTasks.removeTask(target);
				tasks.addTask(1, runAway);
				canAttack = false;
			}

			if (aggroCooldown < 201)
				aggroCooldown++;
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source.equals(DamageSource.drown))
			return false;
		if (source.getSourceOfDamage() instanceof EntityPlayer) {
			EntityPlayer entityPlayer = (EntityPlayer) source.getSourceOfDamage();
			ItemStack heldItem = entityPlayer.getCurrentEquippedItem();
			if (heldItem != null)
				if (heldItem.getItem() instanceof SwordBL || heldItem.getItem() instanceof AxeBL || heldItem.getItem() instanceof PickaxeBL || heldItem.getItem() instanceof SpadeBL) {
					return super.attackEntityFrom(source, damage);
				} else {
					return super.attackEntityFrom(source, MathHelper.ceiling_float_int((float) damage * 0.5F));
				}
		}
		return super.attackEntityFrom(source, damage);
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer player) {
		super.onCollideWithPlayer(player);
		if (!worldObj.isRemote && getDistanceToEntity(player) <= 1.5F && canAttack) {
			worldObj.playSoundEffect(posX, posY, posZ, "thebetweenlands:crabSnip", 1F, 1F);
			aggroCooldown = 0;
		}
	}
}

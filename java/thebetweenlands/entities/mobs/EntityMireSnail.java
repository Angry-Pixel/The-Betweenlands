package thebetweenlands.entities.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityMireSnail extends EntityTameable {

	public EntityMireSnail(World world) {
		super(world);
		setSize(1.0F, 0.8F);
		stepHeight = 0.0F;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2D); // Movespeed
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(5.0D); // MaxHealth
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D); // followRange
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 3;
	}
/*
	@Override
	protected String getLivingSound() {
		return "thebetweenlands:snailliving";
	}

	@Override
	protected String getHurtSound() {
		return "thebetweenlands:snailhurt";
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:snaildeath";
	}

	@Override
	protected Item getDropItem() {
		return ModItems.lifeBlood;
	}
*/

	@Override
	public EntityAgeable createChild(EntityAgeable entity) {
		// TODO Auto-generated method stub
		return null;
	}
}

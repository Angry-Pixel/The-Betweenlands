package thebetweenlands.entities.mobs;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.world.World;

public class EntityMireSnailEgg extends EntityAnimal {

	public EntityMireSnailEgg(World world) {
		super(world);
		setSize(0.35F, 0.35F);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		if(getGrowingAge() < 0 || getGrowingAge() > 0)
			setGrowingAge(0);
	}

	@Override
	public EntityAgeable createChild(EntityAgeable p_90011_1_) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0D); // Movespeed
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(5.0D); // MaxHealth
	}


}

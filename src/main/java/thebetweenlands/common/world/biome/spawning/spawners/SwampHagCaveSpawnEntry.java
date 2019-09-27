package thebetweenlands.common.world.biome.spawning.spawners;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.world.World;
import thebetweenlands.common.entity.mobs.EntitySwampHag;

public class SwampHagCaveSpawnEntry extends CaveSpawnEntry {
	public SwampHagCaveSpawnEntry(int id, short baseWeight) {
		super(id, EntitySwampHag.class, EntitySwampHag::new, baseWeight);
	}

	@Override
	public EntityLiving createEntity(World world) {
		EntityLiving entity = super.createEntity(world);
		float multiplier = (float)this.getWeight() / (float)this.getBaseWeight();
		IAttributeInstance movementAttr = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
		movementAttr.setBaseValue(movementAttr.getBaseValue() + 0.075D * multiplier);
		IAttributeInstance attackAttr = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		attackAttr.setBaseValue(attackAttr.getBaseValue() + 5.0D * multiplier);
		return entity;
	}
}

package thebetweenlands.common.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;

//TODO when porting this entity please save all modifiers to NBT and recalculate them when size is changed.
//all 4 (speed, health, strength, and stamina) are calculated on the fly, which is painful for both the entity and the mob item tooltip
public class Anadia extends WaterAnimal implements BLEntity {
	public static final String HEAD_KEY = "head_item";
	public static final String BODY_KEY = "body_item";
	public static final String TAIL_KEY = "tail_item";

	public Anadia(EntityType<? extends WaterAnimal> type, Level level) {
		super(type, level);
	}
}

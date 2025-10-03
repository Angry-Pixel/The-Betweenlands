package thebetweenlands.common.entity.monster;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.phys.Vec3;

// Literally only exists so we don't have to use Entity to accommodate both the parent and the children in arrays
public interface SludgeWormPartEntity extends EntityAccess {
	public Entity entity();
    
    default Vec3 position() {
    	return this.entity().position();
    }
}

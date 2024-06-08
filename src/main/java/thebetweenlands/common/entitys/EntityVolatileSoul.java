package thebetweenlands.common.entitys;

import java.util.Deque;
import java.util.LinkedList;

import com.mojang.math.Vector3d;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class EntityVolatileSoul extends BetweenlandsEntity {
	private Entity target = null;
	private int strikes = 0;
	private int ticksInAir;
	
	//protected static final DataParameter<Optional<UUID>> OWNER_UUID_DW = EntityDataManager.createKey(EntityVolatileSoul.class, DataSerializers.OPTIONAL_UNIQUE_ID);

	protected Deque<Vector3d> trail = new LinkedList<>();
	
	protected EntityVolatileSoul(EntityType<? extends Monster> p_33002_, Level p_33003_) {
		super(p_33002_, p_33003_);
	}
	

}

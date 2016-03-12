package thebetweenlands.entities.mobs.boss;

import net.minecraft.util.IChatComponent;

public interface IBossBL {
	float getMaxBossHealth();
	float getBossHealth();
	IChatComponent getBossName();
}

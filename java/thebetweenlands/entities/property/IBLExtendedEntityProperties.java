package thebetweenlands.entities.property;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.IExtendedEntityProperties;

public interface IBLExtendedEntityProperties extends IExtendedEntityProperties {
	public String getID();
	public Class<? extends Entity> getEntityClass();
}

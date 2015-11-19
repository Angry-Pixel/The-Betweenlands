package thebetweenlands.entities.property;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;

public class BLEntityPropertiesRegistry {
	public static final EntityPropertiesDecay DECAY = new EntityPropertiesDecay();
	
	
	
	
	public static final BLEntityPropertiesRegistry INSTANCE = new BLEntityPropertiesRegistry();

	private Map<Class<? extends Entity>, Class<? extends IBLExtendedEntityProperties>> registeredProperties = new HashMap<Class<? extends Entity>, Class<? extends IBLExtendedEntityProperties>>();

	private BLEntityPropertiesRegistry() {
		try {
			for(Field f : this.getClass().getFields()) {
				if(IBLExtendedEntityProperties.class.isAssignableFrom(f.getType())) {
					IBLExtendedEntityProperties prop = ((IBLExtendedEntityProperties)f.get(Modifier.isStatic(f.getModifiers()) ? null : this));
					this.registerProperty(prop.getEntityClass(), (Class<? extends IBLExtendedEntityProperties>)f.getType());
				}
			}
		} catch(Exception ex) { 
			ex.printStackTrace();
		}
	}

	public void registerProperty(Class<? extends Entity> entityClass, Class<? extends IBLExtendedEntityProperties> propClass) {
		this.registeredProperties.put(entityClass, propClass);
	}

	@SubscribeEvent
	public void onEntityConstructing(EntityEvent.EntityConstructing event) {
		for(Entry<Class<? extends Entity>, Class<? extends IBLExtendedEntityProperties>> propEntry : this.registeredProperties.entrySet()) {
			if(propEntry.getKey().isAssignableFrom(event.entity.getClass())) {
				Class<? extends IBLExtendedEntityProperties> propClass = propEntry.getValue();
				try {
					Constructor<? extends IBLExtendedEntityProperties> propCtor = propClass.getConstructor();
					IBLExtendedEntityProperties prop = propCtor.newInstance();
					event.entity.registerExtendedProperties(prop.getID(), prop);
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public <T extends IBLExtendedEntityProperties> T  getProperties(Entity entity, IBLExtendedEntityProperties props) {
		return (T) entity.getExtendedProperties(props.getID());
	}

	public <T extends IBLExtendedEntityProperties> T  getProperties(Entity entity, IBLExtendedEntityProperties props, Class<T> propClass) {
		return (T) entity.getExtendedProperties(props.getID());
	}
}

package thebetweenlands.manual;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import thebetweenlands.manual.achievement.AchievementList;
import thebetweenlands.manual.achievement.BLAchievement;

import java.lang.reflect.Field;

/**
 * Created by Bart on 10-8-2015.
 */
public class EntityPropertiesManual implements IExtendedEntityProperties {

    public int completedAchievements = 0;

    public static String getId() {
        return "betweenlands_achievement_data";
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        try {
            for (Field f : AchievementList.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof BLAchievement)
                    nbt.setBoolean(((BLAchievement) obj).name, ((BLAchievement) obj).isCompleted);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        nbt.setInteger("completedAchievements", completedAchievements);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        try {
            for (Field f : AchievementList.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof BLAchievement)
                    ((BLAchievement) obj).isCompleted = nbt.getBoolean(((BLAchievement) obj).name);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        completedAchievements = nbt.getInteger("completedAchievements");
    }

    @Override
    public void init(Entity entity, World world) {

    }
}

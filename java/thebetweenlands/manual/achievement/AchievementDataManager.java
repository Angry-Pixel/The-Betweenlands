package thebetweenlands.manual.achievement;


/**
 * Created by Bart on 10-8-2015.
 */
public class AchievementDataManager {
    public static String completeAchievement(BLAchievement achievement) {
        if (!achievement.isCompleted && achievement.canComplete()) {
            achievement.setCompleted();
            return "COMPLETED";
        } else if (!achievement.canComplete())
            return "MISSING ACHIEVEMENT";
        else
            return "ALREADY COMPLETED";
    }
}

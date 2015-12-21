package thebetweenlands.manual;

import net.minecraft.util.StatCollector;

import java.util.ArrayList;

/**
 * Created by Bart on 20-8-2015.
 */

//TODO implement this in all Entities
public class IManualEntryEntity {
    private String recourseLocation;
    private int picWidth;
    private int picHeight;
    private ArrayList<String> stats;
    private String name;

    public IManualEntryEntity(String name, int picWidth, int picHeight, float health, float attackDamage) {
        this.name = name;
        this.picHeight = picHeight;
        this.picWidth = picWidth;
        this.recourseLocation = "thebetweenlands:textures/gui/manual/" + name + ".png";

        ArrayList<String> stats = new ArrayList<>();
        stats.add(StatCollector.translateToLocal("manual.entity.health") + health);
        stats.add(StatCollector.translateToLocal("manual.entity.attack") + attackDamage);
        this.stats = stats;
    }

    public IManualEntryEntity(String name, int picHeight, int picWidth, ArrayList<String> stats) {
        this.name = name;
        this.picHeight = picHeight;
        this.picWidth = picWidth;
        this.recourseLocation = "thebetweenlands:textures/gui/manual/" + name + ".png";
        this.stats = stats;
    }

    public String manualPictureLocation() {
        return recourseLocation;
    }

    public int pictureWidth() {
        return picWidth;
    }

    public int pictureHeight() {
        return picHeight;
    }

    public ArrayList<String> manualStats() {
        return stats;
    }

    public String pageName() {
        return name;
    }
}

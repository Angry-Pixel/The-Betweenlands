package thebetweenlands.manual.gui.entries;

import thebetweenlands.manual.gui.GuiManualBase;
import thebetweenlands.manual.gui.pages.ManualPage;
import thebetweenlands.manual.gui.widgets.PictureWidget;
import thebetweenlands.manual.gui.widgets.text.TextWidget;

import java.util.ArrayList;

/**
 * Created by Bart on 30-10-2015.
 */
public class ManualEntryEntity extends ManualEntry {

    public ManualEntryEntity(IManualEntryEntity entity, GuiManualBase manual){
        super("manual." + entity.manualName() + ".title", new ManualPage(new TextWidget(manual, 15, 10, "manual." + entity.manualName() + ".title"), new PictureWidget(manual, (GuiManualBase.WIDTH / 2) - (entity.pictureWidth()/2), 15, entity.manualPictureLocation(), entity.pictureWidth(), entity.pictureHeight(), entity.manualStats())), new ManualPage(new TextWidget(manual, 15, 10, "manual." + entity.manualName() + ".description")));
    }
}

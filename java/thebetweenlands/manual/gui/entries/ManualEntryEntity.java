package thebetweenlands.manual.gui.entries;

import thebetweenlands.manual.gui.GuiManualBase;
import thebetweenlands.manual.gui.pages.ManualPage;
import thebetweenlands.manual.gui.widgets.PictureWidget;
import thebetweenlands.manual.gui.widgets.TextWidget;

import java.util.ArrayList;

/**
 * Created by Bart on 30-10-2015.
 */
public class ManualEntryEntity extends ManualEntry {

    public ManualEntryEntity(IManualEntryEntity entity, GuiManualBase manual){
        super(new ManualPage(new TextWidget(manual, 5, 5, "manual." + entity.manualName() + ".title"), new PictureWidget(manual, 5, 10, entity.manualPictureLocation(), entity.pictureWidth(), entity.pictureHeight(), entity.manualStats())), new ManualPage(new TextWidget(manual, 5, 5, "manual." + entity.manualName() + ".description")));
    }
}

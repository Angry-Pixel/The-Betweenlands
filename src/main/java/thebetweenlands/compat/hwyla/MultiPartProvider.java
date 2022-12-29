package thebetweenlands.compat.hwyla;

import static mcp.mobius.waila.api.SpecialChars.getRenderString;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.base.Strings;

import mcp.mobius.waila.addons.core.HUDHandlerEntities;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.config.FormattingConfig;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.util.text.translation.I18n;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.lib.ModInfo;

public class MultiPartProvider implements IWailaEntityProvider {

    @Nonnull
    @Override
    public List<String> getWailaHead(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        if (entity instanceof MultiPartEntityPart) {
            IEntityMultiPart parent = ((MultiPartEntityPart) entity).parent;
            if (parent instanceof IEntityBL) {
                String unknown = "\u00a7r" + String.format(FormattingConfig.entityFormat, "unknown");
                if (currenttip.removeIf(unknown::equals)) {
                    currenttip.add("\u00a7r" + String.format(FormattingConfig.entityFormat, ((Entity) parent).getName()));
                }
            }
        }
        return currenttip;
    }

    @Nonnull
    @Override
    public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        if (entity instanceof MultiPartEntityPart) {
            IEntityMultiPart parent = ((MultiPartEntityPart) entity).parent;
            if (parent instanceof IEntityBL) {
                if (config.getConfig("general.showhp") && parent instanceof EntityLivingBase) {
                    HUDHandlerEntities.nhearts = HUDHandlerEntities.nhearts <= 0 ? 20 : HUDHandlerEntities.nhearts;
                    float health = ((EntityLivingBase) parent).getHealth() / 2.0f;
                    float maxhp = ((EntityLivingBase) parent).getMaxHealth() / 2.0f;

                    if (((EntityLivingBase) parent).getMaxHealth() > HUDHandlerEntities.maxhpfortext)
                        currenttip.add(String.format(I18n.translateToLocal("hud.msg.health") + ": %.0f / %.0f", ((EntityLivingBase) parent).getHealth(), ((EntityLivingBase) parent).getMaxHealth()));
                    else
                        currenttip.add(getRenderString("waila.health", String.valueOf(HUDHandlerEntities.nhearts), String.valueOf(health), String.valueOf(maxhp)));
                }
            }
        }
        return currenttip;
    }

    @Nonnull
    @Override
    public List<String> getWailaTail(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        if (entity instanceof MultiPartEntityPart) {
            IEntityMultiPart parent = ((MultiPartEntityPart) entity).parent;
            if (parent instanceof IEntityBL) {
                if (!Strings.isNullOrEmpty(FormattingConfig.modNameFormat)) {
                    String unknown = String.format(FormattingConfig.modNameFormat, "Unknown");
                    if (currenttip.removeIf(unknown::equals)) {
                        currenttip.add(String.format(FormattingConfig.modNameFormat, ModIdentification.findModContainer(ModInfo.ID).getName()));
                    }
                }
            }
        }

        return currenttip;
    }
}

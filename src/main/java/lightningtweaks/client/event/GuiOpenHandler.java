package lightningtweaks.client.event;

import java.awt.Color;

import javax.swing.JColorChooser;

import lightningtweaks.LightningTweaks;
import lightningtweaks.common.config.LTConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class GuiOpenHandler {
    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent event) {
	GuiScreen screen = event.getGui();
	if (screen instanceof GuiConfig) {
	    GuiConfig config = (GuiConfig) screen;
	    if (config.modID.equals(LightningTweaks.MODID) && LTConfig.LIGHTNING_COLOR_NAME.equals(config.titleLine2)) {
		Color color = JColorChooser.showDialog(null, LightningTweaks.MODID,
			new Color(LTConfig.lightningColor.red(), LTConfig.lightningColor.green(),
				LTConfig.lightningColor.blue(), LTConfig.lightningColor.alpha()));
		if (color != null)
		    LTConfig.lightningColor.set(color);
		event.setCanceled(true);
	    }
	}
    }
}
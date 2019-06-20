package caffeinatedpinkie.lightningtweaks;

import caffeinatedpinkie.lightningtweaks.common.AttributeMetallic;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

/**
 * The base mod file for Lightning Tweaks.
 *
 * @author CaffeinatedPinkie
 */
@Mod(modid = LightningTweaks.MODID, version = LightningTweaks.VERSION, useMetadata = true, acceptableRemoteVersions = "*")
public class LightningTweaks {
    public static final String MODID = "lightningtweaks", VERSION = "1.12.2-1.2.1.0";

    /**
     * The main {@link FMLPostInitializationEvent} handler.
     *
     * @param event the {@link FMLPostInitializationEvent}
     */
    @EventHandler
    public static void postInit(FMLPostInitializationEvent event) {
	LoggerLT.log(() -> AttributeMetallic.postInit(event), "Post-init completed");
    }

    /**
     * Refreshes any data that is generated at initialization.
     */
    public static void refresh() {
	LoggerLT.log(() -> AttributeMetallic.setItems(), "Refreshed mod");
    }
}

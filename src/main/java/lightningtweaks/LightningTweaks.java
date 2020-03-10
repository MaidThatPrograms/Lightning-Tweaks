package lightningtweaks;

import lightningtweaks.common.IProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

@Mod(modid = LightningTweaks.MODID, version = "1.12.2-2.0.0.0-alpha2", useMetadata = true, updateJSON = "https://raw.githubusercontent.com/JamieBrassel/Lightning-Tweaks/1.12.2-2.x.x.x/update.json")
public class LightningTweaks {
    public static final String MODID = "lightningtweaks";
    @SidedProxy(clientSide = MODID + ".client.ClientProxy", serverSide = MODID + ".server.ServerProxy")
    private static IProxy proxy;

    @EventHandler
    public static void postInit(@SuppressWarnings("unused") FMLPostInitializationEvent event) {
	System.out.println("Post-initializating!");
	proxy.postInit();
    }
}
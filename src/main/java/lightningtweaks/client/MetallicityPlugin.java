package lightningtweaks.client;

import java.util.List;

import lightningtweaks.LightningTweaks;
import lightningtweaks.common.ResistivityMap;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * TODO
 */
@WailaPlugin
public class MetallicityPlugin implements IComponentProvider, IWailaPlugin {
	public static ResourceLocation configMetallic = new ResourceLocation(LightningTweaks.MODID, "resistivity");

	/**
	 * TODO
	 */
	@Override
	public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
		if (config.get(configMetallic))
			tooltip.add(new TranslationTextComponent(
					"config.waila.plugin_" + configMetallic.getNamespace() + '.' + configMetallic.getPath())
							.appendText(": " + ResistivityMap.getType(accessor.getBlock().asItem()).toString()));
	}

	/**
	 * TODO
	 */
	@Override
	public void register(IRegistrar registrar) {
		registrar.addConfig(configMetallic, false);
		registrar.registerComponentProvider(this, TooltipPosition.BODY, Block.class);
	}
}

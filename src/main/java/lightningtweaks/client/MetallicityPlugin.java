package lightningtweaks.client;

import java.util.List;

import lightningtweaks.LightningTweaks;
import lightningtweaks.common.LTConfig;
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
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * TODO
 */
@WailaPlugin
public class MetallicityPlugin implements IComponentProvider, IWailaPlugin {
	public static ResourceLocation configMetallic = new ResourceLocation(LightningTweaks.MODID, "metallic");

	/**
	 * TODO
	 */
	@Override
	public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
		if (config.get(configMetallic)) {
			boolean metallic = LTConfig.getMetallicItems().contains(accessor.getBlock().asItem());
			tooltip.add(new TranslationTextComponent(
					"config.waila.plugin_" + configMetallic.getNamespace() + '.' + configMetallic.getPath())
							.appendText(": ").appendSibling(new StringTextComponent(String.valueOf(metallic)))
							.setStyle(new Style().setColor(metallic ? TextFormatting.GREEN : TextFormatting.RED)));
		}
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

/*
 * Copyright (c) 2014-2026 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.client.other_features;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.client.DontBlock;
import net.client.SearchTags;
import net.client.other_feature.OtherFeature;
import net.client.settings.EnumSetting;

@SearchTags({"wurst options", "settings"})
@DontBlock
public final class WurstOptionsOtf extends OtherFeature
{
	private static final Identifier WURST_TEXTURE =
		Identifier.fromNamespaceAndPath("wurst", "wurst_128.png");
	
	private final EnumSetting<Location> location = new EnumSetting<>("Location",
		"description.wurst.setting.wurstoptions.location", Location.values(),
		Location.GAME_MENU);
	
	public WurstOptionsOtf()
	{
		super("WurstOptions", "description.wurst.other_feature.wurstoptions");
		addSetting(location);
	}
	
	public boolean isVisibleInGameMenu()
	{
		return WURST.isEnabled()
			&& location.getSelected() == Location.GAME_MENU;
	}
	
	public boolean isVisibleInStatistics()
	{
		return WURST.isEnabled()
			&& location.getSelected() == Location.STATISTICS;
	}
	
	public Button.Builder buttonBuilder(OnPress onPress)
	{
		MutableComponent message = Component.literal("            Options");
		
		MutableComponent narration =
			Component.translatable("gui.narrate.button", "Wurst Options");
		
		Tooltip tooltip = Tooltip.create(Component.literal(getDescription()));
		
		return Button.builder(message, onPress)
			.createNarration(sup -> narration).tooltip(tooltip);
	}
	
	public void drawWurstLogoOnButton(GuiGraphicsExtractor context,
		Button wurstOptionsButton)
	{
		// Logo display removed.
	}
	
	private enum Location
	{
		GAME_MENU("Game Menu"),
		STATISTICS("Statistics");
		
		private final String name;
		
		private Location(String name)
		{
			this.name = name;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
}

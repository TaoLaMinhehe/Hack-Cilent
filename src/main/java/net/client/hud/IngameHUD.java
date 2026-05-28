/*
 * Copyright (c) 2014-2026 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.client.hud;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.client.WurstClient;
import net.client.clickgui.ClickGui;
import net.client.clickgui.screens.ClickGuiScreen;
import net.client.events.GUIRenderListener;

public final class IngameHUD implements GUIRenderListener
{
	private final HackListHUD hackList = new HackListHUD();
	private TabGui tabGui;
	
	@Override
	public void onRenderGUI(GuiGraphicsExtractor context, float partialTicks)
	{
		if(!WurstClient.INSTANCE.isEnabled())
			return;
		
		if(tabGui == null)
			tabGui = new TabGui();
		
		ClickGui clickGui = WurstClient.INSTANCE.getGui();
		
		clickGui.updateColors();
		
		hackList.render(context, partialTicks);
		tabGui.render(context, partialTicks);
		
		// pinned windows
		if(!(WurstClient.MC.screen instanceof ClickGuiScreen))
			clickGui.renderPinnedWindows(context, partialTicks);
	}
	
	public HackListHUD getHackList()
	{
		return hackList;
	}
}

/*
 * Copyright (c) 2014-2026 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.client.clickgui.components;

import java.util.Objects;

import net.client.clickgui.screens.EditBlockListScreen;
import net.client.settings.BlockListSetting;
import net.client.settings.Setting;

public final class BlockListEditButton extends AbstractListEditButton
{
	private final BlockListSetting setting;
	
	public BlockListEditButton(BlockListSetting setting)
	{
		this.setting = Objects.requireNonNull(setting);
		setWidth(getDefaultWidth());
		setHeight(getDefaultHeight());
	}
	
	@Override
	protected void openScreen()
	{
		MC.setScreen(new EditBlockListScreen(MC.screen, setting));
	}
	
	@Override
	protected String getText()
	{
		return setting.getName() + ": " + setting.size();
	}
	
	@Override
	protected Setting getSetting()
	{
		return setting;
	}
}

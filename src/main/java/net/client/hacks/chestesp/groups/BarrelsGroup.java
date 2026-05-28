/*
 * Copyright (c) 2014-2026 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.client.hacks.chestesp.groups;

import java.awt.Color;

import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.client.hacks.chestesp.ChestEspBlockGroup;
import net.client.settings.CheckboxSetting;
import net.client.settings.ColorSetting;
import net.client.util.LootrModCompat;

public final class BarrelsGroup extends ChestEspBlockGroup
{
	@Override
	protected CheckboxSetting createIncludeSetting()
	{
		return new CheckboxSetting("Include barrels", true);
	}
	
	@Override
	protected ColorSetting createColorSetting()
	{
		return new ColorSetting("Barrel color",
			"Barrels will be highlighted in this color.", Color.GREEN);
	}
	
	@Override
	protected boolean matches(BlockEntity be)
	{
		return be instanceof BarrelBlockEntity
			|| LootrModCompat.isLootrBarrel(be);
	}
}

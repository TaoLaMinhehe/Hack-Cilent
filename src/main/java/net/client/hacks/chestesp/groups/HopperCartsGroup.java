/*
 * Copyright (c) 2014-2026 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.client.hacks.chestesp.groups;

import java.awt.Color;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.minecart.MinecartHopper;
import net.client.hacks.chestesp.ChestEspEntityGroup;
import net.client.settings.CheckboxSetting;
import net.client.settings.ColorSetting;

public final class HopperCartsGroup extends ChestEspEntityGroup
{
	@Override
	protected CheckboxSetting createIncludeSetting()
	{
		return new CheckboxSetting("Include hopper carts", false);
	}
	
	@Override
	protected ColorSetting createColorSetting()
	{
		return new ColorSetting("Hopper cart color",
			"Minecarts with hoppers will be highlighted in this color.",
			Color.YELLOW);
	}
	
	@Override
	protected boolean matches(Entity e)
	{
		return e instanceof MinecartHopper;
	}
}

/*
 * Copyright (c) 2014-2026 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.client.hacks;

import java.util.Random;

import net.client.Category;
import net.client.SearchTags;
import net.client.events.UpdateListener;
import net.client.hack.Hack;
import net.client.util.Rotation;

@SearchTags({"Retarded"})
public final class DerpHack extends Hack implements UpdateListener
{
	private final Random random = new Random();
	
	public DerpHack()
	{
		super("Derp");
		setCategory(Category.FUN);
	}
	
	@Override
	protected void onEnable()
	{
		// disable incompatible derps
		WURST.getHax().headRollHack.setEnabled(false);
		WURST.getHax().tiredHack.setEnabled(false);
		
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		float yaw = MC.player.getYRot() + random.nextFloat() * 360F - 180F;
		float pitch = random.nextFloat() * 180F - 90F;
		
		new Rotation(yaw, pitch).sendPlayerLookPacket();
	}
}

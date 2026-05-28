/*
 * Copyright (c) 2014-2026 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.client.hacks;

import net.minecraft.client.KeyMapping;
import net.client.Category;
import net.client.SearchTags;
import net.client.events.UpdateListener;
import net.client.hack.Hack;
import net.client.mixinterface.IKeyMapping;
import net.client.settings.SliderSetting;
import net.client.settings.SliderSetting.ValueDisplay;

@SearchTags({"miley cyrus", "twerk", "wrecking ball"})
public final class MileyCyrusHack extends Hack implements UpdateListener
{
	private final SliderSetting twerkSpeed = new SliderSetting("Twerk speed",
		"I came in like a wreeecking baaall...", 5, 1, 10, 1,
		ValueDisplay.INTEGER);
	
	private int timer;
	
	public MileyCyrusHack()
	{
		super("MileyCyrus");
		setCategory(Category.FUN);
		addSetting(twerkSpeed);
	}
	
	@Override
	protected void onEnable()
	{
		timer = 0;
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		IKeyMapping.get(MC.options.keyShift).resetPressedState();
	}
	
	@Override
	public void onUpdate()
	{
		timer++;
		if(timer < 10 - twerkSpeed.getValueI())
			return;
		
		KeyMapping sneakKey = MC.options.keyShift;
		sneakKey.setDown(!sneakKey.isDown());
		timer = -1;
	}
}

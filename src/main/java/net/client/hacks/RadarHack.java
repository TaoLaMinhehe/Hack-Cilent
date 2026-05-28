/*
 * Copyright (c) 2014-2026 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.client.hacks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.client.Category;
import net.client.SearchTags;
import net.client.clickgui.Window;
import net.client.clickgui.components.RadarComponent;
import net.client.events.UpdateListener;
import net.client.hack.Hack;
import net.client.settings.CheckboxSetting;
import net.client.settings.SliderSetting;
import net.client.settings.SliderSetting.ValueDisplay;
import net.client.settings.filterlists.EntityFilterList;
import net.client.settings.filters.*;
import net.client.util.FakePlayerEntity;

@SearchTags({"MiniMap", "mini map"})
public final class RadarHack extends Hack implements UpdateListener
{
	private final Window window;
	private final ArrayList<Entity> entities = new ArrayList<>();
	
	private final SliderSetting radius = new SliderSetting("Radius",
		"Radius in blocks.", 100, 1, 100, 1, ValueDisplay.INTEGER);
	private final CheckboxSetting rotate =
		new CheckboxSetting("Rotate with player", true);
	
	private final EntityFilterList entityFilters =
		new EntityFilterList(FilterPlayersSetting.genericVision(false),
			FilterSleepingSetting.genericVision(false),
			FilterHostileSetting.genericVision(false),
			FilterPassiveSetting.genericVision(false),
			FilterPassiveWaterSetting.genericVision(false),
			FilterBatsSetting.genericVision(true),
			FilterSlimesSetting.genericVision(false),
			FilterInvisibleSetting.genericVision(false));
	
	public RadarHack()
	{
		super("Radar");
		
		setCategory(Category.RENDER);
		addSetting(radius);
		addSetting(rotate);
		entityFilters.forEach(this::addSetting);
		
		window = new Window("Radar");
		window.setPinned(true);
		window.setInvisible(true);
		window.add(new RadarComponent(this));
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
		window.setInvisible(false);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		window.setInvisible(true);
	}
	
	@Override
	public void onUpdate()
	{
		LocalPlayer player = MC.player;
		ClientLevel world = MC.level;
		
		entities.clear();
		Stream<Entity> stream = StreamSupport
			.stream(world.entitiesForRendering().spliterator(), true)
			.filter(e -> !e.isRemoved() && e != player)
			.filter(e -> !(e instanceof FakePlayerEntity))
			.filter(LivingEntity.class::isInstance)
			.filter(e -> ((LivingEntity)e).getHealth() > 0);
		
		stream = entityFilters.applyTo(stream);
		
		entities.addAll(stream.collect(Collectors.toList()));
	}
	
	public Window getWindow()
	{
		return window;
	}
	
	public Iterable<Entity> getEntities()
	{
		return Collections.unmodifiableList(entities);
	}
	
	public double getRadius()
	{
		return radius.getValue();
	}
	
	public boolean isRotateEnabled()
	{
		return rotate.isChecked();
	}
}

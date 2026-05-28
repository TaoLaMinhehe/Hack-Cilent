/*
 * Copyright (c) 2014-2026 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.client.hacks;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.client.Category;
import net.client.events.CameraTransformViewBobbingListener;
import net.client.events.RenderListener;
import net.client.events.UpdateListener;
import net.client.hack.Hack;
import net.client.hacks.chestesp.ChestEspGroup;
import net.client.hacks.chestesp.ChestEspGroupManager;
import net.client.settings.EspStyleSetting;
import net.client.util.RenderUtils;
import net.client.util.chunk.ChunkUtils;

public class ChestEspHack extends Hack implements UpdateListener,
	CameraTransformViewBobbingListener, RenderListener
{
	private final EspStyleSetting style = new EspStyleSetting();
	private final ChestEspGroupManager groups = new ChestEspGroupManager();
	
	public ChestEspHack()
	{
		super("ChestESP");
		setCategory(Category.RENDER);
		addSetting(style);
		groups.allGroups.stream().flatMap(ChestEspGroup::getSettings)
			.forEach(this::addSetting);
	}
	
	@Override
	protected void onEnable()
	{
		EVENTS.add(UpdateListener.class, this);
		EVENTS.add(CameraTransformViewBobbingListener.class, this);
		EVENTS.add(RenderListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
		EVENTS.remove(CameraTransformViewBobbingListener.class, this);
		EVENTS.remove(RenderListener.class, this);
		groups.allGroups.forEach(ChestEspGroup::clear);
	}
	
	@Override
	public void onUpdate()
	{
		groups.allGroups.forEach(ChestEspGroup::clear);
		ChunkUtils.getLoadedBlockEntities().forEach(
			be -> groups.blockGroups.forEach(group -> group.addIfMatches(be)));
		MC.level.entitiesForRendering().forEach(
			e -> groups.entityGroups.forEach(group -> group.addIfMatches(e)));
	}
	
	@Override
	public void onCameraTransformViewBobbing(
		CameraTransformViewBobbingEvent event)
	{
		if(style.hasLines())
			event.cancel();
	}
	
	@Override
	public void onRender(PoseStack matrixStack, float partialTicks)
	{
		groups.entityGroups.stream().filter(ChestEspGroup::isEnabled)
			.forEach(g -> g.updateBoxes(partialTicks));
		
		if(style.hasBoxes())
			renderBoxes(matrixStack);
		
		if(style.hasLines())
			renderTracers(matrixStack, partialTicks);
	}
	
	private void renderBoxes(PoseStack matrixStack)
	{
		for(ChestEspGroup group : groups.allGroups)
		{
			if(!group.isEnabled())
				continue;
			
			List<AABB> boxes = group.getBoxes();
			int quadsColor = group.getColorI(0x40);
			int linesColor = group.getColorI(0x80);
			
			RenderUtils.drawSolidBoxes(matrixStack, boxes, quadsColor, false);
			RenderUtils.drawOutlinedBoxes(matrixStack, boxes, linesColor,
				false);
		}
	}
	
	private void renderTracers(PoseStack matrixStack, float partialTicks)
	{
		for(ChestEspGroup group : groups.allGroups)
		{
			if(!group.isEnabled())
				continue;
			
			List<AABB> boxes = group.getBoxes();
			List<Vec3> ends = boxes.stream().map(AABB::getCenter).toList();
			int color = group.getColorI(0x80);
			
			RenderUtils.drawTracers(matrixStack, partialTicks, ends, color,
				false);
		}
	}
}

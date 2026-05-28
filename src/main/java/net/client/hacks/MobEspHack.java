/*
 * Copyright (c) 2014-2026 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.client.hacks;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.client.Category;
import net.client.SearchTags;
import net.client.events.CameraTransformViewBobbingListener;
import net.client.events.RenderListener;
import net.client.events.UpdateListener;
import net.client.hack.Hack;
import net.client.settings.EspBoxSizeSetting;
import net.client.settings.EspStyleSetting;
import net.client.settings.filterlists.EntityFilterList;
import net.client.settings.filters.*;
import net.client.util.EntityUtils;
import net.client.util.RenderUtils;
import net.client.util.RenderUtils.ColoredBox;
import net.client.util.RenderUtils.ColoredPoint;

@SearchTags({"mob esp", "MobTracers", "mob tracers"})
public final class MobEspHack extends Hack implements UpdateListener,
	CameraTransformViewBobbingListener, RenderListener
{
	private final EspStyleSetting style = new EspStyleSetting();
	
	private final EspBoxSizeSetting boxSize = new EspBoxSizeSetting(
		"\u00a7lAccurate\u00a7r mode shows the exact hitbox of each mob.\n"
			+ "\u00a7lFancy\u00a7r mode shows slightly larger boxes that look better.");
	
	private final EntityFilterList entityFilters =
		new EntityFilterList(FilterHostileSetting.genericVision(false),
			FilterNeutralSetting
				.genericVision(AttackDetectingEntityFilter.Mode.OFF),
			FilterPassiveSetting.genericVision(false),
			FilterPassiveWaterSetting.genericVision(false),
			FilterBatsSetting.genericVision(false),
			FilterSlimesSetting.genericVision(false),
			FilterPetsSetting.genericVision(false),
			FilterVillagersSetting.genericVision(false),
			FilterZombieVillagersSetting.genericVision(false),
			FilterGolemsSetting.genericVision(false),
			FilterPiglinsSetting
				.genericVision(AttackDetectingEntityFilter.Mode.OFF),
			FilterZombiePiglinsSetting
				.genericVision(AttackDetectingEntityFilter.Mode.OFF),
			FilterEndermenSetting
				.genericVision(AttackDetectingEntityFilter.Mode.OFF),
			FilterShulkersSetting.genericVision(false),
			FilterAllaysSetting.genericVision(false),
			FilterInvisibleSetting.genericVision(false),
			FilterNamedSetting.genericVision(false),
			FilterArmorStandsSetting.genericVision(true));
	
	private final ArrayList<LivingEntity> mobs = new ArrayList<>();
	
	public MobEspHack()
	{
		super("MobESP");
		setCategory(Category.RENDER);
		addSetting(style);
		addSetting(boxSize);
		entityFilters.forEach(this::addSetting);
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
	}
	
	@Override
	public void onUpdate()
	{
		mobs.clear();
		
		Stream<LivingEntity> stream = StreamSupport
			.stream(MC.level.entitiesForRendering().spliterator(), false)
			.filter(LivingEntity.class::isInstance).map(e -> (LivingEntity)e)
			.filter(e -> !(e instanceof Player))
			.filter(e -> !e.isRemoved() && e.getHealth() > 0);
		
		stream = entityFilters.applyTo(stream);
		
		mobs.addAll(stream.collect(Collectors.toList()));
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
		if(style.hasBoxes())
		{
			double extraSize = boxSize.getExtraSize() / 2;
			
			ArrayList<ColoredBox> boxes = new ArrayList<>(mobs.size());
			for(LivingEntity e : mobs)
			{
				AABB box = EntityUtils.getLerpedBox(e, partialTicks)
					.move(0, extraSize, 0).inflate(extraSize);
				boxes.add(new ColoredBox(box, getColor(e)));
			}
			
			RenderUtils.drawOutlinedBoxes(matrixStack, boxes, false);
		}
		
		if(style.hasLines())
		{
			ArrayList<ColoredPoint> ends = new ArrayList<>(mobs.size());
			for(LivingEntity e : mobs)
			{
				Vec3 point =
					EntityUtils.getLerpedBox(e, partialTicks).getCenter();
				ends.add(new ColoredPoint(point, getColor(e)));
			}
			
			RenderUtils.drawTracers(matrixStack, partialTicks, ends, false);
		}
	}
	
	private int getColor(LivingEntity e)
	{
		float f = MC.player.distanceTo(e) / 20F;
		float r = Mth.clamp(2 - f, 0, 1);
		float g = Mth.clamp(f, 0, 1);
		float[] rgb = {r, g, 0};
		return RenderUtils.toIntColor(rgb, 0.5F);
	}
}

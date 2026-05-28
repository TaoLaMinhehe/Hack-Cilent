/*
 * Copyright (c) 2014-2026 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.client.hacks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.client.Category;
import net.client.SearchTags;
import net.client.events.LeftClickListener;
import net.client.events.UpdateListener;
import net.client.hack.DontSaveState;
import net.client.hack.Hack;
import net.client.hacks.nukers.CommonNukerSettings;
import net.client.settings.SliderSetting;
import net.client.settings.SliderSetting.ValueDisplay;
import net.client.settings.SwingHandSetting;
import net.client.settings.SwingHandSetting.SwingHand;
import net.client.util.BlockBreaker;
import net.client.util.BlockUtils;
import net.client.util.RotationUtils;

@SearchTags({"speed nuker", "FastNuker", "fast nuker"})
@DontSaveState
public final class SpeedNukerHack extends Hack implements UpdateListener
{
	private final SliderSetting range =
		new SliderSetting("Range", 5, 1, 6, 0.05, ValueDisplay.DECIMAL);
	
	private final CommonNukerSettings commonSettings =
		new CommonNukerSettings();
	
	private final SwingHandSetting swingHand = new SwingHandSetting(
		SwingHandSetting.genericMiningDescription(this), SwingHand.OFF);
	
	public SpeedNukerHack()
	{
		super("SpeedNuker");
		setCategory(Category.BLOCKS);
		addSetting(range);
		commonSettings.getSettings().forEach(this::addSetting);
		addSetting(swingHand);
	}
	
	@Override
	public String getRenderName()
	{
		return getName() + commonSettings.getRenderNameSuffix();
	}
	
	@Override
	protected void onEnable()
	{
		WURST.getHax().autoMineHack.setEnabled(false);
		WURST.getHax().excavatorHack.setEnabled(false);
		WURST.getHax().nukerHack.setEnabled(false);
		WURST.getHax().nukerLegitHack.setEnabled(false);
		WURST.getHax().tunnellerHack.setEnabled(false);
		WURST.getHax().veinMinerHack.setEnabled(false);
		
		EVENTS.add(LeftClickListener.class, commonSettings);
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(LeftClickListener.class, commonSettings);
		EVENTS.remove(UpdateListener.class, this);
		
		commonSettings.reset();
	}
	
	@Override
	public void onUpdate()
	{
		if(commonSettings.isIdModeWithAir())
			return;
		
		Vec3 eyesVec = RotationUtils.getEyesPos();
		BlockPos eyesBlock = BlockPos.containing(eyesVec);
		double rangeSq = range.getValueSq();
		int blockRange = range.getValueCeil();
		
		Stream<BlockPos> stream =
			BlockUtils.getAllInBoxStream(eyesBlock, blockRange)
				.filter(BlockUtils::canBeClicked)
				.filter(commonSettings::shouldBreakBlock);
		
		if(commonSettings.isSphereShape())
			stream =
				stream.filter(pos -> pos.distToCenterSqr(eyesVec) <= rangeSq);
		
		ArrayList<BlockPos> blocks = stream
			.sorted(
				Comparator.comparingDouble(pos -> pos.distToCenterSqr(eyesVec)))
			.collect(Collectors.toCollection(ArrayList::new));
		
		if(blocks.isEmpty())
			return;
		
		WURST.getHax().autoToolHack.equipIfEnabled(blocks.get(0));
		BlockBreaker.breakBlocksWithPacketSpam(blocks);
		swingHand.swing(InteractionHand.MAIN_HAND);
	}
}

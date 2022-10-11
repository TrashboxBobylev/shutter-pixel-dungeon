/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2022 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class Quarterstaff extends MeleeWeapon {

	{
		image = ItemSpriteSheet.QUARTERSTAFF;
		hitSound = Assets.Sounds.HIT_CRUSH;
		hitSoundPitch = 0.8f;

		tier = 1;
	}

	public static Wand lastWand;
	public static Class<? extends Wand> lastWandClass;

	static {
		lastWand = null;
		lastWandClass = null;
	}

	@Override
	public int max(int lvl) {
		return  7*(tier) +    //8 base, down from 10
				lvl*(tier+1);   //scaling unchanged
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (attacker instanceof Hero && ((Hero)attacker).subClass == HeroSubClass.BATTLEMAGE){
			if (lastWand != null)
				lastWand.onHit(this, attacker, defender, damage);

			if (((Hero) attacker).hasTalent(Talent.EXCESS_CHARGE)){
				Buff.affect(attacker, Talent.AcceleratingChargeTracker.class).increment();
			}
		}

		if (attacker instanceof Hero && ((Hero) attacker).hasTalent(Talent.MYSTICAL_CHARGE)){
			Hero hero = (Hero) attacker;
			for (Buff b : hero.buffs()){
				if (b instanceof Artifact.ArtifactBuff && !((Artifact.ArtifactBuff) b).isCursed() ) {
					((Artifact.ArtifactBuff) b).charge(hero, hero.pointsInTalent(Talent.MYSTICAL_CHARGE)/2f);
				}
			}
		}

		Talent.EmpoweredStrikeTracker empoweredStrike = attacker.buff(Talent.EmpoweredStrikeTracker.class);
		if (empoweredStrike != null){
			damage = Math.round( damage * (1f + Dungeon.hero.pointsInTalent(Talent.EMPOWERED_STRIKE)/6f));
		}

		if (empoweredStrike != null){
			empoweredStrike.detach();
			if (!(defender instanceof Mob) || !((Mob) defender).surprisedBy(attacker)){
				Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG, 0.75f, 1.2f);
			}
		}

		return super.proc(attacker, defender, damage);
	}

	public String statsInfo(){
		String stats_desc = Messages.get(this, "stats_desc", 35 * (buffedVisiblyUpgraded() + 1));
		if (Dungeon.hero.subClass == HeroSubClass.BATTLEMAGE && lastWand != null){
			stats_desc += "\n\n" + Messages.get(lastWand, "bmage_desc");
		}
		return stats_desc;
	}
}

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Combo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class BetterBlocking extends Weapon.Enchantment {

    private static ItemSprite.Glowing BLUE = new ItemSprite.Glowing( 0x0000FF );

    @Override
    public ItemSprite.Glowing glowing() {
        return BLUE;
    }

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        int level = Math.max( 0, weapon.buffedLvl() );

        // lvl 0 - 16%
        // lvl 1 - 28%
        // lvl 2 - 33%
        float procChance = (level+1f)/(level+6f) * procChanceMultiplier(attacker);
        if (Random.Float() < procChance) {

            final Ballistica bolt = new Ballistica(attacker.pos, defender.pos, Ballistica.WONT_STOP);

            final ConeAOE cone = new ConeAOE(bolt, 6, 70, Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID);

            //cast to cells at the tip, rather than all cells, better performance.
            for (Ballistica ray : cone.outerRays) {
                attacker.sprite.parent.add(new Beam.SpiritRay(attacker.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(ray.collisionPos)));
                ArrayList<Char> chars = new ArrayList<>();
                for (int c : ray.subPath(1, ray.dist)) {

                    Char ch;
                    if ((ch = Actor.findChar(c)) != null) {

                        chars.add(ch);
                    }

                    CellEmitter.center(c).burst(PurpleParticle.BURST, Random.IntRange(1, 2));
                }

                for (Char ch : chars) {
                    int dmg = Math.round(damage * 0.5f);
                    weapon.proc(attacker, defender, dmg);
                    ch.damage(dmg, this);
                    Splash.at(ch.pos, 0xCC99FFFF, 2);
                    ch.sprite.flash();
                    if (attacker instanceof Hero && ((Hero) attacker).subClass == HeroSubClass.GLADIATOR) {
                        Buff.affect(attacker, Combo.class).hit(ch);
                    }
                }
            }
            return -1;
        }
        return damage;
    }
}

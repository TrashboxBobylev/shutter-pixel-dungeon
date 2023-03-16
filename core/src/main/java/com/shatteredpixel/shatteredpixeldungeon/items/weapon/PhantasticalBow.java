package com.shatteredpixel.shatteredpixeldungeon.items.weapon;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class PhantasticalBow extends SpiritBow {
    {
        image = ItemSpriteSheet.PHAN_BOW;
    }

    @Override
    public int getRegularShotCost() {
        return 0;
    }

    @Override
    public int STRReq(int lvl) {
        return STRReq(2, lvl); //tier 2
    }

    @Override
    public int min(int lvl) {
        int dmg = 1 + Math.round(Dungeon.hero.lvl/8f)
                + (RingOfSharpshooting.levelDamageBonus(Dungeon.hero)+lvl)/2;
        return Math.max(0, dmg);
    }

    @Override
    public int max(int lvl) {
        int dmg = 4 + (int)(Dungeon.hero.lvl/4f)
                + (RingOfSharpshooting.levelDamageBonus(Dungeon.hero)+lvl);
        return Math.max(0, dmg);
    }

    @Override
    public boolean isUpgradable() {
        return true;
    }

    @Override
    public int buffedLvl(){
        if (Dungeon.hero.buff( Degrade.class ) != null) {
            return Degrade.reduceLevel(level());
        } else {
            return level();
        }
    }

    @Override
    public SpiritArrow knockArrow() {
        return new PhantasticalArrow();
    }

    public class PhantasticalArrow extends SpiritArrow {

    }
}

package com.shatteredpixel.shatteredpixeldungeon.items.weapon;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
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
        int dmg = 1 + Math.round(Dungeon.hero.lvl/6f)
                + RingOfSharpshooting.levelDamageBonus(Dungeon.hero)/2
                + (curseInfusionBonus ? 1 + Dungeon.hero.lvl/30 : 0);
        return Math.max(0, dmg);
    }

    @Override
    public int max(int lvl) {
        int dmg = 5 + (int)(Dungeon.hero.lvl/4f)
                + RingOfSharpshooting.levelDamageBonus(Dungeon.hero)
                + (curseInfusionBonus ? 2 + Dungeon.hero.lvl/15 : 0);
        return Math.max(0, dmg);
    }
}

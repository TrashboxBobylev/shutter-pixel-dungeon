package com.shatteredpixel.shatteredpixeldungeon.items.quest;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class BetterPickaxe extends Pickaxe {
    {
        image = ItemSpriteSheet.BETTER_PICKAXE;

        levelKnown = true;

        unique = true;
        bones = false;

        tier = 5;
        DLY = 0.5f; //2x speed
    }

    @Override
    public int STRReq(int lvl) {
        return super.STRReq(lvl) - 2;
    }

    @Override
    public int min(int lvl) {
        return  (tier - 3) +  //2 base, down from 5
                lvl;    //level scaling
    }

    @Override
    public int max(int lvl) {
        return  5*(tier-2) +    //15 base, down from 30
                lvl*(tier-2);   //+3 per level, down from +6
    }
}

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class BurntPan extends MeleeWeapon {
    {
        image = ItemSpriteSheet.PAN;
        hitSound = Assets.Sounds.HIT_CRUSH;
        hitSoundPitch = 0.6f;

        tier = 3;
        DLY = 0.25f; //this is important
        ACC = 0.85f;
    }

    @Override
    public void hitSound(float pitch) {
        int level = 0;
        if (Dungeon.hero.buff(BurntPanTracker.class) != null){
            level = (Dungeon.hero.buff(BurntPanTracker.class).level-1);
        }
        Sample.INSTANCE.play(hitSound, 1 + 0.1f * level,
                pitch * (hitSoundPitch + 0.2f * level));
    }

    @Override
    public int min(int lvl) {
        int level = 1;
        if (Dungeon.hero.buff(BurntPanTracker.class) != null){
            level = (Dungeon.hero.buff(BurntPanTracker.class).level);
        }
        return  (tier-2 +  //1, down from 3
                lvl)*level;    //same level scaling
    }

    protected float baseDelay( Char owner ){
        if (owner instanceof Hero)
            return super.baseDelay(owner);
        else
            return 1f;
    }

    @Override
    public int max(int lvl) {
        int level = 1;
        if (Dungeon.hero.buff(BurntPanTracker.class) != null){
            level = (Dungeon.hero.buff(BurntPanTracker.class).level);
        }
        return  (4*(tier-1) +    //8 base, down from 20
                lvl*(tier-1))*level;   //+2 per level, down from 4
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        RoundShield.guardAbility(hero, 5, this);
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (attacker instanceof Hero) {
            BurntPanTracker buff = Buff.affect(attacker, BurntPanTracker.class);
            buff.increment();
            int level = buff.level;
            if (level >= 4) {
                buff.detach();
                defender.sprite.emitter().burst(Speck.factory(Speck.CROWN), 12);
                Sample.INSTANCE.play(Assets.Sounds.LEVELUP, 1, 1.5f);
            }
        }
        return super.proc(attacker, defender, damage);
    }

    public static class BurntPanTracker extends Buff {

        public int level;

        @Override
        public int icon() {
            return BuffIndicator.COMBO;
        }

        public void increment(){
            level = Math.min(level+1, 4);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", level);
        }

        @Override
        public void tintIcon(Image icon) {
            switch (level){
                case 1:
                    icon.hardlight(0f, 1f, 0f);
                    break;
                case 2:
                    icon.hardlight(1f, 1f, 0f);
                    break;
                case 3:
                    icon.hardlight(1f, 0.6f, 0f);
                    break;
                case 4:
                    icon.hardlight(1f, 0f, 0f);
                    break;
            }
        }

        @Override
        public float iconFadePercent() {
            return 1f - (level / 4f);
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString(level);
        }

        @Override
        public boolean act() {
            if (target instanceof Hero){
                if (((Hero) target).justMoved)
                    detach();
            }

            spend(TICK);
            return true;
        }

        @Override
        public void detach() {
            super.detach();
            target.spendToWhole();
        }

        private static final String TURNS = "panLevel";

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            level = bundle.getInt(TURNS);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(TURNS, level);
        }
    }
}

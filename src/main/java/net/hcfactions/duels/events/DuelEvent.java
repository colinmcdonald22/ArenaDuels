package net.hcfactions.duels.events;

import net.hcfactions.duels.Duel;
import org.bukkit.event.Event;

public abstract class DuelEvent extends Event {

    private Duel duel;

    public DuelEvent(Duel duel) {
        this.duel = duel;
    }

    public Duel getDuel() {
        return duel;
    }

}
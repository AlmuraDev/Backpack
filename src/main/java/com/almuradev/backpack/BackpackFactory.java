package com.almuradev.backpack;

import com.almuradev.backpack.backend.DatabaseManager;
import com.almuradev.backpack.backend.entity.Backpacks;
import com.google.common.collect.Sets;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class BackpackFactory {
    private static final Set<BackpackInventory> BACKPACKS = Sets.newConcurrentHashSet();

    public static BackpackInventory load(World world, Player player) throws IOException {
        final Session session = DatabaseManager.getSessionFactory().openSession();
        final Criteria criteria = session.createCriteria(Backpacks.class);
        Backpacks record = (Backpacks) criteria.add(Restrictions.and(Restrictions.eq("worldUniqueId", world.getUniqueId()), Restrictions.eq
                ("playerUniqueId", player.getUniqueId()))).uniqueResult();

        if (record == null) {
            record = new Backpacks();
            record.setWorldUniqueId(world.getUniqueId());
            record.setPlayerUniqueId(player.getUniqueId());
            record.setSize(9);
            record.setTitle(Texts.legacy().to(Texts.of(TextColors.AQUA, "My Backpack")));
            session.beginTransaction();
            session.saveOrUpdate(record);
            session.getTransaction().commit();
        }
        final BackpackInventory inventory = new BackpackInventory(record);
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            DatabaseManager.loadSlot(session, inventory, i);
        }
        session.close();
        BACKPACKS.add(inventory);
        return new BackpackInventory(record);
    }

    public static Optional<BackpackInventory> get(World world, Player player) {
        for (BackpackInventory inventory : BACKPACKS) {
            if (inventory.getRecord().getWorldUniqueId().equals(world.getUniqueId()) && inventory.getRecord().getPlayerUniqueId().equals(player
                    .getUniqueId())) {
                return Optional.of(inventory);
            }
        }

        return Optional.empty();
    }
}

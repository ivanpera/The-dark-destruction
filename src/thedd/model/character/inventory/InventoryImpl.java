package thedd.model.character.inventory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import model.item.Item;

/**
 * Implementation of Inventory interface.
 *
 */
public class InventoryImpl implements Inventory {

    private final Map<Item, Integer> items;
    private int hash;

    /**
     * InventoryImpl constructor.
     */
    public InventoryImpl() {
        this.items = new HashMap<>();
    }

    @Override
    public final boolean addItem(final Item item) {
        if (item == null) {
            return false;
        }
        if (this.items.containsKey(item)) {
            this.items.put(item, this.items.get(item) + 1);
        } else {
            this.items.put(item, 1);
        }
        return true;
    }

    @Override
    public final boolean removeItem(final Item item) {
        if (item == null) {
            return false;
        }
        if (!this.items.containsKey(item)) {
            return false;
        }
        this.items.put(item, this.items.get(item) - 1);
        if (this.items.get(item) <= 0) {
            this.items.remove(item);
        }
        return true;
    }

    @Override
    public final String toString() {
        String ret = "";
        for (Map.Entry<Item, Integer> pair : this.items.entrySet()) {
            ret = ret + "[ Item: " + pair.getKey().toString() + " - Number: " + pair.getValue() + "]\n";
        }
        if (ret.equals("")) {
            ret = "Empty\n";
        }
        return ret;
    }

    @Override
    public final List<Item> getAll() {
        return Collections.unmodifiableList(this.items.keySet().stream().collect(Collectors.toList()));
    }

    @Override
    public final int getQuantity(final Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        if (!this.items.containsKey(item)) {
            return 0;
        } else {
            return this.items.get(item);
        }
    }

    @Override
    public final int hashCode() {
        if (hash == 0) {
            hash = items.hashCode();
        }
        return hash;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (obj instanceof InventoryImpl) {
            final InventoryImpl other = (InventoryImpl) obj;
            return this.getAll().equals(other.getAll());
        }
        return false;
    }
}
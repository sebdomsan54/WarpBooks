package ua.lokha.warpbooks;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * Список айди
 */
public class ArrayIds {

    private int[] ids;

    ArrayIds(List<Integer> ids) {
        this.ids = new int[ids.size()];
        int i = 0;
        for (Integer id : ids) {
            this.ids[i++] = id;
        }
    }

    /**
     * Находится ли этот материал в списке
     * @param material
     * @return
     */
    public boolean contains(Material material) {
        return this.contains(material.getId());
    }

    /**
     * Находится ли этот айди в списке
     * @param id
     * @return
     */
    public boolean contains(int id) {
        for (int idIt : this.ids) {
            if (idIt == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Преобразовать лист материалов в лист айдишек
     * @param materials
     * @return
     */
    public static List<Integer> wrapToListIntegers(List<Material> materials) {
        List<Integer> list = new ArrayList<>();
        for (Material material : materials) {
            list.add(material.getId());
        }
        return list;
    }
}

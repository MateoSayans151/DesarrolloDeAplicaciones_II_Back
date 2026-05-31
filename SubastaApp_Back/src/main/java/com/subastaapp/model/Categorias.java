package com.subastaapp.model;

import java.util.HashMap;
import java.util.Map;

public enum Categorias {
    COMUN,
    ESPECIAL,
    PLATA,
    ORO,
    PLATINO;

    private static Map<Categorias, Integer> categorias_equivalencias = new HashMap<Categorias, Integer>();

    public static boolean isBetterThan(Categorias c1, Categorias c2) {
        categorias_equivalencias.put(Categorias.COMUN, 1);
        categorias_equivalencias.put(Categorias.ESPECIAL, 2);
        categorias_equivalencias.put(Categorias.PLATA, 3);
        categorias_equivalencias.put(Categorias.ORO, 4);
        categorias_equivalencias.put(Categorias.PLATINO, 5);

        int val1 = categorias_equivalencias.get(c1);
        int val2 = categorias_equivalencias.get(c2);

        return val1 > val2;
    }
}

package org.RisingSMP.factory;

import org.bukkit.NamespacedKey;


public class FactoryItems {

    // Chiave unica per identificare i blocchi custom
    public static NamespacedKey FACTORY_KEY;

    // Metodo per inizializzare la chiave (da chiamare in onEnable del main)
    public static void init() {
        FACTORY_KEY = new NamespacedKey(org.RisingSMP.factory.Factory.instance, "factory");
    }
}
package net.saifs.landlord.utils.concurrency;

import net.saifs.landlord.Landlord;

public class ThreadUtil {
    public static void sync(Runnable runnable) {
        Landlord landlord = Landlord.getInstance();
        landlord.getServer().getScheduler().runTask(landlord, runnable);
    }

    public static void async(Runnable runnable) {
        Landlord landlord = Landlord.getInstance();
        landlord.getServer().getScheduler().runTaskAsynchronously(landlord, runnable);
    }
}

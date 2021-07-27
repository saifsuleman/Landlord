package net.saifs.landlord.utils.concurrency;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Promise<T> {
    private boolean completed;
    private T data;
    private Consumer<T> consumer;

    public Promise(Supplier<T> supplier) {
        this.completed = false;
        ThreadUtil.async(() -> {
            this.data = supplier.get();
            this.completed = true;
            if (this.consumer != null) {
                accept(data);
            }
        });
    }

    public void then(Consumer<T> consumer) {
        this.consumer = consumer;
        if (this.completed) {
            accept(data);
        }
    }

    public T await() {
        while (true) if (completed) return data;
    }

    public void accept(T data) {
        ThreadUtil.sync(() -> consumer.accept(data));
    }
}

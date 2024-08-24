package chounion.quizmaven;

import java.nio.file.*;
import java.io.IOException;
import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatcher {
    private final WatchService watcher;
    private final Path dir;
    private final Runnable onChangeCallback;

    public FileWatcher(Path dir, Runnable onChangeCallback) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.dir = dir;
        this.onChangeCallback = onChangeCallback;
        dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
    }

    public void watch() {
        Thread watchThread = new Thread(() -> {
            try {
                while (true) {
                    WatchKey key = watcher.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();
                        if (kind == OVERFLOW) {
                            continue;
                        }
                        Path fileName = (Path) event.context();
                        Path child = dir.resolve(fileName);
                        System.out.println("File changed: " + child);
                        onChangeCallback.run();
                    }
                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        watchThread.setDaemon(true);
        watchThread.start();
    }
}
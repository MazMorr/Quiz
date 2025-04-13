package com.marcosoft.quiz.utils;

import java.nio.file.*;
import java.io.IOException;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * La clase {@code FileWatcher} permite monitorear un directorio específico
 * para detectar cambios en los archivos, como creaciones, modificaciones o eliminaciones.
 * Cuando se detecta un cambio, se ejecuta un callback definido por el usuario.
 */
public class FileWatcher {
    private final WatchService watcher;
    private final Path dir;
    private final Runnable onChangeCallback;

    /**
     * Crea una nueva instancia de {@code FileWatcher}.
     *
     * @param dir               El directorio que se desea monitorear.
     * @param onChangeCallback  Una función de callback que se ejecutará cuando se detecte un cambio.
     * @throws IOException Si ocurre un error al inicializar el {@link WatchService}.
     */
    public FileWatcher(Path dir, Runnable onChangeCallback) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.dir = dir;
        this.onChangeCallback = onChangeCallback;
        dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
    }

    /**
     * Inicia el monitoreo del directorio en un hilo separado.
     * Detecta eventos de creación, modificación y eliminación de archivos.
     * Cuando se detecta un cambio, se ejecuta el callback proporcionado.
     */
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
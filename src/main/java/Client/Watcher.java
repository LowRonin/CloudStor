package Client;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class Watcher {

    private WatchService service;

    public Watcher(Path dir) throws IOException {
        service = FileSystems.getDefault()
                .newWatchService();
        new Thread(this::run).start();
        dir.register(service, ENTRY_CREATE, ENTRY_DELETE);
    }

    private WatchService getService(){
        return service;
    }

    private void run() {
        try {
            while (true) {
                WatchKey watchKey = service.take();
                Client.watcherTrigger = true;
                Thread.sleep(20);
                Client.watcherTrigger = false;
                watchKey.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

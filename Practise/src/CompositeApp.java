import java.nio.file.NotDirectoryException;
import java.util.List;

public class CompositeApp {

    interface FileSystemItem {
        abstract long getSize();

        default boolean add(FileSystemItem item) throws NotDirectoryException {
            throw new NotDirectoryException("Not a directory");
        }

        default boolean remove(FileSystemItem item) throws NotDirectoryException {
            throw new NotDirectoryException("Not a directory");
        }
    }

    class File implements FileSystemItem {
        String name;
        long size;

        @Override
        public long getSize() {
            return this.size;
        }
    }

    class Directory implements FileSystemItem {
        String name;
        List<FileSystemItem> items;

        @Override
        public long getSize() {
            return items.stream().mapToLong(FileSystemItem::getSize).sum();
        }

        @Override
        public boolean add(FileSystemItem item) {
            return this.items.add(item);
        }

        @Override
        public boolean remove(FileSystemItem item) {
            return this.items.remove(item);
        }
    }

    class Client {
        private final Directory directory;

        public Client(Directory directory) {
            this.directory = directory;
        }

        public void process(String directoryName, String opType) {
            if (opType.equals("ADD")) {
                Directory items = new Directory();
                File item = new File();
                item.name = "file1";
                item.size = 4;
                items.add(item);
                this.directory.add(items);
            }

        }
    }

    public static void main(String[] args) {

    }
}

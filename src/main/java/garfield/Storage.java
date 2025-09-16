package garfield;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Handles loading tasks from and saving tasks to the disk.
 * Responsible for file I/O operations related to task persistence.
 */
public class Storage {
    private final Path filePath;

    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    public List<Task> load() throws GarfieldException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            try {
                Files.createDirectories(filePath.getParent());
                Files.createFile(filePath);
            } catch (IOException e) {
                System.out.println("Error creating data file: " + e.getMessage());
                throw new GarfieldException("Could not create data file.");
            }
            return tasks;
        }
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    tasks.add(Task.fromStorageString(line));
                } catch (Exception e) {
                    System.out.println("Corrupted line skipped: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading tasks: " + e.getMessage());
        }
        return tasks;
    }

    public void save(List<Task> tasks) {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (Task task : tasks) {
                writer.write(task.toStorageString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }
}
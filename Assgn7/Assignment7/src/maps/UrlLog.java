//COLLABORATOR - GPT5

package maps;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class UrlLog {
    private static final int MAX_RECENT = 5;

    public static void main(String[] args) throws IOException {
        Path logPath = Paths.get("server_log.csv"); // file at project root

        Map<String, Deque<String>> recentByUser = new TreeMap<>();
        Map<String, Integer> urlCounts = new TreeMap<>();

        try (Stream<String> lines = Files.lines(logPath, StandardCharsets.UTF_8)) {
            lines.skip(1) // skip header
                 .forEach(line -> {
                     if (line == null || line.trim().isEmpty()) return; // Java 8: no isBlank()
                     String[] parts = line.trim().split(",", 3);
                     if (parts.length < 3) return;

                     String user = parts[1].trim();
                     String url  = parts[2].trim();

                     Deque<String> q = recentByUser.computeIfAbsent(user,
                             k -> new ArrayDeque<>(MAX_RECENT));
                     q.addLast(url);
                     if (q.size() > MAX_RECENT) q.removeFirst();

                     urlCounts.merge(url, 1, Integer::sum);
                 });
        }

        // --- Output ---
        
        // OLDEST (5th) --->>> LATEST (1st)
        
        System.out.println("Recent URLs per user:");
        recentByUser.forEach((u, q) -> System.out.println(u + " -> " + new ArrayList<>(q)));

        System.out.println("\nURL Visit Counts:");
        int width = urlCounts.keySet().stream().mapToInt(String::length).max().orElse(0);
        urlCounts.forEach((url, cnt) ->
                System.out.printf("  %-" + width + "s : %d%n", url, cnt));
    }
}

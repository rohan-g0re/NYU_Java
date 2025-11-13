//COLLABORATOR - GPT5






import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;

public class Reader implements Runnable {

    private final Path csvPath;
    private final BlockingQueue<Msg> out;
    private final int numValidators;

    public Reader(Path csvPath, BlockingQueue<Msg> out, int numValidators) {
        this.csvPath = csvPath;
        this.out = out;
        this.numValidators = numValidators;
    }

    @Override
    public void run() {
        try (BufferedReader br = Files.newBufferedReader(csvPath)) {
            String line = br.readLine();

            // Try to skip header line if it looks like one
            if (line != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    String[] p = trimmed.split(",", -1);
                    boolean looksLikeHeader = p.length >= 4
                            && "id".equalsIgnoreCase(p[0].trim());
                    if (!looksLikeHeader) {
                        // first line is actually data
                        Tx tx = parse(line);
                        out.put(new MsgTx(tx, System.nanoTime()));
                    }
                }
            }

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Tx tx = parse(line);
                out.put(new MsgTx(tx, System.nanoTime()));
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } finally {
            // signal each validator that there is no more work
            try {
                for (int i = 0; i < numValidators; i++) {
                    out.put(Stop.INSTANCE);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    
//    I updated the splitter such that it only takes number from the erro transactions becasue it was taking proffesor's comments as well

    static Tx parse(String line) {
        // Simple CSV split; for real CSV use a parser
        String[] p = line.split(",", -1);
        if (p.length < 4) {
            // clearly bad row -> will be classified as "other"
            return new Tx("", "", 0L, 0L);
        }

        String id = p[0].trim();
        String account = p[1].trim();

        long epochSecs = 0L;
        long cents = 0L;

        try {
            epochSecs = Long.parseLong(p[2].trim());
        } catch (Exception e) {
            // keep 0, validator will treat as "other"
        }

        // >>> IMPORTANT: only take the numeric part of the cents column <<<
        String centsField = p[3].trim();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < centsField.length(); i++) {
            char c = centsField.charAt(i);
            if (Character.isDigit(c) || (c == '-' && sb.length() == 0)) {
                sb.append(c);
            } else {
                break; // stop at first non-numeric (space, #, etc.)
            }
        }
        String centsNum = sb.length() == 0 ? "0" : sb.toString();
        try {
            cents = Long.parseLong(centsNum);
        } catch (Exception e) {
            // keep 0, validator will classify as "other" if needed
        }

        return new Tx(id, account, epochSecs, cents);
    }

}

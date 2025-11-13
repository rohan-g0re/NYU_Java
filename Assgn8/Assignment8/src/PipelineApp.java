//COLLABORATOR - GPT5






import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Reader (CSV) -> Validator (2 workers) -> Aggregator
 *                          \-> ErrorWriter
 *
 * All communication via bounded BlockingQueues (capacity 256).
 */
public class PipelineApp {

    public static void main(String[] args) throws Exception {

        // 1. Decide which CSV to use
        Path csvPath;

        if (args.length > 0) {
            // If user passed a filename on command line, use that
            csvPath = Paths.get(args[0]);
        } else {
            // Otherwise, ask interactively
            java.util.Scanner sc = new java.util.Scanner(System.in);
            System.out.println("Choose input file:");
            System.out.println("1 - transactions1.csv");
            System.out.println("2 - transactions_with_errors.csv");

            String choice = sc.nextLine().trim();
            String fileName;
            switch (choice) {
                case "1":
                    fileName = "transactions1.csv";
                    break;
                case "2":
                    fileName = "transactions_with_errors.csv";
                    break;
                default:
                    System.out.println("Unknown choice, defaulting to transactions1.csv");
                    fileName = "transactions1.csv";
            }
            csvPath = Paths.get(fileName);
            // (optional) sc.close();   // you can skip close for this short-lived app
        }

        int capacity = 256;
        int numValidators = 2;

        // ===== the rest of your existing code stays the same =====
        BlockingQueue<Msg> qReaderToVal = new ArrayBlockingQueue<>(capacity);
        BlockingQueue<Msg> qValToAgg   = new ArrayBlockingQueue<>(capacity);
        BlockingQueue<Msg> qValToErr   = new ArrayBlockingQueue<>(capacity);

        Aggregator agg = new Aggregator(qValToAgg, numValidators);
        ErrorWriter errorWriter = new ErrorWriter(qValToErr,
                Paths.get("errors.csv"), numValidators);
        Reader reader = new Reader(csvPath, qReaderToVal, numValidators);
        Validator validator1 = new Validator(qReaderToVal, qValToAgg, qValToErr);
        Validator validator2 = new Validator(qReaderToVal, qValToAgg, qValToErr);

        Thread tAgg = new Thread(agg, "aggregator");
        Thread tErr = new Thread(errorWriter, "error-writer");
        Thread tVal1 = new Thread(validator1, "validator-1");
        Thread tVal2 = new Thread(validator2, "validator-2");
        Thread tReader = new Thread(reader, "reader");

        tAgg.start();
        tErr.start();
        tVal1.start();
        tVal2.start();
        tReader.start();

        tReader.join();
        tVal1.join();
        tVal2.join();
        tAgg.join();
        tErr.join();

        Map<AggKey, Long> totals = agg.snapshotTotals();
        System.out.println("Top 10 aggregates:");
        totals.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(10)
                .forEach(e ->
                        System.out.println(e.getKey() + "          -> "
                                + e.getValue() + " cents"));

        System.out.println("Errors written to "
                + Paths.get("errors.csv").toAbsolutePath());
    }
}





//====== Data model ======
final class Tx {
    final String id;
    final String account;
    final long epochSec;
    final long cents;

    Tx(String id, String account, long epochSec, long cents) {
        this.id = id;
        this.account = account;
        this.epochSec = epochSec;
        this.cents = cents;
    }

    public String toString() {
        return id + "," + account + "," + epochSec + "," + cents;
    }
}

final class AggKey {
    final String account;
    final LocalDate day; // UTC day

    AggKey(String account, LocalDate day) {
        this.account = account;
        this.day = day;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AggKey)) return false;
        AggKey k = (AggKey) o;
        return Objects.equals(account, k.account) && Objects.equals(day, k.day);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, day);
    }

    @Override
    public String toString() {
        return account + " @ " + day;
    }
}



// ====== Messaging between stages ======
interface Msg {}

final class MsgTx implements Msg {
    final Tx tx;
    final long t0Nanos;

    MsgTx(Tx tx, long t0Nanos) {
        this.tx = tx;
        this.t0Nanos = t0Nanos;
    }
}



final class Stop implements Msg {
    static final Stop INSTANCE = new Stop();
    private Stop() {}
}

//COLLABORATOR - GPT5






import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class Aggregator implements Runnable {

    private final BlockingQueue<Msg> in;
    private final int expectedStops;

    // only touched by this single thread, so plain HashMap is fine
    private final Map<String, Boolean> seenIds = new HashMap<>();
    private final Map<AggKey, Long> totals = new HashMap<>();

    public Aggregator(BlockingQueue<Msg> in, int expectedStops) {
        this.in = in;
        this.expectedStops = expectedStops;
    }

    @Override
    public void run() {
        int stopCount = 0;
        try {
            while (true) {
                Msg msg = in.take();

                if (msg instanceof Stop) {
                    stopCount++;
                    if (stopCount >= expectedStops) {
                        break;        // all validators have finished
                    }
                    continue;
                }

                if (!(msg instanceof MsgTx)) {
                    continue;
                }

                Tx tx = ((MsgTx) msg).tx;

                // de-duplicate by transaction id
                if (tx.id == null || tx.id.isEmpty()) {
                    continue;
                }
                if (seenIds.containsKey(tx.id)) {
                    continue;
                }
                seenIds.put(tx.id, Boolean.TRUE);

                LocalDate day = Instant.ofEpochSecond(tx.epochSec)
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate();
                AggKey key = new AggKey(tx.account, day);

                long current = totals.containsKey(key) ? totals.get(key) : 0L;
                totals.put(key, current + tx.cents);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // called after threads have finished
    Map<AggKey, Long> snapshotTotals() {
        return new HashMap<>(totals);
    }
}

//COLLABORATOR - GPT5






import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

public class ErrorWriter implements Runnable {

    private final BlockingQueue<Msg> in;
    private final Path outPath;
    private final int expectedStops;

    public ErrorWriter(BlockingQueue<Msg> in, Path outPath, int expectedStops) {
        this.in = in;
        this.outPath = outPath;
        this.expectedStops = expectedStops;
    }

    @Override
    public void run() {
        int stopCount = 0;

        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(outPath))) {
            // simple CSV header
            pw.println("id,account,epochSec,cents,error");

            while (true) {
                Msg msg = in.take();

                if (msg instanceof Stop) {
                    stopCount++;
                    if (stopCount >= expectedStops) {
                        break;      // all validators have signalled completion
                    }
                    continue;
                }

                if (!(msg instanceof MsgTx)) {
                    continue;
                }

                MsgTx mt = (MsgTx) msg;
                Optional<String> err = Validator.validate(mt.tx);

                if (err.isPresent()) {
                    Tx t = mt.tx;
                    pw.println(t.id + "," + t.account + ","
                            + t.epochSec + "," + t.cents + ","
                            + err.get());
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

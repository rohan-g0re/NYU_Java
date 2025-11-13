//COLLABORATOR - GPT5






import java.util.Optional;
import java.util.concurrent.BlockingQueue;

public class Validator implements Runnable {

    private final BlockingQueue<Msg> in;
    private final BlockingQueue<Msg> toAgg;
    private final BlockingQueue<Msg> toErr;

    public Validator(BlockingQueue<Msg> in,
                     BlockingQueue<Msg> toAgg,
                     BlockingQueue<Msg> toErr) {
        this.in = in;
        this.toAgg = toAgg;
        this.toErr = toErr;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Msg msg = in.take();

                if (msg instanceof Stop) {
                    // propagate stop downstream and exit
                    toAgg.put(msg);
                    toErr.put(msg);
                    break;
                }

                if (!(msg instanceof MsgTx)) {
                    continue;
                }

                MsgTx mt = (MsgTx) msg;

                if (validate(mt.tx).isPresent()) {
                    // invalid -> send to error writer
                    toErr.put(mt);
                } else {
                    // valid -> send to aggregator
                    toAgg.put(mt);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    
   /*
    * I have 4 types of error --> added bad_time bcoz we dind not have anythingfor epochset column 
    * */
    
    static Optional<String> validate(Tx t) {
        if (t.id == null || t.id.isEmpty()) return Optional.of("missing_id");
        if (t.account == null || t.account.isEmpty()) return Optional.of("missing_account");
        if (t.epochSec < 946684800L || t.epochSec >= 4102444800L)
            return Optional.of("bad_time"); // 2000..2100
        if (Math.abs(t.cents) > 10_000_00L) return Optional.of("amount_out_of_range");
        return Optional.empty();
    }
}

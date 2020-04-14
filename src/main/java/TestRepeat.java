import bwapi.*;

public class TestRepeat extends Routine {
    private final ChalkBoard info;
    private Sequencer sequencer;
    private Repeat repeater;
    private Selector selector;

    public void reset() {
        this.sequencer = null;
        this.repeater = null;
        this.selector = null;
        this.start(info);
    }

    public TestRepeat(ChalkBoard info){
        this.info = info;
    }

    public void start(ChalkBoard info){
        if(selector == null)
            selector = new Selector();

        selector.addRoutine(new BuildExpansion(info));
        selector.start();
    }

    public void act(ChalkBoard info) {
        if(selector == null){
            this.start(info);
            return;
        }
        else{
            selector.act(info);
        }

        if(selector.state == RoutineState.Success){
            succeed();
            System.out.println("TEST: selector finished");
        }
        else if(selector.state == RoutineState.Failure){
            System.out.println("TEST: Failure");
            reset();
        }
    }
}

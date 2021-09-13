package cs735_835.noc;

public class MyWire implements Wire {
    Port inbound, outbound;

    public MyWire( Port in, Port out ){
        inbound = in;
        outbound = out;
    }
    @Override
    public boolean transfer() {
        if( inbound.notFree() && outbound.isFree() ){
            outbound.setMessage( inbound.getMessage() );
            inbound.freePort();
            return true;
        }
        return false;
    }
}

package cs735_835.noc;

public class MyRouter implements Router {
    boolean active;
    Port north, east, south, west;
    int row, col;
    Network network;
    MyCore core;
    public MyRouter( int r, int c, Network net ){
        row = r;
        col = c;
        network = net;
        north = new Port( this );
        south = new Port( this );
        east = new Port( this );
        west = new Port( this );
    }

    @Override
    public boolean isActive() {
        return network.getCore( row, col ).isActive() || north.notFree() || south.notFree() || east.notFree() || west.notFree();
    }

    public MyCore getCore(){
        if( core == null )
            core = (MyCore) network.getCore( row, col );
        return core;
    }

    @Override
    public void route() {
        //System.out.println("Route called on " +row +" "+col );


        // If the North port contains a message intended for the core, it is delivered.
        if( north.notFree() && north.getMessage().destRow() == row && north.getMessage().destCol() == col ){
            if( north.getMessage().isTracked() )
                System.out.println( "at " + network.clock.getTime() + ", msg "+ north.getMessage().getId() + " is delivered to ("+ row + ", "+col+")" );
            getCore().receiveMessage( north.getMessage() );
            north.freePort();
        }
        // If the West port contains a message intended for the core, it is delivered.
        if( west.notFree() && west.getMessage().destRow() == row && west.getMessage().destCol() == col ){
            if( west.getMessage().isTracked() )
                System.out.println( "at " + network.clock.getTime() + ", msg "+ west.getMessage().getId() + " is delivered to ("+ row + ", "+col+")" );
            getCore().receiveMessage( west.getMessage() );
            west.freePort();
        }
        // If the North port contains a message and the South port is free, the message is moved from North to South.
        if( north.notFree() && south.isFree() ){
            if( north.getMessage().isTracked() )
                System.out.println( "at "+network.clock.getTime()+", ("+row+", "+col+") moves msg "+north.getMessage().getId()+" from North to South" );
            south.setMessage( north.getMessage() );
            north.freePort();
        }
        //If the West port contains a message and the East port is free and the message is intended for a core in a different column, the message is moved from West to East.
        if( west.notFree() && east.isFree() && west.getMessage().destCol() !=col ){
            if( west.getMessage().isTracked() )
                System.out.println( "at "+network.clock.getTime()+", ("+row+", "+col+") moves msg "+west.getMessage().getId()+" from West to East" );
            east.setMessage( west.getMessage() );
            west.freePort();
        }
        //If the West port contains a message and the South port is free and the message is intended for a core in the same column, the message is moved from West to South.
        if( west.notFree() && south.isFree() && west.getMessage().destCol() == col ){
            if( west.getMessage().isTracked() )
                System.out.println( "at "+network.clock.getTime()+", ("+row+", "+col+") moves msg "+west.getMessage().getId()+" from West to South" );
            south.setMessage( west.getMessage() );
            west.freePort();
        }

        if( getCore().messageQueue.isEmpty() )
            return;

        Message m = core.messageQueue.peek();
        if( m.isTracked() )
            System.out.println( "at "+network.clock.getTime()+", new msg "+m.getId()+" generated by ("+row+", "+col+") intended for ("+m.destRow()+", "+m.destCol()+")" );

        if( m.destRow() == row && m.destCol() == col )
            core.receiveMessage( core.messageQueue.pop() );
        else if( m.destCol() == col && south.isFree() )
            south.setMessage( core.messageQueue.pop() );
        else if( m.destCol() != col && east.isFree() )
            east.setMessage( core.messageQueue.pop() );

    }
}

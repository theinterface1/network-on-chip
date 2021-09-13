package cs735_835.noc;

public class Port {
    MyRouter router;
    Message message;
    public Port( MyRouter parent){
        router = parent;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void freePort(){
        message = null;
    }

    public boolean isFree(){
        return message == null;
    }
    public boolean notFree(){
        return message != null;
    }
}

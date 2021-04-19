package hk.edu.cuhk.ie.iems5722.a2_1155069587;

/**
 * Created by WD-MAC on 16/2/2.
 */
public class ChatMessage {
    private String message;
    private String fromName;
    private boolean isSelf;
    private String timeStamp;
    private int userId;

    public ChatMessage(String fromName, int userId, boolean isSelf, String message, String timeStamp){
        this.fromName = fromName;
        this.message = message;
        this.timeStamp = timeStamp;
        this.isSelf = isSelf;
        this.userId = userId;
    }
    public String getMessage(){
        return this.message;
    }
    public String getFromName() { return this.fromName; }
    public boolean isSelf() {
        return this.isSelf;
    }
    public String getTimeStamp(){
        return this.timeStamp;
    }
    public int getUserId(){
        return this.userId;
    }

}

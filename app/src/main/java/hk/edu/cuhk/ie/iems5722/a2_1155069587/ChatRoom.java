package hk.edu.cuhk.ie.iems5722.a2_1155069587;

/**
 * Created by WD-MAC on 16/2/18.
 */
public class ChatRoom {
    private String name;
    private int id;
    public ChatRoom(String name, int id){
        this.name = name;
        this.id = id;
    }
    public String getName(){return this.name;}
    public int getId(){return this.id;}
}

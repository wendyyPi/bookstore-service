package com.group.bookstore.backend;

/**
 * @author Yusen Nian
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class User {
    private String name;
    private ArrayList<Message> messages;
    private ArrayList<String> blackList;

    public User(String name) {
        this.name = name;
        this.messages = new ArrayList<>();
        this.blackList = new ArrayList<>();
    }

    public void blockUser(User user){
        if(user == null){
            return;
        }
        if(this.blackList.contains(user.getName())){
            return;
        }
        ArrayList<String> newBlackList = this.getBlackList();
        newBlackList.add(user.getName());
        this.setBlackList(newBlackList);
        UserManager.upLoad();
    }

    public void unBlockUser(User user){
        if(user == null)
            return;
        if(!this.blackList.contains(user.getName()))
            return;
        ArrayList<String> newBlackList = this.getBlackList();
        newBlackList.remove(user.getName());
        this.setBlackList(newBlackList);
        UserManager.upLoad();
    }

    public int unreadNum(){
        int num = 0;
        for(Message message : messages){
            if(message.getStatus() == Message.Status.UNREAD){
                num++;
            }
        }
        return num;
    }

    public boolean sendMessage(String receiver, String message) {
        UserManager manager = UserManager.getInstance();
        User userReceiver = manager.get(receiver);
        if (userReceiver == null)
            return false;
        MessageFactory messageFactory = new MessageFactory();
        Message msg = messageFactory.create(userReceiver, this, message);
        if (msg == null)
            return false;
        userReceiver.getMessages().add(msg);
        UserManager.upLoad();
        return true;
    }

    public void deleteMessage(int i ){
        messages.remove(i);
        UserManager.upLoad();
    }


    public String getName() {
        return name;
    }


    public ArrayList<Message> getMessages() {
        // 使用 Comparator 来按照时间降序
        Collections.sort(messages, new Comparator<Message>() {
            @Override
            public int compare(Message m1, Message m2) {
                return m2.getDate().compareTo(m1.getDate());
            }
        });
        return messages;
    }


    public ArrayList<String> getBlackList() {
        return blackList;
    }

    public void setBlackList(ArrayList<String> blackList) {
        this.blackList = blackList;
    }
}

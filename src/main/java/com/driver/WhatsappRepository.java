package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;

    //private HashSet<String> userMobile;

    private HashMap<String,User> allUsers;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.allUsers = new HashMap<>();
      //  this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public boolean userExists(String phoneNumber){
        return allUsers.containsKey(phoneNumber);
    }

    public void addUser(String name, String phoneNumber){
        allUsers.put(phoneNumber,new User(name,phoneNumber));
    }

    public void groupIncrement(){
        this.customGroupCount++;
    }

    public int getGroupNumber(){
        return customGroupCount;
    }

    public void mapGroupWithAdmin(Group group, User user){
        adminMap.put(group,user);
    }

    public void mapGroupWithUsers(Group group, List<User> allUsers){
        groupUserMap.put(group,allUsers);
    }

    public void messageIncrement() {
        messageId++;
    }

    public int getMessageCount() {
        return messageId;
    }

    public boolean groupExists(Group group) {
        return groupUserMap.containsKey(group);
    }

    public boolean isSenderGroupMember(User sender, Group group) {
        List<User> allMembers = groupUserMap.get(group);

        for(User user : allMembers){
            if(user.equals(sender)) return true;
        }

        return false;
    }

    public void mapMessageWithSender(Message message, User sender) {
        senderMap.put(message,sender);
    }

    public void mapMessageWithGroup(Message message, Group group) {
        List<Message> allMessages = groupMessageMap.getOrDefault(group, new ArrayList<>());
        allMessages.add(message);
        groupMessageMap.put(group,allMessages);
    }

    public int numberOfMessagesInGroup(Group group) {
        return groupMessageMap.get(group).size();
    }

    public boolean isApproverAdmin(User approver, Group group) {
        return adminMap.get(group).equals(approver);
    }

    public void changeAdmin(User user, Group group) {
        adminMap.put(group,user);
    }
}

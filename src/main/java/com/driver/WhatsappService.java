package com.driver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WhatsappService {

    Logger logger = LoggerFactory.getLogger(WhatsappService.class);
    WhatsappRepository whatsappRepository;
    public WhatsappService(){
        whatsappRepository = new WhatsappRepository();
    }
    public String createUser(String name, String phoneNumber) throws Exception {
        logger.info("Add User : In Service");
        boolean userExists = whatsappRepository.userExists(phoneNumber);
        if(userExists) throw new Exception("User already exists");
        // if the user doesn't already exist
        whatsappRepository.addUser(name,phoneNumber);
        logger.info("Added User in DataBase");
        return "SUCCESS";
    }

    public Group createGroup(List<User> users){
        logger.info("Create Group : In Service");
        //assuming that these are new users which need to be added
        for(User user : users) {
            if (!whatsappRepository.userExists(user.getMobile())) {
                whatsappRepository.addUser(user.getName(), user.getMobile());
                logger.info("Added NEW USER Which weren't added before, Adding them while creating GROUP");
            }
        }


        int sizeOfGroup = users.size();

        //if size is greater than 2, it means it is a group
        if(sizeOfGroup > 2) {

            logger.info("Group contains more than 2 members");
            logger.info("Increased Group Count");
            whatsappRepository.groupIncrement();
            String groupName = "Group "+whatsappRepository.getGroupNumber();

            //admin of the group is the first user
            User groupAdmin = users.get(0);

            //mapping group with admin
            Group group = new Group(groupName,sizeOfGroup);
            whatsappRepository.mapGroupWithAdmin(group,groupAdmin);

            //mapping group with all users
            whatsappRepository.mapGroupWithUsers(group,users);

            logger.info("Added Group in Database");

            return group;
        }

        // it means size is 2 . if size is 1, it means that is an invalid request
        String groupName = users.get(1).getName();
        User groupAdmin1 = users.get(0);


        Group group2 = new  Group(groupName,2);

        whatsappRepository.mapGroupWithAdmin(group2,groupAdmin1);

        whatsappRepository.mapGroupWithUsers(group2,users);

        logger.info("Added Personal Chat in DATABASE");

        return group2; // with two members : personal chat

    }

    public int createMessage(String message){
        whatsappRepository.messageIncrement();
        logger.info("Message incremented and returned");
        return whatsappRepository.getMessageCount();

    }


    public int sendMessage(Message message, User sender, Group group) throws Exception {
        logger.info("Send MEssage : In Service");

        boolean groupExists = whatsappRepository.groupExists(group);
        if(!groupExists) throw new Exception("Group does not exist");

//        boolean userExists = whatsappRepository.userExists(sender.getMobile());
//        if(!userExists) throw new Exception("Sender does not exist");

        boolean isSenderGroupMember = whatsappRepository.isSenderGroupMember(sender,group);
        if(!isSenderGroupMember) throw new Exception("You are not allowed to send message");

        logger.info("Group Exists, User Exists, Sender part of the group");

        //mapping sender with message
        whatsappRepository.mapMessageWithSender(message,sender);

        //mapping group with message
        whatsappRepository.mapMessageWithGroup(message,group);

        logger.info("Mapped message with sender and mapped group with messages");

        //number of messages in that group
        return whatsappRepository.numberOfMessagesInGroup(group);
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception{
        logger.info("change Adming : In Service");

        boolean groupExists = whatsappRepository.groupExists(group);
        if(!groupExists) throw new Exception("Group does not exist");

        boolean isApproverAdmin = whatsappRepository.isApproverAdmin(approver,group);
        if(!isApproverAdmin) throw new Exception("Approver does not have rights");

        boolean isUserGroupMember = whatsappRepository.isSenderGroupMember(user,group);
        if(!isUserGroupMember) throw new Exception("User is not a participant");

        logger.info("Group Exists, Approver is Admin  and User is a member of Group");

        logger.info("Changed Admin");
        //changing the admin of the group
        whatsappRepository.changeAdmin(user,group);

        return "SUCCESS";
    }
}

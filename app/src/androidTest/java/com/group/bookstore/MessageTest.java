package com.group.bookstore;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

import com.group.bookstore.backend.Message;
import com.group.bookstore.backend.User;
import com.group.bookstore.backend.UserManager;
import com.group.bookstore.database.DataBase;

import java.util.HashMap;

/**
 * This test is to test Firebase and sending message
 * @author Fanghzou Liu
 */
@RunWith(AndroidJUnit4.class)
public class MessageTest {
    @Parameterized.Parameter
    UserManager userManager;

    @Before
    public void init() throws InterruptedException {
        UserManager.init();
        Thread.sleep(5000); // sleep 5s to sync data
        userManager = UserManager.getInstance();
        UserManager.setCurrentUser(userManager.getUsers().get(0));
    }

    @Test
    public void NotNullTest() {
        assertNotNull(userManager.getCurrentUser());
    }

    @Test
    public void SendDeleteTest() {
        User user0 = userManager.getCurrentUser();
        User user1 = userManager.getUsers().get(1);
        String content = "Test message generated in JUnit test";
        user0.sendMessage(user1.getName(), content);
        Message message = user1.getMessages().get(0);
        assertEquals(content, message.getMessageContent());
        assertEquals(user0.getName(), message.getAddresser());
        assertEquals(Message.Status.UNREAD, message.getStatus());
        assertEquals(user1.getName(), message.getReceiver());
        // open message
        message.openMessage();
        assertEquals(Message.Status.READ, message.getStatus());
        // delete message
        user1.deleteMessage(0);
        assertFalse(user1.getMessages().contains(message));
    }

    @Test
    public void BlackListTest(){
        User user0 = userManager.getCurrentUser();
        User user1 = userManager.getUsers().get(1);
        // block user
        user1.blockUser(user0);
        assertTrue(user1.getBlackList().contains(user0.getName()));
        // send message
        assertFalse(user0.sendMessage(user1.getName(), "Test message should be not sent to this user"));
        // unblock user
        user1.unBlockUser(user0);
        assertFalse(user1.getBlackList().contains(user0.getName()));
    }

    @Test
    public void SendToUserDoesNotExist(){
        User user0 = userManager.getCurrentUser();
        User user1 = new User("USERDOESNOTEXIST");
        assertFalse(user0.sendMessage(user1.getName(),"Test message should not be sent"));
    }
}

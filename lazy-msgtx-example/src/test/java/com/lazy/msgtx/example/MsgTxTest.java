package com.lazy.msgtx.example;

import com.alibaba.fastjson.JSON;
import com.lazy.msgtx.core.MessageLog;
import com.lazy.msgtx.core.endpoint.PageRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * <p>
 *
 * </p>
 *
 * @author lzy
 * @since 2022/6/4.
 */
public class MsgTxTest extends BaseTest {



    @Test
    public void pageTest() throws Exception {

        MessageLog messageLog = new MessageLog();
//        messageLog.setMessageId("1532924655252410368");
//        messageLog.setMessageBody("");
        messageLog.setMessageType("CREATE_ORDER");
//        messageLog.setBizId("011001");
//        messageLog.setProcessStatus("1");
//        messageLog.setPid(-1L);

        PageRequest request = new PageRequest();
        request.setPage(1);
        request.setSize(10);
        request.setMessageLog(messageLog);
        System.out.println(JSON.toJSONString(request));
        this.mockMvc.perform(post("/msgtx/page")
                .content(JSON.toJSONString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();


    }

    @Test
    public void retryTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/msgtx/retry/1532924655252410368")).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

}

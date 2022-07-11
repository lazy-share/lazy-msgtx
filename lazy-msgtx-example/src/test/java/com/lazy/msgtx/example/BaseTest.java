package com.lazy.msgtx.example;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * <p>
 *
 * </p>
 *
 * @author lzy
 * @since 2022/6/4.
 */
@SpringBootTest
public class BaseTest {


    MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext wac) {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultRequest(get("/").accept(MediaType.APPLICATION_JSON)) // 默认请求路径
                .build();


    }


}

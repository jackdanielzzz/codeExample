package com.springbackend.advmanager.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BannerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BannerController bannerController;

    @Test
    public void shouldPassWhenNotNull() throws Exception {
        Assertions.assertThat(bannerController).isNotNull();
    }

    @Test
    void shouldPassWhenAllBannersRequesting() throws Exception {
        this.mockMvc.perform(get("http://localhost:8080/api/banners"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteBanner() throws Exception {
        this.mockMvc.perform(post("http://localhost:8080/api/banners/delete/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("banner was deleted")));
    }
}
package com.daily.cost.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Tag(name = "Spring ai")
@RestController
@RequestMapping("/ai")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.defaultSystem("你是一个专业的Java技术顾问，回答简洁清晰").build();
    }

    /**
     * 普通对话
     */
    @GetMapping("/chat")
    public String chat(@RequestParam String q) {
        return chatClient.prompt()
                .user(q)
                .call()
                .content();
    }

    /**
     * 流的形式对话
     */
    @GetMapping(value = "/stream",
            produces = "text/event-stream;charset=UTF-8")
    public Flux<String> stream(
            @RequestParam String q,
            HttpServletResponse response) {
        // 强制设置响应头
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/event-stream;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        return chatClient.prompt()
                .user(q)
                .stream()
                .content();
    }

    /**
     * 结构化输出，自动解析成Java对象
     */
    @GetMapping("/parse")
    public Person parse(@RequestParam String desc) {
        return chatClient.prompt()
                .user("从以下描述中提取人物信息：" + desc)
                .call()
                .entity(Person.class);
    }
    record Person(String name, Integer age, String job) {}

}

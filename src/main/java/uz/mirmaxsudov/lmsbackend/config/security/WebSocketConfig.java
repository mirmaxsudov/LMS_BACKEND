package uz.mirmaxsudov.lmsbackend.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import uz.mirmaxsudov.lmsbackend.security.service.JwtService;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtService jwtService;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app"); // client→server prefix
    }
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new ChannelInterceptor() {
//            @Override
//            public Message<?> preSend(Message<?> message, MessageChannel channel) {
//                System.out.println("In config");
//                StompHeaderAccessor accessor = MessageHeaderAccessor
//                        .getAccessor(message, StompHeaderAccessor.class);
//
//                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//                    // 1) extract your JWT from a native header
//                    String token = accessor.getFirstNativeHeader("token");
//                    if (token.isBlank() || jwtService.isTokenExpired(token)) {
//                        throw new IllegalArgument Exception("Invalid or missing JWT");
//                    }
//                    // 2) set the Principal so @MessageMapping methods see who’s sending
//                    String username = jwtService.extractUsername(token);
//                    Principal user = new UsernamePasswordAuthenticationToken(username, null, List.of());
//                    accessor.setUser(user);
//                }
//                return message;
//            }
//        });
//    }
}
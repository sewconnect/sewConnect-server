package stephenowinoh.spring.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import stephenowinoh.spring.security.MyUserDetailsService;
import stephenowinoh.spring.security.token.JwtService;

@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocketSecurity
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${websocket.allowed-origins}")
    private String allowedOrigins;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(
            MessageMatcherDelegatingAuthorizationManager.Builder messages) {

        messages
                .nullDestMatcher().permitAll()  // Back to permitAll
                .simpTypeMatchers(SimpMessageType.CONNECT).permitAll()
                .simpTypeMatchers(
                        SimpMessageType.DISCONNECT,
                        SimpMessageType.HEARTBEAT,
                        SimpMessageType.UNSUBSCRIBE
                ).permitAll()  // Change these to permitAll too
                .simpSubscribeDestMatchers("/user/queue/**", "/topic/**").authenticated()
                .simpDestMatchers("/app/**").authenticated()
                .anyMessage().authenticated();

        return messages.build();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost("localhost")
                .setRelayPort(61613)
                .setClientLogin("guest")
                .setClientPasscode("guest");

        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins(allowedOrigins.split(","))
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                System.out.println(">>> Interceptor called for message type: " + message.getHeaders().get("simpMessageType"));

                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                System.out.println(">>> Accessor: " + accessor);
                System.out.println(">>> Command: " + (accessor != null ? accessor.getCommand() : "null"));

                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    System.out.println("=== WEBSOCKET CONNECT ATTEMPT ===");


                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    System.out.println("Auth header present: " + (authHeader != null));

                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        try {
                            String token = authHeader.substring(7);
                            System.out.println("Token extracted, length: " + token.length());

                            String username = jwtService.extractUsername(token);
                            System.out.println("Username from token: " + username);

                            if (username != null && jwtService.isTokenValid(token)) {
                                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                                UsernamePasswordAuthenticationToken authentication =
                                        new UsernamePasswordAuthenticationToken(
                                                userDetails,
                                                null,
                                                userDetails.getAuthorities()
                                        );
                                accessor.setUser(authentication);
                                System.out.println("WebSocket AUTH SUCCESS for: " + username);
                            } else {
                                System.err.println("Token validation FAILED");
                                return null;
                            }
                        } catch (Exception e) {
                            System.err.println("WebSocket auth exception: " + e.getMessage());
                            e.printStackTrace();
                            return null;
                        }
                    } else {
                        System.err.println("No valid Authorization header");
                        return null;
                    }
                }

                return message;
            }
        });
    }

}


package gdimitriu.kafka_app.security;

//@Configuration
//@EnableWebSecurity
public class LoginSecurityConfig {
/*
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests( authz -> authz
                .requestMatchers(HttpMethod.GET, "/swagger-[\\s\\S]*").permitAll()
                .requestMatchers(HttpMethod.GET,"/v3/api-docs[\\s\\S]*").permitAll()
                .anyRequest().authenticated()).csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder().username("user").password("{noop}user")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }
 */
}

package dev.example.config.security;

import dev.example.config.security.handler.OAuth2AuthenticationFailureHandler;
import dev.example.config.security.handler.OAuth2AuthenticationSuccessHandler;
import dev.example.config.security.service.OAuth2UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.reactive.function.client.WebClient;

import javax.sql.DataSource;


@EnableWebSecurity
@EnableGlobalMethodSecurity(// теперь доступы прописываются над методами
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true
)
@AllArgsConstructor

public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /////////////////////////////
    // BY JWT TOKEN
//    private final JwtConfigurer jwtConfigurer;
//    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Qualifier("userDetailsServiceImpl")
    private final UserDetailsService userDetailsService;
    private final DataSource dataSource;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final OAuth2UserServiceImpl oAuth2UserServiceImpl;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        SimpleUrlAuthenticationFailureHandler handler = new SimpleUrlAuthenticationFailureHandler("/");

        http
                //не заходит
//                .exceptionHandling()
//                .authenticationEntryPoint(restAuthenticationEntryPoint)
//                .exceptionHandling(e -> e
//                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
//                )
//                .and()

                // use JWT (without formLogin!)
                // на фронте токен хранить в local storage)
                // при каждом ответе с фронта добавляем в хедер токен
//                .csrf().disable()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
//                .addFilter(new JWTAuthorizationFilter(authenticationManager()))
//                .apply(jwtConfigurer)
                // api
//                .and()
//                .authorizeRequests()
//                .antMatchers("/api/v1/auth/login").permitAll()
//                .antMatchers("/api/v1/users/*").authenticated()

                //mvc + session + cookie

                .cors()
                .and()
                .authorizeRequests()
                .antMatchers(
                        "/favicon.ico",
                        "/css/**",
                        "/js/**",
                        "/images/**",

                        "/",
                        "/error",
                        "/index",
                        "/auth/register",
                        "/auth/verify/*",
                        "/auth/resent-token/*",
                        "/auth/login",
                        "/oauth2/**"
                ).permitAll()
                .antMatchers(HttpMethod.POST, "/auth/process").permitAll()
                .antMatchers(HttpMethod.POST, "/auth/register").permitAll()
                .anyRequest().authenticated()

                .and()
                .formLogin()
                .usernameParameter("email") //!!!
                .loginPage("/auth/login").permitAll()
//                .loginProcessingUrl("/auth/process").permitAll()//не заходит - это просто чтобы скрыть от пользователей системный урл обработки, сами переопределить метод не можем?
                .defaultSuccessUrl("/auth/success")

                .and()
                .oauth2Login(o -> o
                                .loginPage("/auth/login")
                                .userInfoEndpoint()
                                .userService(oAuth2UserServiceImpl)
                                .and()
                                .successHandler(oAuth2AuthenticationSuccessHandler)
//                                .failureHandler((request, response, exception) -> {
//                                    request.getSession().setAttribute("error.message", exception.getMessage());
//                                    handler.onAuthenticationFailure(request, response, exception);
//                                })
                                .failureHandler(oAuth2AuthenticationFailureHandler)
                )

//                .and()
                .rememberMe()
                .key("authsecret")
                .tokenValiditySeconds(60) //3*24*60*60
                .userDetailsService(userDetailsService) //!!!
                .tokenRepository(persistentTokenRepository()) //!!!

                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout", HttpMethod.POST.name()))
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .deleteCookies("remember-me")
                .logoutSuccessUrl("/auth/login");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
//        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    protected DaoAuthenticationProvider daoAuthenticationProvider() {
        final DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean //!!!!!
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        final JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }

    //////
    @Bean
    public WebClient rest(
            ClientRegistrationRepository clients,
            OAuth2AuthorizedClientRepository authz
    ) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(clients, authz);

        return WebClient
                .builder()
                .filter(oauth2)
                .build();
    }

//    @Bean
//    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService(WebClient rest) {
//
//        return request -> {
//            OAuth2User user = oAuth2UserServiceImpl.loadUser(request);
//
//            if (!"google".equals(request.getClientRegistration().getRegistrationId())) {
//                return user;
//            }
//
//            OAuth2AuthorizedClient client = new OAuth2AuthorizedClient(
//                    request.getClientRegistration(),
//                    user.getName(),
//                    request.getAccessToken()
//            );
//            String url = user.getAttribute("organizations_url");
//
//            List<Map<String, Object>> orgs = rest
//                    .get()
//                    .uri(url)
//                    .attributes(oauth2AuthorizedClient(client))
//                    .retrieve()
//                    .bodyToMono(List.class)
//                    .block();
//
//            if (orgs != null &&
//                    orgs.stream().anyMatch(org -> "spring-projects".equals(org.get("login")))) {
//                return user;
//            }
//
//            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_token", "Not in Spring Team", ""));
//        };
//    }

    //    /////////////////////////////
//    // BY DATABASE
//    private final UserDetailsService userDetailsService;
//
//    @Autowired
//    public SecurityConfig(@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
//        this.userDetailsService = userDetailsService;
//    }
//
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .csrf().disable()
//                .authorizeRequests()
//                .antMatchers("/").permitAll()
//                .anyRequest().authenticated()
//
//                .and()
//                .formLogin()
//                .loginPage("/auth/login").permitAll()
//                .defaultSuccessUrl("/auth/success")
//
//                .and()
//                .logout()
//                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout", "POST"))
//                .invalidateHttpSession(true)
//                .clearAuthentication(true)
//                .deleteCookies("JSESSIONID")
//                .logoutSuccessUrl("/auth/login");
//    }
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.authenticationProvider(daoAuthenticationProvider());
//    }
//
//    @Bean
//    protected DaoAuthenticationProvider daoAuthenticationProvider() {
//        final DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//
//        provider.setPasswordEncoder(passwordEncoder());
//        provider.setUserDetailsService(userDetailsService);
//
//        return provider;
//    }
//
//    @Bean
//    protected PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder(12);
//    }

//    /////////////////////////////
//    // BY formLogin
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .csrf().disable()
//                .authorizeRequests()
//                .antMatchers("/").permitAll()
//                .anyRequest().authenticated()
//
//                .and()
//                .formLogin()
//                .loginPage("/auth/login").permitAll()
//                .defaultSuccessUrl("/auth/success")
//
//                .and()
//                .logout()
//                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout", "POST"))
//                .invalidateHttpSession(true)
//                .clearAuthentication(true)
//                .deleteCookies("JSESSIONID")
//                .logoutSuccessUrl("/auth/login");
//    }
//
//    @Bean
//    protected UserDetailsService userDetailsService() {
//
//        final UserDetails admin = User.builder()
//                .username("admin")
//                .password(passwordEncoder().encode("admin"))
//                .authorities(RoleEnum.ADMIN.getAuthorities())//SimpleGrantedAuthorities by "reader" and "writer"
//                .build();
//
//        final UserDetails user = User.builder()
//                .username("user")
//                .password(passwordEncoder().encode("user"))
//                .authorities(RoleEnum.USER.getAuthorities())//SimpleGrantedAuthorities by "reader"
//                .build();
//
//        return new InMemoryUserDetailsManager(
//                admin,
//                user
//        );
//    }
//
//    @Bean
//    protected PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder(12);
//    }

//    /////////////////////////////
//    // BY PERMISSIONS
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .csrf().disable()
//                .authorizeRequests()
//                    .antMatchers("/").permitAll()
//                // если над методом анн @PreAuthorize("hasAuthority('writer')") - то тут уже не указываем
////                    .antMatchers(HttpMethod.GET, "/api/**").hasAuthority(PermissionEnum.READER.getPermission())//"reader"
////                    .antMatchers(HttpMethod.POST, "/api/**").hasAuthority(PermissionEnum.WRITER.getPermission())//"writer
////                    .antMatchers(HttpMethod.DELETE, "/api/**").hasAuthority(PermissionEnum.WRITER.getPermission())//"writer
//                .anyRequest().authenticated()
//                    .and()
//                .httpBasic();
//    }
//
//    @Bean
//    protected UserDetailsService userDetailsService(){
//        return new InMemoryUserDetailsManager(
//                User.builder()
//                    .username("admin")
//                    .password(passwordEncoder().encode("admin"))
//                    .authorities(RoleEnum.ADMIN.getAuthorities())//SimpleGrantedAuthorities by "reader" and "writer"
//                    .build(),
//
//                User.builder()
//                    .username("user")
//                    .password(passwordEncoder().encode("user"))
//                    .authorities(RoleEnum.USER.getAuthorities())//SimpleGrantedAuthorities by "reader"
//                    .build()
//        );
//    }
//
//    @Bean
//    protected PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder(12);
//    }

//    ///////////////////////////
//     IN MEMORY / BY ROLES
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .csrf().disable()
//                .authorizeRequests()
//                    .antMatchers("/").permitAll()
//                    .antMatchers(HttpMethod.GET, "/api/**").hasAnyRole(RoleEnum.ADMIN.name(), RoleEnum.USER.name())
//                    .antMatchers(HttpMethod.POST, "/api/**").hasRole(RoleEnum.ADMIN.name())
//                    .antMatchers(HttpMethod.DELETE, "/api/**").hasRole(RoleEnum.ADMIN.name())
//                .anyRequest().authenticated()
//                    .and()
//                .httpBasic();
//    }
//
//    @Bean
//    protected UserDetailsService userDetailsService(){
//        return new InMemoryUserDetailsManager(
//                User.builder()
//                    .username("admin")
//                    .password(passwordEncoder().encode("admin"))
//                    .roles(RoleEnum.ADMIN.name())
//                    .build(),
//                User.builder()
//                    .username("user")
//                    .password(passwordEncoder().encode("user"))
//                    .roles(RoleEnum.USER.name())
//                    .build()
//        );
//    }
//
//    @Bean
//    protected PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder(12);
//    }
    /////////////////////////////
}

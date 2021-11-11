package dev.example.test7.security;

import dev.example.test7.security.jwt.JwtConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)// теперь доступы прописываются над методами

public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /////////////////////////////
    // BY JWT TOKEN
    private final JwtConfigurer jwtConfigurer;
    private final UserDetailsService userDetailsService;
//    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    public SecurityConfig(
            JwtConfigurer jwtConfigurer,
            @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService//,
//            @Qualifier("restAuthenticationEntryPoint") RestAuthenticationEntryPoint restAuthenticationEntryPoint
    ) {
        this.jwtConfigurer = jwtConfigurer;
        this.userDetailsService = userDetailsService;
//        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//                .exceptionHandling()
//                .authenticationEntryPoint(restAuthenticationEntryPoint) //не заходит
//                .and()

                .csrf().disable()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

//                .and()
                .authorizeRequests()

                //api
//                .antMatchers("/api/v1/auth/login").permitAll()
//                .antMatchers("/api/v1/users/*").authenticated()

                //mvc
                .antMatchers("/", "/auth/login").permitAll()
                .anyRequest().authenticated()

                .and()
                .formLogin()
                .loginPage("/auth/login").permitAll()
                .defaultSuccessUrl("/auth/success")

                .and()
                .rememberMe()
                .key("authsecret")
                .tokenValiditySeconds(60)

                .and()
                .anonymous()
                .authorities("ROLE_ANONYMOUS")
                .principal("anonymous")

                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout", "POST"))
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies()
//                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/auth/login")

                .and()
                .apply(jwtConfigurer);
    }

    /// по идеи уже не нужно ////
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    protected DaoAuthenticationProvider daoAuthenticationProvider() {
        final DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);

        return provider;
    }
    //////

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }


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
//                .authorities(Role.ADMIN.getAuthorities())//SimpleGrantedAuthorities by "reader" and "writer"
//                .build();
//
//        final UserDetails user = User.builder()
//                .username("user")
//                .password(passwordEncoder().encode("user"))
//                .authorities(Role.USER.getAuthorities())//SimpleGrantedAuthorities by "reader"
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
////                    .antMatchers(HttpMethod.GET, "/api/**").hasAuthority(Permission.READER.getPermission())//"reader"
////                    .antMatchers(HttpMethod.POST, "/api/**").hasAuthority(Permission.WRITER.getPermission())//"writer
////                    .antMatchers(HttpMethod.DELETE, "/api/**").hasAuthority(Permission.WRITER.getPermission())//"writer
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
//                    .authorities(Role.ADMIN.getAuthorities())//SimpleGrantedAuthorities by "reader" and "writer"
//                    .build(),
//
//                User.builder()
//                    .username("user")
//                    .password(passwordEncoder().encode("user"))
//                    .authorities(Role.USER.getAuthorities())//SimpleGrantedAuthorities by "reader"
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
//                    .antMatchers(HttpMethod.GET, "/api/**").hasAnyRole(Role.ADMIN.name(), Role.USER.name())
//                    .antMatchers(HttpMethod.POST, "/api/**").hasRole(Role.ADMIN.name())
//                    .antMatchers(HttpMethod.DELETE, "/api/**").hasRole(Role.ADMIN.name())
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
//                    .roles(Role.ADMIN.name())
//                    .build(),
//                User.builder()
//                    .username("user")
//                    .password(passwordEncoder().encode("user"))
//                    .roles(Role.USER.name())
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

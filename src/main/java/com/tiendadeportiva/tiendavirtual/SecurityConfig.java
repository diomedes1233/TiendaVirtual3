package com.tiendadeportiva.tiendavirtual;

import com.tiendadeportiva.tiendavirtual.handler.CustomSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;
@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private final DataSource dataSource;

    final
    CustomSuccessHandler customSuccessHandler;

    public SecurityConfig(DataSource dataSource, CustomSuccessHandler customSuccessHandler) {
        this.dataSource = dataSource;
        this.customSuccessHandler = customSuccessHandler;
    }

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception{
        auth.jdbcAuthentication().passwordEncoder(new BCryptPasswordEncoder())
                .dataSource(dataSource)
                .usersByUsernameQuery("select correo,password,estado from empleado where correo=?")
                .authoritiesByUsernameQuery("select correo, rol from empleado where correo=?");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                //Empresas
                .antMatchers("/").hasRole("ADMIN")
                .antMatchers("/VerEmpresas/**").hasRole("ADMIN")
                .antMatchers("/AgregarEmpresa").hasRole("ADMIN")
                .antMatchers("/GuardarEmpresa").hasRole("ADMIN")
                .antMatchers("/EditarEmpresa/{id}").hasRole("ADMIN")
                .antMatchers("/ActualizarEmpresa").hasRole("ADMIN")
                .antMatchers("/EliminarEmpresa/{id}").hasRole("ADMIN")

                //Empleados
                .antMatchers("/VerEmpleados/**").hasRole("ADMIN")
                .antMatchers("/AgregarEmpleado/**").hasRole("ADMIN")
                .antMatchers("/GuardarEmpleado/**").hasRole("ADMIN")
                .antMatchers("/EditarEmpleado/{id}/**").hasRole("ADMIN")
                .antMatchers("/ActualizarEmpleado/**").hasRole("ADMIN")
                .antMatchers("/EliminarEmpleado/{id}/**").hasRole("ADMIN")
                .antMatchers("/Empresa/{id}/Empleados/**").hasRole("ADMIN")

                //Movimientos
                .antMatchers("/VerMovimientos/**").hasAnyRole("ADMIN","USER")
                .antMatchers("/AgregarMovimiento/**").hasAnyRole("ADMIN","USER")
                .antMatchers("/GuardarMovimiento/**").hasAnyRole("ADMIN","USER")
                .antMatchers("/EditarMovimiento/**").hasRole("ADMIN")
                .antMatchers("/ActualizarMovimiento/**").hasRole("ADMIN")
                .antMatchers("/EliminarMovimiento/{id}/**").hasRole("ADMIN")
                .antMatchers("/Empleado/{id}/Movimientos/**").hasAnyRole("ADMIN","USER")
                .anyRequest().authenticated()
                .and().oauth2Login()
                .and().formLogin().successHandler(customSuccessHandler)
                .and().exceptionHandling().accessDeniedPage("/Denegado")
                .and().logout().permitAll()
                .and().exceptionHandling().accessDeniedPage("/Desabilitado")
                .and().logout().permitAll();


        return http.oauth2Login()
                .and().build();
    }
}

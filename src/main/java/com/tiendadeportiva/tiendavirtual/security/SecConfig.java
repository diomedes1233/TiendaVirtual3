package com.tiendadeportiva.tiendavirtual.security;


import com.tiendadeportiva.tiendavirtual.handler.CustomSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Autowired
    CustomSuccessHandler customSuccessHandler;

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception{
        auth.jdbcAuthentication().passwordEncoder(new BCryptPasswordEncoder())
                .dataSource(dataSource)
                .usersByUsernameQuery("select correo,password,estado from empleado where correo=?")
                .authoritiesByUsernameQuery("select correo, rol from empleado where correo=?");
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()

                .antMatchers("/","/VerEmpresas").hasRole("ADMIN")
                .antMatchers("/VerEmpresas/**").hasRole("ADMIN")
                .antMatchers("/GuardarEmpresa").hasRole("ADMIN")
                .antMatchers("/EditarEmpresa/{id}").hasRole("ADMIN")
                .antMatchers("/ActualizarEmpresa").hasRole("ADMIN")
                .antMatchers("/EliminarEmpresa/{id}").hasRole("ADMIN")


                .antMatchers("/VerEmpleados/**").hasRole("ADMIN")
                .antMatchers("/AgregarEmpleado/**").hasRole("ADMIN")
                .antMatchers("/GuardarEmpleado/**").hasRole("ADMIN")
                .antMatchers("/EditarEmpleado/{id}/**").hasRole("ADMIN")
                .antMatchers("/ActualizarEmpleado/**").hasRole("ADMIN")
                .antMatchers("/EliminarEmpleado/{id}/**").hasRole("ADMIN")
                .antMatchers("/Empresa/{id}/Empleados/**").hasRole("ADMIN")


                .antMatchers("/VerMovimientos/**").hasAnyRole("ADMIN","USER")
                .antMatchers("/AgregarMovimiento/**").hasAnyRole("ADMIN","USER")
                .antMatchers("/EditarMovimiento/**").hasAnyRole("ADMIN","USER")
                .and().formLogin().successHandler(customSuccessHandler)
                .and().exceptionHandling().accessDeniedPage("/Denegado")
                .and().logout().permitAll()
                .and().exceptionHandling().accessDeniedPage("/Desabilitado")
                .and().logout().permitAll();


    }
}

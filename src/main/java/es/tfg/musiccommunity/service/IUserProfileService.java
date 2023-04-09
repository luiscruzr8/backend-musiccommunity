package es.tfg.musiccommunity.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface IUserProfileService {

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}

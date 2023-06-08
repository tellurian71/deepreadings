package com.deepreadings.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;



public class ReaderDetails implements UserDetails {
	
	private static final long serialVersionUID = 20220226L;
	
	private Reader reader;
	
	public ReaderDetails(Reader reader) {
		this.reader = reader;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<Role> roles = reader.getRoles();
		
		List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
		for (Role role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		}
		return authorities;
	}

	@Override
	public String getPassword() {
		return reader.getPassword();
	}

	@Override
	public String getUsername() {
		return reader.getName();
	}
	
	public String getAlias() {
		return reader.getAlias();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return reader.isEnabled();
	}

}

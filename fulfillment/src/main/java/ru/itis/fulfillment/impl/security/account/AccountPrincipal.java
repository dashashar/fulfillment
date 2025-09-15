package ru.itis.fulfillment.impl.security.account;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.itis.fulfillment.impl.model.AccountEntity;

import java.util.Collection;
import java.util.Collections;

@Getter
public class AccountPrincipal implements UserDetails {

    private final long id;
    private final long telegramId;

    public AccountPrincipal(AccountEntity account) {
        this.id = account.getId();
        this.telegramId = account.getTelegramId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }
}

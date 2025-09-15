package ru.itis.fulfillment.impl.security.token;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import ru.itis.fulfillment.impl.security.account.AccountPrincipal;

@Getter
public class TelegramAuthentication extends AbstractAuthenticationToken {

    private final AccountPrincipal principal;

    public TelegramAuthentication(AccountPrincipal principal) {
        super(principal.getAuthorities());
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}

package api.lemonico.core.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Builder
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class LoginUser implements TokenDetail, UserDetails {

	private static final long serialVersionUID = 3993597711944310497L;
	private Integer userId;
	private String username;
	private String password;
	private String email;
	private Collection<? extends GrantedAuthority> authorities;
	private boolean enabled;
	private final static LoginUser loginUser = new LoginUser();

	// 开始定义 UserDetails 必要的属性，因为不打算启用这些限制条件，所以不对这些条件做限制，全部设置为 true （通过）
	private Boolean accountNonExpired;
	private Boolean accountNonLocked;
	private Boolean credentialsNonExpired;

	private LoginUser() {

	}

	public static LoginUser getLoginUserInstance() {
		return loginUser;
	}

	@Override
	public String getEmail() { return this.email; }

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public boolean isEnabled() {
		return this.getEnabled();
	}

	@JsonIgnore
	public Boolean getEnabled() {
		return this.enabled;
	}

	@Override
	public boolean isAccountNonExpired() {
		return this.getAccountNonExpired();
	}

	@JsonIgnore
	public boolean getAccountNonExpired() {
		return this.accountNonExpired;
	}
	@Override
	public boolean isAccountNonLocked() {
		return this.getAccountNonLocked();
	}

	@JsonIgnore
	public Boolean getAccountNonLocked() {
		return this.accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return this.getCredentialsNonExpired();
	}

	@JsonIgnore
	public Boolean getCredentialsNonExpired() {
		return this.credentialsNonExpired;
	}

}

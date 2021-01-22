package api.lemonico.entity;

import lombok.extern.slf4j.Slf4j;
import org.seasar.doma.jdbc.entity.EntityListener;
import org.seasar.doma.jdbc.entity.PreInsertContext;
import org.seasar.doma.jdbc.entity.PreUpdateContext;
import org.slf4j.MDC;

import java.time.LocalDateTime;

@Slf4j
public class BaseListener<T extends BaseEntity> implements EntityListener<T> {

	/**
	 * ユーザID Key
	 */
	public static final String USER_ID_KEY = "userId";

	/**
	 * デフォルトのユーザID
	 */
	private static final String DEFAULT_USER_ID = "init";

	private static String getUserId() {
		String userId = MDC.get(USER_ID_KEY);
		if (userId == null) {
			log.warn("UserId is NULL. key={}", USER_ID_KEY);
			return DEFAULT_USER_ID;
		} else {
			return userId;
		}
	}

	public BaseListener() {
	}

	@Override
	public void preInsert(T entity, PreInsertContext<T> context) {
		String userId = BaseListener.getUserId();
		LocalDateTime now = LocalDateTime.now();

		entity.setCreateBy(userId);
		entity.setCreateTime(now);
		entity.setUpdateBy(userId);
		entity.setUpdateTime(now);
		entity.setIsActived(1);
	}

	@Override
	public void preUpdate(T entity, PreUpdateContext<T> context) {
		String userId = BaseListener.getUserId();

		entity.setUpdateBy(userId);
		entity.setUpdateTime(LocalDateTime.now());
	}
}

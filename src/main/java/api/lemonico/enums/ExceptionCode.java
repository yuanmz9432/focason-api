package api.lemonico.enums;

public enum ExceptionCode implements CodeEnum<String>{

	USER_FORBIDDEN("E9001", "黑名单"),
	WRONG_PASSWORD("E9003", "密码错误"),
	USER_NOT_EXIST("E9002", "用户不存在");

	ExceptionCode(String code, String message) {
		this.code = code;
		this.message = message;
	}
	
	private String code;
	
	private String message;
	
	public String getValue() {
		return code;
	}
	
	public String getLabel() {
		return message;
	}
	
	public static ExceptionCode of(String code) {
		return CodeEnum.of(ExceptionCode.class, code);
	}
}

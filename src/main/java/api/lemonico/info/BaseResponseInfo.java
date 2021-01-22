package api.lemonico.info;

import java.io.Serializable;

import api.lemonico.enums.ResponseCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BaseResponseInfo implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -1832122505105038646L;

	private Integer code;

	private String message;

	private Object data;

	public BaseResponseInfo() {
	}

	public static BaseResponseInfo success() {
		BaseResponseInfo baseResponse = new BaseResponseInfo();
		baseResponse.setResultCode(ResponseCode.SUCCESS);
		return baseResponse;
	}

	public static BaseResponseInfo success(Object data) {
		BaseResponseInfo BaseResponse = new BaseResponseInfo();
		BaseResponse.setResultCode(ResponseCode.SUCCESS);
		BaseResponse.setData(data);
		return BaseResponse;
	}

	public static BaseResponseInfo success(ResponseCode responseCode, Object data) {
		BaseResponseInfo BaseResponse = new BaseResponseInfo();
		BaseResponse.setResultCode(responseCode);
		BaseResponse.setData(data);
		return BaseResponse;
	}

	public static BaseResponseInfo failure(ResponseCode resultCode) {
		BaseResponseInfo BaseResponse = new BaseResponseInfo();
		BaseResponse.setResultCode(resultCode);
		return BaseResponse;
	}

	public void setResultCode(ResponseCode resultCode) {
		this.code = resultCode.getValue();
		this.message = resultCode.getLabel();
	}

}

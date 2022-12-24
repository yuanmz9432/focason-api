package com.lemonico.core.annotation;



import com.alibaba.fastjson.JSON;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.exception.LcBadRequestException;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.apiLimit.GetIp;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * リクエスト上限制御インターセプター
 *
 * @since 1.0.0
 */
@Component
public class RequestLimitInterceptor implements HandlerInterceptor
{

    /**
     * コントローラー前に行いたい共通処理
     *
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @param handler ハンドラー
     * @return 上限になる場合、falseを返す。逆に、trueを返す
     * @throws Exception 異常
     */
    @Override
    public boolean preHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, Object handler)
        throws Exception {
        if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            // 获取方法中的注解 看有没有改注解
            // メソッドの中のコメントを取得する。コメントを変更したかどうかを確認する
            RequestLimit annotation = method.getAnnotation(RequestLimit.class);
            RequestLimit classAnnotation = method.getDeclaringClass().getAnnotation(RequestLimit.class);
            RequestLimit requestLimit = annotation != null ? annotation : classAnnotation;
            if (requestLimit != null) {
                // 有该注解，判断访问的次数
                // このコメントがあり、アクセスの回数を判断する
                if (isLimit(request, requestLimit)) {
                    throw new LcBadRequestException("請求頻度は高いすぎて、後ほどアクセスしてください。");
                }
            }
        }
        return true;
    }

    /**
     * インタフェースアクセスの回数を判断する
     *
     * @param request {@link HttpServletRequest}
     * @param requestLimit {@link RequestLimit}
     * @return 上限になる場合、falseを返す。逆に、trueを返す
     * @throws ParseException 異常
     */
    public boolean isLimit(HttpServletRequest request, RequestLimit requestLimit) throws ParseException {
        HttpSession session = request.getSession();
        // session 失効時間は60sである
        session.setMaxInactiveInterval(60);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String format = simpleDateFormat.format(new Date());
        String requestURL = String.valueOf(request.getRequestURL());
        // 訪問する url と ip をつなぎ合わせる
        String key = requestURL + "/" + GetIp.getIp(request);
        String attribute = (String) session.getAttribute(key);
        if (attribute == null) {
            // 初回訪問 訪問時間 + 訪問回数1
            String value = format + "-" + 1;
            session.setAttribute(key, value);
            return false;
        }
        String[] split = attribute.split("-");
        long start = simpleDateFormat.parse(split[0]).getTime();
        long end = simpleDateFormat.parse(simpleDateFormat.format(new Date())).getTime();
        int l = (int) ((end - start) / (1000));
        // アクセス訪問間隔時間が設定値を超えているかどうかを判断する。
        if (l > requestLimit.second()) {
            session.removeAttribute(key);
            // 設定値を超える 新しいアクセスを作成する 回数は1
            String value = format + "-" + 1;
            session.setAttribute(key, value);
            return false;
        } else {
            int times = Integer.parseInt(split[1]);
            // 访问次数 +1
            // 訪問回数+1
            String newValue = split[0] + "-" + (times + 1);
            // 访问次数 大于 最大次数
            // アクセス回数が最大回数より大きい
            if ((times + 1) > requestLimit.maxCount()) {
                return true;
            }
            session.setAttribute(key, newValue);
        }
        return false;
    }

    /**
     * @Param: response
     * @description: 相応する内容
     * @return: void
     * @date: 2020/05/19
     */
    private void result(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        // 文字セットを text/json に設定する
        response.setContentType("text/json; charset=utf-8");
        PrintWriter out = response.getWriter();
        String s = JSON.toJSON(CommonUtils.failure(ErrorCode.TOO_MANY_REQUESTS)).toString();
        out.write(s);
        out.flush();
        // ストリームのクローズ
        out.close();
    }

}

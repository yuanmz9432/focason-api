package com.lemonico.core.utils.apiLimit;



import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.servlet.http.HttpServletRequest;

/**
 * @className: GetIp
 * @description: IPアドレスを取得する
 * @date: 2020/05/19 12:47
 **/
public class GetIp
{

    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !"unKnown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    /**
     * @Description: 获取公网Ip
     *               @Date： 2021/6/21
     * @Param：
     * @return：
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.trim().isEmpty() && !"unknown".equalsIgnoreCase(ip.trim())) {
            return ip.split(",", 2)[0].trim();
        }
        return "127.0.0.1";

    }


    public static String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * Gets the real ips.
     *
     * @return the real ips
     */
    public static boolean getRequestHeadersInMap(HttpServletRequest request) throws UnknownHostException {

        InetAddress IP = InetAddress.getLocalHost();
        System.out.println("IP of my system is := " + IP.getHostAddress());
        InetAddress iAddress = InetAddress.getLocalHost();
        return false;
    }
}

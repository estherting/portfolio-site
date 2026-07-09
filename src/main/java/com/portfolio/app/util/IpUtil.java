package com.portfolio.app.util;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtil {

    private IpUtil() {}

    /**
     * Best-effort client IP resolution. Note: IP-based tracking is imperfect —
     * shared networks (offices, universities, mobile carriers) and VPNs can
     * cause false positives/negatives — but it's a reasonable, low-friction
     * way to discourage repeat voting without requiring user accounts.
     */
    public static String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }
}
